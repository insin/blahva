package test;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import spark.Request;
import spark.Response;
import spark.Route;
import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;

public class TestServer {
	/** jade4j configuration and cache. */
	static final JadeConfiguration jade = new JadeConfiguration();

	/** JSR 303 Bean Validator. */
	static final Validator validator =
			Validation.buildDefaultValidatorFactory().getValidator();

	/** In-memory list of Things, for testing. */
	static final List<Thing> things = new ArrayList<Thing>();

	public static void main(String[] args) throws IOException {
		// Configure jade4j
		jade.setTemplateLoader(new FileTemplateLoader("templates/", "UTF-8"));
		jade.setMode(Jade4J.Mode.HTML);
		jade.setPrettyPrint(true);

		// Context variables which will be available in all templates - we're
		// using constant-style names to distinguish them from template
		// variables and rendering context variables.
		Map<String, Object> defaults = new HashMap<String, Object>();
		defaults.put("APP_NAME", "jade4j & Spark Test");
		defaults.put("NAV_BAR_ITEMS", new NavBarItem[] {
			new NavBarItem("/test", "Test Page", "test"),
		    new NavBarItem("/things", "Things", "things")
		});
		jade.setSharedVariables(defaults);

		// Route mappings & handlers
		get(new Route("/") {
			public Object handle(Request request, Response response) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("message", "Hello World");
				return render("index", context);
			}
		});

		get(new Route("/test") {
			public Object handle(Request request, Response response) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("headings", new String[] { "One", "Two", "Three" });
				context.put("rows", new String[][] {
					new String[] { "1", "2", "3" },
					new String[] { "4", "5", "6" },
					new String[] { "7", "8", "9" }
				});
				return render("test", context);
			}
		});

		get(new Route("/things") {
			public Object handle(Request request, Response response) {
				return render("things/list", "things", things);
			}
		});

		get(new Route("/things/add") {
			public Object handle(Request request, Response response) {
				return render("things/form", "thing", new Thing());
			}
		});

		post(new Route("/things/add") {
			public Object handle(Request request, Response response) {
				Thing thing = new Thing().name(request.queryParams("name"))
						                 .description(request.queryParams("description"));
				Set<ConstraintViolation<Thing>> errors = validator.validate(thing);
				if (errors.isEmpty()) {
					things.add(thing);
					response.redirect("/things");
					return null;
				}
				return render("things/form", "thing", thing, "errors", mapErrors(errors));
			}
		});
	}

	/**
	 * Convenience method for rendering a template by name (without .jade
	 * extension).
	 */
	static String render(String template) {
		return render(template, new HashMap<String, Object>());
	}

	/**
	 * Convenience method for rendering a template by name (without .jade
	 * extension) and some context variables.
	 */
	static String render(String template, Map<String, Object> context) {
		try {
			JadeTemplate jadeTemplate = jade.getTemplate(template);
			return jade.renderTemplate(jadeTemplate, context);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Convenience method for rendering a template by name (without .jade
	 * extension) and some context variables pass as varargs as pairs of
	 * <name> <value> arguments.
	 */
	static String render(String template, Object... contextArgs) {
		assert contextArgs.length >=2 && contextArgs.length % 2 == 0;
		Map<String, Object> context = new HashMap<String, Object>();
		for (int i = 0; i < contextArgs.length; i += 2) {
			context.put((String)contextArgs[i], contextArgs[i + 1]);
		}
		return render(template, context);
	}

	/**
	 * Creates a Map from a Set of ConstraintViolations, keyed with property
	 * paths, with errors messages held in a list.
	 */
	static Map<String, List<String>> mapErrors(Set<? extends ConstraintViolation<?>> errorSet) {
		 Map<String, List<String>> errors = new HashMap<String, List<String>>();
		 for (ConstraintViolation<?> error: errorSet) {
			 String field = error.getPropertyPath().toString();
			 if (!errors.containsKey(field)) {
				 errors.put(field, new ArrayList<String>());
			 }
			 errors.get(field).add(error.getMessage());
		 }
		 return errors;
	}
}
