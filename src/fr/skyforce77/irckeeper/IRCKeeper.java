package fr.skyforce77.irckeeper;

import fr.skyforce77.irckeeper.commands.CommandManager;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.JoinEvent;
import jerklib.events.MessageEvent;
import jerklib.events.QuitEvent;
import jerklib.listeners.IRCEventListener;

public class IRCKeeper implements IRCEventListener{

	private ConnectionManager manager;
	
	public IRCKeeper(String name) {
		manager = new ConnectionManager(new Profile(name));
		Session session = manager.requestConnection("irc.esper.net");
		session.addIRCEventListener(this);
	}

	@Override
	public void receiveEvent(IRCEvent e) {
		if (e.getType() == Type.CONNECT_COMPLETE)
		{
			e.getSession().join("#cryptyrc");
			System.out.println("Successfuly connected.");
		}
		else if (e.getType() == Type.CHANNEL_MESSAGE)
		{
			MessageEvent me = (MessageEvent) e;
			System.out.println("<" + me.getNick() + "> " + me.getMessage());
		}
		else if (e.getType() == Type.PRIVATE_MESSAGE)
		{
			MessageEvent me = (MessageEvent) e;
			System.out.println("[" + me.getNick() + "] " + me.getMessage());
		}
		else if (e.getType() == Type.JOIN)
		{
			JoinEvent je = (JoinEvent) e;
			System.out.println(je.getNick()+" ("+je.getUserName()+") joined ");
			System.out.println(je.getUserName());
			if(je.getUserName().equals("~skyforce7")) {
				System.out.println("Opping "+je.getNick());
				je.getChannel().op(je.getNick());
			}
		}
		else if (e.getType() == Type.QUIT)
		{
			QuitEvent qe = (QuitEvent) e;
			System.out.println(qe.getUserName()+" left ("+qe.getQuitMessage()+")");
		}
	}
	
	public static void main(String[] args) {
		new IRCKeeper("skyforce77-PI");
		CommandManager.createCommands();
		CommandManager.createTerminal();
	}
}
