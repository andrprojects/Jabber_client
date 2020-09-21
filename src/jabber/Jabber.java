package jabber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

public class Jabber {

	private static final int JABBER_PORT = 5222;
	private Socket socket;
	public String hostname = "";
	private BufferedReader rx = null;
	private PrintWriter tx = null;
	private boolean connected = false;

	public static final int CONNECT_OK = 0;
	public static final int CONNECT_STREAMERROR = 1;
	public static final int CONNECT_INITERROR = 2;

	public Jabber() {
	}

	public int connect(String hostname) throws UnknownHostException {
		// Open the basic socket to the server.
		try {
			socket = new Socket(hostname, JABBER_PORT);
		} catch (IOException e) {
			return (CONNECT_STREAMERROR);
		}

		// Now open up the rx and tx streams
		try {
			rx = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			return (CONNECT_STREAMERROR);
		}

		try {
			tx = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			return (CONNECT_STREAMERROR);
		}

		connected = true;
		this.hostname = hostname;

		return (CONNECT_OK);
	}

	public void sendText(String msg) {
		if (!connected)
			return;
		tx.print(msg);
		tx.flush();
	}

	public void disconnect() throws IOException {
		if ((socket == null) || (tx == null) || (rx == null))
			return;

		tx.print("</stream:stream>");
		tx.flush();
		rx.close();
		socket.close();
	}

	public static void main(String[] args_0) throws IOException, SmackException, XMPPException, InterruptedException {
//		Jabber jab = new Jabber();
//		jab.connect("10.0.11.7");
//		jab.sendText("<?xml version='1.0'?>"+
//					"<stream:stream"+
//					"to='10.0.11.7'"+
//					"xmlns='jabber:client'"+
//					"xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>"+
//					"</stream:stream>"
//					);
//		System.out.println(jab.rx.readLine());
//		jab.disconnect();

		Builder config = XMPPTCPConnectionConfiguration.builder();
		config.setUsernameAndPassword("andrey", "gosha16");
//		config.setXmppDomain("10.0.11.7");
//		config.setHost("10.0.11.7");

		AbstractXMPPConnection connection = new XMPPTCPConnection(config.build());
		connection.connect(); // Establishes a connection to the server
		connection.login(); // Logs in
		
		ChatManager chatManager = ChatManager.getInstanceFor(connection);
		EntityBareJid jid = JidCreate.entityBareFrom("baeldung2@jabb3r.org");
		Chat chat = chatManager.chatWith(jid);
		
		chat.send("Hello!");
		
		chatManager.addIncomingListener(new IncomingChatMessageListener() {
			  @Override
			  public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
			      System.out.println("New message from " + from + ": " + message.getBody());
			  }
			});
	}

}
