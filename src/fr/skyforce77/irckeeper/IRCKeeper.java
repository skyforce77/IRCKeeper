package fr.skyforce77.irckeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCEventListener;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;

public class IRCKeeper extends IRCEventAdapter implements IRCEventListener{

	private IRCConnection manager;
	private ArrayList<String> connected = new ArrayList<>();
	private ArrayList<String> admins = new ArrayList<>();
	private boolean registered = false;

	public IRCKeeper() {
		manager = new IRCConnection("irc.quakenet.org", new int[]{6667, 6668, 6669}, null, "Geeklink", "GeeklinkFR-SU", "geeklinkfr@gmail.com");
		manager.setDaemon(true);
		manager.addIRCEventListener(this);
		manager.setColors(false); 
		manager.setPong(true);
		try {
			System.out.println("connecting");
			manager.connect();
			
			new Thread("Connect") {
				@Override
				public void run() {
					while(!registered) {
						try {
							Thread.sleep(100l);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					while(getChannels().exists()) {
						try {
							BufferedReader fr = new BufferedReader(new FileReader(getChannels()));
							String s = fr.readLine();
							while(s != null) {
								if(!connected.contains(s)) {
									manager.doJoin("#"+s);
									Thread.sleep(1000l);
									System.out.println("Joined #"+s);
									connected.add(s);
								}
								s = fr.readLine();
							}
							fr.close();

							if(getAdmins().exists()) {
								fr = new BufferedReader(new FileReader(getAdmins()));
								s = fr.readLine();
								while(s != null) {
									if(!admins.contains(s)) {
										admins.add(s);
									}
									s = fr.readLine();
								}
								fr.close();
							}
						} catch(Exception e) {}
					}
					try {
						Thread.sleep(600000l);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new Thread("TerminalHandler") {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                    String line = null;

                    try {
                        line = br.readLine();
                        manager.send(line);
                    } catch (IOException ioe) {
                        System.out.println("IO error trying to read command!");
                    }
                }
            }
        }.start();
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
		new IRCKeeper();
	}

	public void onDisconnected() {
		System.out.println("Disconnected");
	}

	public void onError(String msg) {
		System.out.println("Error: "+ msg);
	}

	public void onError(int num, String msg) {
		System.out.println("Error #"+ num +": "+ msg);
	}

	public void onInvite(String chan, IRCUser u, String nickPass) {
		System.out.println(chan +"> "+ u.getNick() +" invites "+ nickPass);
	}

	public void onJoin(String chan, IRCUser u) {
		System.out.println(chan +"> "+ u.getNick() +" joins");
		if(admins.contains(u.getUsername())) {
			System.out.println("["+chan+"] Opping "+u.getUsername());
			manager.send("mode "+chan+" +o "+u.getNick());
		}
	}

	public void onKick(String chan, IRCUser u, String nickPass, String msg) {
		System.out.println(chan +"> "+ u.getNick() +" kicks "+ nickPass);
	}

	public void onMode(IRCUser u, String nickPass, String mode) {
		System.out.println("Mode: "+ u.getNick() +" sets modes "+ mode +" "+ 
				nickPass);
	}

	public void onMode(IRCUser u, String chan, IRCModeParser mp) {
		System.out.println(chan +"> "+ u.getNick() +" sets mode: "+ mp.getLine());
	}

	public void onNick(IRCUser u, String nickNew) {
		System.out.println("Nick: "+ u.getNick() +" is now known as "+ nickNew);
	}

	public void onNotice(String target, IRCUser u, String msg) {
		System.out.println(target +"> "+ u.getNick() +" (notice): "+ msg);
	}

	public void onPart(String chan, IRCUser u, String msg) {
		System.out.println(chan +"> "+ u.getNick() +" parts");
	}

	public void onPrivmsg(String chan, IRCUser u, String msg) {
		System.out.println(chan +"> "+ u.getNick() +": "+ msg);
	}

	public void onQuit(IRCUser u, String msg) {
		System.out.println("Quit: "+ u.getNick());
	}

	public void onReply(int num, String value, String msg) {
		if(num == 1) {
			registered = true;
		}
		System.out.println("Reply #"+ num +": "+ value +" "+ msg);
	}

	public void onTopic(String chan, IRCUser u, String topic) {
		System.out.println(chan +"> "+ u.getNick() +" changes topic into: "+ topic);
	}
}
