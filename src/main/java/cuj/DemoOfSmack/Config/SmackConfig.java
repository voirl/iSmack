package cuj.DemoOfSmack.Config;
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

public class SmackConfig {
	private String serviceName ;
	private String host ;
	private int port ;
	public SmackConfig(String serviceName , String host , int port )
	{
		this.serviceName = serviceName;
		this.host = host;
		this.port = port;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	

}
