package cuj.DemoOfSmack.Connector;
/*
*
* project_name: DemoOfSmack
* class_name: 
* class_description: 
* author: cujamin
* create_time: 2016年7月13日
* modifier: 
* modify_time: 
* modify_description: 
* @version
*
 */

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.search.UserSearchManager;

import cuj.DemoOfSmack.Client.ChatClient;
import cuj.DemoOfSmack.Config.SmackConfig;

public class SmackConnector {
	static private SmackConfig smackConfig= null;
	static public XMPPTCPConnection connection = null;
	static public Presence presence = null;//状态
	static Roster roster = null;//花名册
//	static public RosterThread rosterThread ;
	
	static private ReceiverThread receiverThread ;
	static private SetMassageThread setMassageThread;
	
	public SmackConnector(SmackConfig smackConfig)
	{
		this.smackConfig = smackConfig;
		//配置需要连接的服务器信息	
	}
	public void connect()
	{
		XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
		configBuilder.setServiceName( smackConfig.getServiceName() );
		configBuilder.setHost( smackConfig.getHost() );
		configBuilder.setPort( smackConfig.getPort() );
		configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		configBuilder.setCustomSSLContext(null);//SSL
		configBuilder.setCompressionEnabled(false);//压缩？
		
		XMPPTCPConnectionConfiguration config = configBuilder.build();

		connection =new XMPPTCPConnection(config);
		try {
			//连接服务器
			connection.connect();
            System.out.println("connect is success");
            //登录服务器(却输入账号密码)****************************
        } catch (SmackException e) {
            System.out.println("************1con");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("************2con");
            e.printStackTrace();
        } catch (XMPPException e) {
            System.out.println("************3con");
            e.printStackTrace();
        }
		
	}
	//登录:用户名user，密码password
	public void logIn(String userName , String passWord)
	{
		try {
			connection.login(userName , passWord);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//自身状态
		presence = getPresence(connection);
		
//		rosterThread = new RosterThread();
//		rosterThread.start();
		//花名册
		roster = getRoster(connection);
		setRosterListener();
		
		//接收消息线程
		receiverThread = new ReceiverThread();
		receiverThread.start();
		
		//发送消息线程
		setMassageThread = new SetMassageThread();
		setMassageThread.start();
		
	}
	
	//注册账号
	public static void registerUser(String username , String password ,XMPPTCPConnection connection) throws XMPPErrorException
	{	
		try {
			
			 AccountManager.sensitiveOperationOverInsecureConnectionDefault(true);  
	         AccountManager.getInstance(connection).createAccount(username, password);  
	         System.out.println("注册成功!");  
		} catch (NoResponseException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("注册失败");
		} catch (XMPPErrorException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	//设置当前状态
	public Presence getPresence(XMPPTCPConnection connection)
	{
		Presence presence = new Presence(Presence.Type.available);  
        presence.setStatus("X");
        try {
       	 connection.sendPacket(presence);
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        return presence;
	}
	//设置花名册
	public Roster getRoster(XMPPTCPConnection connection)
	{
		Roster roster = Roster.getInstanceFor(SmackConnector.connection);
      	
      	return roster;
	}
	//设置花名册监听
	public void setRosterListener()
	{
		roster.addRosterListener(new RosterListener(){
			int a=0;//登录时和有加好友请求时，都会调用entriesAdded，用a在区分”登录“和”有加好友请求“两种情况
      		@Override
   			public void entriesAdded(Collection<String> addresses) {
      			if(a==0)
      			{
      				for(String friend : addresses)
          			{
          				System.out.println("好友 : "+friend);
          			}
      				a=1;
      			}
      			else
      			{
      				String name = (String) addresses.toString().subSequence(1, addresses.toString().length()-1);
          			System.out.println(name);
          			addFriends(name,name);
      			}
      		}
   			@Override
   			public void entriesUpdated(Collection<String> addresses) {
   				System.out.println("更新好友 ： "+addresses);
   			}
   			@Override
   			public void entriesDeleted(Collection<String> addresses) {
   				System.out.println("删除好友： "+addresses);
   			}
   			@Override
   			public void presenceChanged(Presence presence) {
   				System.out.println(presence.getTo()+"  的好友:  "+presence.getFrom() +"   状态为 ：  "+ presence.getType());
   				if(presence.getType().equals(Presence.Type.unavailable))
   				{
   					//TODO
   					System.out.println("好友"+presence.getFrom()+"掉线");
   				}
   			}	 
      	 });
	}
	//添加好友
	static public void addFriends(String friendjid,String name)
	 {
		 try {
			roster.createEntry(friendjid, name, new String[]{"Friends"});
		} catch (NotLoggedInException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	//删除好友
	static public void delFriends(String friendjid)
	{
		try {
			roster.removeEntry(roster.getEntry(friendjid));
		} catch (NotLoggedInException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

////监听花名册线程
//class RosterThread extends Thread
//{
//	static Roster roster = null;
//	public void run()
//	{
//		System.out.println("RosterThread is started");
//		roster = Roster.getInstanceFor(SmackConnector.connection);
//      	roster.addRosterListener(new RosterListener(){
//      		@Override
//   			public void entriesAdded(Collection<String> addresses) {
//      			System.out.println("好友列表  ： "+addresses);
//      		}
//   			@Override
//   			public void entriesUpdated(Collection<String> addresses) {
//   				System.out.println("更新好友 ： "+addresses);
//   			}
//   			@Override
//   			public void entriesDeleted(Collection<String> addresses) {
//   				System.out.println("删除好友： "+addresses);
//   			}
//   			@Override
//   			public void presenceChanged(Presence presence) {
//   				System.out.println(presence.getTo()+"  的好友:  "+presence.getFrom() +"   状态为 ：  "+ presence.getType());
//   			}	 
//      	 });
//	}
//}
//接受消息线程
class ReceiverThread extends Thread
{
	ChatManager receiverManager = null;
	public void run()
	{
		System.out.println("ReceiverThread is started");
		receiverManager = ChatManager.getInstanceFor( SmackConnector.connection);
		receiverManager.addChatListener(
	        	new ChatManagerListener() {
	        		@Override
	        		public void chatCreated(Chat chat, boolean createdLocally)
	        		{
	        			if (!createdLocally)
	        			{
	        				chat.addMessageListener(new ChatMessageListener()
	        				{
	        					@Override
	        					public void processMessage(Chat chat, Message message) 
	        					{
	        						if(message.getBody()!=null)
	        						{
	        							handleData(message);
	        						}
	        					}
	        				});
	        			}
	        		}
	        	});
	}
	private void handleData(Message message)
	{
		//TODO
		System.out.println(message);
		System.out.println(message.getBody());
	}
}
//发送消息线程
class SetMassageThread extends Thread
{
	ChatManager setMassageManager = null;
	Chat newChat = null;
	public void run()
	{
		setMassageManager = ChatManager.getInstanceFor(SmackConnector.connection);
		System.out.println("SetMassageThread is started");
		String to = "test@cujamin-pc";    	 
	    Scanner input = new Scanner(System.in); 
	    while (true) {
	        String message = input.nextLine();
	        //输入"1":下线
	        if(message.equals("1"))
	        {
	        	System.out.println("下线");
	        	SmackConnector.connection.disconnect();
	        	break;
	        }
	        else if(message.equals("2"))
	        {
	        	SmackConnector.addFriends(to,to);
	        }
	        else if(message.equals("3"))
	        {
	        	int a = 0;
	        	Collection<RosterEntry> rosters = Roster.getInstanceFor(SmackConnector.connection).getEntries(); 
	        	for(RosterEntry  rosterEntry : rosters)
	        	{
	        		if(rosterEntry.equals(SmackConnector.roster.getEntry(to)))
	        		{
	        			a = 1;
	        			SmackConnector.delFriends(to);
	        			break;
	        		}
	        	}
        		if(a == 0)
        		{
        			System.out.println("没有找到该好友："+to);
        		}
	        }
	        else postData( to , message , setMassageManager);
	    }  
	}
	public void postData(String to, String data ,ChatManager chatManager)
	{
		newChat = chatManager.createChat(to , null);
        try {
       	 newChat.sendMessage(data);
		} catch (NotConnectedException e) {
			System.out.println("Error Delivering block");
		}
	}
}

