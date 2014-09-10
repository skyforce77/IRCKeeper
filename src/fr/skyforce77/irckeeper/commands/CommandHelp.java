package fr.skyforce77.irckeeper.commands;

import java.util.ArrayList;

import fr.skyforce77.irckeeper.commands.Argument.ArgumentType;

public class CommandHelp extends Command {

	int pagesnumber = 0;
	ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
	
	@Override
	public void onTyped(String[] args) {
		update();
		if(args.length == 1 && args[0].equals("")) {
			sendPage(0);
		} else if(args.length == 1 && !args[0].equals("") && isNumber(args[0])) {
			sendPage(Integer.parseInt(args[0])-1);
		}
	}
	
	public void sendPage(int number) {
		if(number > pagesnumber) {
			System.out.println("Page #"+(number+1)+" do not exists");
			return;
		}
		System.out.println("<Commands> Page: "+(number+1)+"/"+(pagesnumber+1));
		for(String command : pages.get(number)) {
			Command c = CommandManager.getCommand(command);
			if(c.getUse() == null) {
				System.out.println("- "+command);
			} else {
				System.out.println("- "+c.getUse());
			}
		}
	}
	
	public void update() {
		int i = 1;
		pagesnumber = 0;
		pages = new ArrayList<ArrayList<String>>();
		ArrayList<String> commands = new ArrayList<String>();
		for(String command : CommandManager.getCommands()) {
			if(i < 8) {
				commands.add(command);
				i++;
			} else {
				pages.add(commands);
				commands = new ArrayList<String>();
				commands.add(command);
				i = 2;
				pagesnumber++;
			}
		}
		if(i > 1) {
			pages.add(commands);
		}
	}
	
	@Override
	public void onInitialized(String label) {
		setArguments(new Argument("page", ArgumentType.Integer, true));
	}
	
	@Override
	public boolean isCorrect(String[] args) {
		if(args.length == 1 && args[0].equals("")) {
			return true;
		} else if(args.length == 1 && !args[0].equals("") && isNumber(args[0])) {
			return true;
		}
		return false;
	}

}
