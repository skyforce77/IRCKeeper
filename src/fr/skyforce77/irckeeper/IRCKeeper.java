package fr.skyforce77.irckeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.JoinEvent;
import jerklib.events.MessageEvent;
import jerklib.events.QuitEvent;
import jerklib.listeners.IRCEventListener;
import fr.skyforce77.irckeeper.commands.CommandManager;

public class IRCKeeper implements IRCEventListener{

	private ConnectionManager manager;
	private ArrayList<String> connected = new ArrayList<>();

	public IRCKeeper(String name) {
		manager = new ConnectionManager(new Profile(name));
		Session session = manager.requestConnection("irc.quakenet.org");
		session.addIRCEventListener(this);
	}

	@Override
	public void receiveEvent(final IRCEvent e) {
		if (e.getType() == Type.CONNECT_COMPLETE) {
			new Thread("Connect") {
				@Override
				public void run() {
					while(getChannels().exists()) {
						try {
							BufferedReader fr = new BufferedReader(new FileReader(getChannels()));
							String s = fr.readLine();
							while(s != null) {
								if(!connected.contains(s)) {
									e.getSession().join("#"+s);
									System.out.println("Join #"+s);
									connected.add(s);
								}
								s = fr.readLine();
							}
							fr.close();
						} catch(Exception e) {}
					}
					try {
						Thread.sleep(600000l);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
			System.out.println("Successfuly connected.");
		}
		else if (e.getType() == Type.CHANNEL_MESSAGE) {
			MessageEvent me = (MessageEvent) e;
			System.out.println("<" + me.getNick() + "> " + me.getMessage());
		}
		else if (e.getType() == Type.PRIVATE_MESSAGE) {
			MessageEvent me = (MessageEvent) e;
			System.out.println("[" + me.getNick() + "] " + me.getMessage());
		}
		else if (e.getType() == Type.JOIN) {
			JoinEvent je = (JoinEvent) e;
			System.out.println(je.getNick()+" ("+je.getUserName()+") joined ");
			System.out.println(je.getUserName());
			if(getAdmins().exists()) {
				try {
					BufferedReader fr = new BufferedReader(new FileReader(getAdmins()));
					String s = fr.readLine();
					while(s != null) {
						if(je.getUserName().equals(s)) {
							System.out.println("Opping "+je.getNick());
							je.getChannel().op(je.getNick());
						}
						s = fr.readLine();
					}
					fr.close();
				} catch(Exception ex) {}
			}
		}
		else if (e.getType() == Type.QUIT) {
			QuitEvent qe = (QuitEvent) e;
			System.out.println(qe.getUserName()+" left ("+qe.getQuitMessage()+")");
		}
	}

	public static File getDirectory() {
		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN"))
			return new File(System.getenv("APPDATA"),"/.irckeeper");
		else if (OS.contains("MAC"))
			return new File(System.getProperty("user.home") + "/Library/Application "
					+ "Support","/.irckeeper");
		else if (OS.contains("NUX"))
			return new File(System.getProperty("user.home"),"/.irckeeper");
		return new File(System.getProperty("user.dir"),"/.irckeeper");
	}

	public static File getAdmins() {
		return new File(getDirectory(), "/admins");
	}

	public static File getChannels() {
		return new File(getDirectory(), "/channels");
	}

	public static void main(String[] args) {
		new IRCKeeper("GeeklinkFr-SU");
		CommandManager.createCommands();
		CommandManager.createTerminal();
	}
}
