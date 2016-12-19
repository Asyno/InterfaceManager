package manage;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.bootstrap.HttpServer;

public interface InterfaceTyp
{
	public String[] getData(String data);
	
	public String[] getNextSend();
	
	public HttpUriRequest getNextRequest(String host, int port);
	
	public HttpServer getServerSettings(int port);
}
