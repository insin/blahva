package test;

public class NavBarItem {
	private String url;
	private String text;
	/**
	 * Unique identifier for this item, used in templates to mark the nav bar
	 * link as active, and as a body class.
	 */
	private String name;

	public NavBarItem(String url, String text, String name) {
		this.url = url;
		this.text = text;
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public String getText() {
		return text;
	}

	public String getName() {
		return name;
	}
}
