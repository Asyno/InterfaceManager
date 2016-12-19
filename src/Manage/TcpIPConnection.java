package manage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class TcpIPConnection implements Runnable
{
	private Socket client       = null;
	private ServerSocket server = null;
	private String iP           = "";
	private int port            = 0;
	private boolean isServer    = false;
	private boolean start       = false;
	
	public TcpIPConnection(String iP, int port, boolean isServer)
	{
		this.iP       = iP;
		this.port     = port;
		this.isServer = isServer;
		
		start();
	}
	
	public String[] getStatus()
	{
		if(!start)
			return null;
		if(client!=null&&client.isConnected()&&!client.isClosed())
		{
			if(isServer)
				iP="server"+client.getRemoteSocketAddress();
			return new String[]{iP,""+port};
		}
		if(isServer)
			return new String[]{"Server","Disconnected"};
		return new String[]{"Client","Disconnected"};
	}
	
	public void run()
	{
		connect();
	}
	
	public void start()
	{
		start = true;
		new Thread(this).start();
	}
	
	public void connect()
	{
		if(isServer)
		{
			try 
			{
				if(start&&server==null)
				{
					if(server!=null)
						server.close();
					server = new ServerSocket(port);
					client = server.accept();
				}
			}
			catch (UnknownHostException e){System.out.println("TCP connect "+e);}
			catch (SocketException e)     {}
			catch (IOException e)         {System.out.println("TCP connect "+e);}
		}
		else
		{
			try
			{
				client = new Socket(iP, port);
			}
			catch (UnknownHostException e) {e.printStackTrace();}
			catch (ConnectException e)     {System.out.println("TCP: keine Verbindung"+e);}
			catch (IOException e)          {e.printStackTrace();}
		}
	}
	
	public void stop()
	{
		try
		{
			start = false;
			if(client!=null)
				client.close();
			if(server!=null)
				server.close();
		}
		catch (IOException e)         {System.out.println("errorTCP close "+e);}
		catch (NullPointerException e){System.out.println("errorTCP close "+e);}
	}
	
	public InputStream getInputStream() throws IOException
	{
		if(client!=null)
			return client.getInputStream();
		else
			return null;
	}
	
	public OutputStream getOutputStream() throws IOException
	{
		if(client!=null)
			return client.getOutputStream();
		else
			return null;
	}
}