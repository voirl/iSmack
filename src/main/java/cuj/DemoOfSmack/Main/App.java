package cuj.DemoOfSmack.Main;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * Hello world!
 *
 */
public class App 
{
	static ChatManager chatManager = null;
	static XMPPTCPConnection connection = null;
	static Presence presence = null;
	static Roster roster =null;
	public static void main( String[] args ) 
    {
		//配置需要连接的服务器信息
		XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
		configBuilder.setServiceName("cujamin-pc");
		configBuilder.setHost("localhost");
		configBuilder.setPort(5222);
		configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		configBuilder.setCompressionEnabled(false);
		XMPPTCPConnectionConfiguration config = configBuilder.build();

		connection =new XMPPTCPConnection(config);
         try {
        	 //连接服务器
        	 connection.connect();
             System.out.println("connect is success");
             //登录服务器(却输入账号密码)****************************
             connection.login("cuj1","1");
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
         //设置当前状态(状态写死)************************
         presence = new Presence(Presence.Type.available);  
         presence.setStatus("X");
         try {
        	 connection.sendPacket(presence);
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         
         
         //通信管理(接受所有信息，发送消息消息给指定用户)
         chatManager = ChatManager.getInstanceFor(connection);
         
         //接收信息
         chatManager.addChatListener(
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
         							System.out.println(message);
         							System.out.println(message.getBody());
         						}
         					}
         				});
         			}
         		}
         	});
         
         //监听好友状态
    	 roster = Roster.getInstanceFor(connection);
    	 roster.addRosterListener(new RosterListener(){
			@Override
			public void entriesAdded(Collection<String> addresses) {}
			@Override
			public void entriesUpdated(Collection<String> addresses) {}
			@Override
			public void entriesDeleted(Collection<String> addresses) {}
			@Override
			public void presenceChanged(Presence presence) {
				System.out.println(presence);
			}	 
    	 });
    	 addFriends();


         //发送消息（待完善，目前只实现给指定用户发送消息）****************
    	 String to = "psi-t@cujamin-pc";    	 
         Scanner input = new Scanner(System.in); 
         while (true) {
             String message = input.nextLine();
             //输入"1":下线
             if(message.equals("1"))
             {
            	 connection.disconnect();
            	 break;
             }
             postData( to , message );
         }  
    }
	 //添加好友****************************************************************
	 public static void addFriends()
	 {
		 try {
			roster.createEntry("test@cujamin-pc", "test", new String[]{"Friends"});
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
	//监听消息**************************************************************
	public static void  receiveData()
	{
		
	}
	//发送消息**************************************************************
	public static void postData(String to, String data)
	{
		Chat newChat = chatManager.createChat(to , null);
        try {
       	 newChat.sendMessage(data);
		} catch (NotConnectedException e) {
			System.out.println("Error Delivering block");
		}
	}
	//***************************************************************************
}