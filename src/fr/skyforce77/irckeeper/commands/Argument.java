package fr.skyforce77.irckeeper.commands;



public class Argument {

	private String name;
	private boolean optional;
	private ArgumentType type = ArgumentType.String;
	
	public Argument(String name, ArgumentType type) {
		this.name = name;
		this.optional = false;
		this.type = type;
	}
	
	public Argument(String name, ArgumentType type, boolean optional) {
		this.name = name;
		this.optional = optional;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isOptional() {
		return optional;
	}
	
	public String getRender() {
		return name+"("+type.getDisplay()+")";
	}
	
	public ArgumentType getType() {
		return type;
	}
	
	public static class ArgumentType {
		
		public static ArgumentType String = new ArgumentType("text");
		public static ArgumentType Url = new ArgumentType("url");
		public static ArgumentType Integer = new ArgumentType("number");
		public static ArgumentType Boolean = new ArgumentType("true/false");
		
		private String display;
		
		public ArgumentType(String display) {
			this.display = display;
		}
		
		public String getDisplay() {
			return display;
		}
	}
}
