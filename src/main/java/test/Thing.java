package test;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class Thing {
	@NotBlank
	@Length(max=20)
	private String name;

	@Length(max=500)
	private String description;

	public Thing() {
	}

	// ---------------------------------------------------- Chaining Setters ---

	public Thing name(String name) {
		this.setName(name);
		return this;
	}

	public Thing description(String description) {
		this.setDescription(description);
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			name = "";
		}
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description == null) {
			description = "";
		}
		this.description = description;
	}
}
