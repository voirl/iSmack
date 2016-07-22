package cuj.DemoOfSmack.Client;
/*
*
* project_name: DemoOfSmack
* class_name: 
* class_description: 
* author: cujamin
* create_time: 2016年7月9日
* modifier: 
* modify_time: 
* modify_description: 
* @version
*
 */



import java.util.Scanner;

import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import cuj.DemoOfSmack.Config.SmackConfig;
import cuj.DemoOfSmack.Connector.SmackConnector;

public class ChatClient {


	static public SmackConfig chatConfig = null;
	static public SmackConnector chatConnector =null;

	static public void start()
	{
		chatConfig = new SmackConfig("cujamin-pc" , "localhost" , 5222 );
		chatConnector = new SmackConnector(chatConfig);
		//连接
		chatConnector.connect();
		chatConnector.logIn("cuj1","1");

	}	
}