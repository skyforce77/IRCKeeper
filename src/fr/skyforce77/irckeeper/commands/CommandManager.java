package fr.skyforce77.irckeeper.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

public class CommandManager {

	private static HashMap<String, Command> commands = new HashMap<String, Command>();
	private static HashMap<String, String> alias = new HashMap<String, String>();

	public static void createCommands() {
		register("help", new CommandHelp());
		register("stop", new CommandStop());
	}

	public static void register(String label, Command command) {
		commands.put(label, command);
		command.label = label;
		command.onInitialized(label);

		for (String s : command.getAlias()) {
			registerAlias(label, s);
		}
	}

	public static void registerAlias(String label, String alia) {
		alias.put(alia, label);
	}

	public static boolean isCommand(String label) {
		if (commands.containsKey(label)) {
			return true;
		}
		if (alias.containsKey(label)) {
			return true;
		}
		return false;
	}

	public static Command getCommand(String label) {
		if (isCommand(label)) {
			if (commands.containsKey(label)) {
				return commands.get(label);
			}
			if (alias.containsKey(label)) {
				return commands.get(alias.get(label));
			}
		} else {
			return null;
		}
		return null;
	}

	public static Set<String> getCommands() {
		return commands.keySet();
	}

	public static void onCommandTyped(String label, String[] args) {
		String s = label;
		for (String a : args) {
			s = s + " " + a;
		}
		if (isCommand(label)) {
			if (getCommand(label).isCorrect(args)) {
				try {
					getCommand(label).onTyped(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (getCommand(label).getUse() != null) {
					System.out.println("Incorrect arguments. Usage: /"+getCommand(label).getUse());
				} else {
					System.out.println("Incorrect arguments. Usage: /"+label);
				}
			}
		} else {
			System.out.println("Unknown command, type /help for help");
		}
	}
	
	public static void createTerminal() {
        new Thread("TerminalHandler") {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                    String line = null;

                    try {
                        line = br.readLine();
                    } catch (IOException ioe) {
                        System.out.println("IO error trying to read command!");
                    }

                    String label;
                    if (line.contains(" ")) {
                        label = line.split(" ")[0];
                    } else {
                        label = line;
                    }

                    String[] args;
                    if (line.contains(" ")) {
                        args = line.replace(label, "").split(" ");
                    } else {
                        args = new String[]{""};
                    }

                    onCommandTyped(label, args);
                }
            }
        }.start();
    }

}
