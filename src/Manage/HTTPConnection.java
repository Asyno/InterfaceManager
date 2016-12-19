package manage;

import java.io.IOException;

import javax.swing.JTextArea;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import userGUI.InterfaceData;

public class HTTPConnection implements Runnable
{
	private InterfaceTyp         interTyp;
	private JTextArea            monitor;
	private String               host;
	private int                  port;
	
	private CloseableHttpClient  httpclient = HttpClients.createDefault();
	private HTTPConnectionServer server;
	
	private boolean              start      = true;
	
	
	public HTTPConnection(String host,int port,String typ,Object setting,InterfaceData data,JTextArea monitor, int iD)
	{
		this.monitor = monitor;
		this.host    = host;
		this.port    = port;
		
		switch(typ)
		{
		case("tv_Samsung_LYNC"): interTyp = new InterfaceTV_Samsung_LYNC(data, iD, setting);
		}
		
		server = new HTTPConnectionServer(interTyp);
		
		start();
		server.start(port);
	}
	
	private void start()
	{
		new Thread(this).start();
	}
	
	public void stop()
	{
		start = false;
		server.stop();
	}

	@Override
	public void run()
	{
		if(start)
		{
			try
			{
				CloseableHttpResponse response = httpclient.execute(interTyp.getNextRequest(host, port));
				monitor.append(EntityUtils.toString(response.getEntity())+"\r\n");
				
				response.close();
			}
			catch (ClientProtocolException e) {e.printStackTrace();}
			catch (IOException e) {e.printStackTrace();}
		}
	}
}
