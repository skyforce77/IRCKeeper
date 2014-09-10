package fr.skyforce77.irckeeper.commands;


public class CommandStop extends Command {
	
	@Override
	public void onTyped(String[] args) {
		System.exit(0);
	}

}
