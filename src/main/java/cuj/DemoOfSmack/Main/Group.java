package cuj.DemoOfSmack.Main;
/*
*
* project_name: DemoOfSmack
* class_name: 
* class_description: 
* author: cujamin
* create_time: 2016年7月8日
* modifier: 
* modify_time: 
* modify_description: 
* @version
*
 */

import java.io.IOException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

public class Group {
	public static void main( String[] args ) 
    {
		XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
		configBuilder.setServiceName("cujamin-pc");
		configBuilder.setHost("localhost");
		configBuilder.setPort(5222);
		configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		configBuilder.setCompressionEnabled(false);
		XMPPTCPConnectionConfiguration config = configBuilder.build();

		XMPPTCPConnection connection =new XMPPTCPConnection(config);

        try {
        	connection.connect();
            System.out.println("connect is success");
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
         
       
        Message newmsg = new Message(); 
 		
 		newmsg.setTo("pandion-cuj@cujamin-pc");
 		newmsg.setTo("psi-t@cujamin-pc");
 		
 		newmsg.setSubject("重要通知");
 		newmsg.setBody("今天下午2点60分有会！");
 		newmsg.setType(Message.Type.normal);// normal支持离线 
 		try {
			connection.sendPacket(newmsg);
		} catch (NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 		connection.disconnect();
         //发送消息
//         Scanner input = new Scanner(System.in); 
//         while (true) {  
//             String message = input.nextLine();   
//             try {
//            	 newChat.sendMessage(message);
//			} catch (NotConnectedException e) {
//				System.out.println("Error Delivering block");
//			}  
//         }  
       //connection.disconnect(); 
 		 while (true);
    }

}
