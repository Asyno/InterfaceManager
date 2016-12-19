import java.io.IOException;
import java.util.NoSuchElementException;

import javax.swing.*;

import Manage.TcpIPConnection;


public class InterfaceMonitor implements Runnable
{
	private JTextArea monitorTextArea  = new JTextArea(15,20);
	private JScrollPane scroll         = new JScrollPane(monitorTextArea);
	private JTextField monitorStatus   = new JTextField(20);
	private JPanel main                = new JPanel();
	
	private String iP      = "";
	private int port       = 0;
	private boolean server = false;
	private TcpIPConnection tcp;
	
	public InterfaceMonitor()
	{
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		monitorTextArea.setEditable(false);
		monitorStatus.setEditable(false);
		
		main.add(scroll);
		main.add(new JLabel(" "));
		main.add(monitorStatus);
		monitorTextArea.setText("STOP");
	}
	
	public void run()
	{
		while(true)
		{
			try {
				Thread.sleep(1000);
				if(tcp.getSocket().isConnected())
				{
					if(server)
						monitorStatus.setText("Connected on Port "+port+" from "+tcp.getSocket().getRemoteSocketAddress());
					else
						monitorStatus.setText("Connected to "+iP+":"+port);
					tcp.getOutputStream().println("hello World.");
					monitorTextArea.append("TX: hello World.\r\n");
					monitorTextArea.append("RX: "+tcp.getInputStream().nextLine()+"\r\n");
				}
			}
			catch (NullPointerException e){}
			catch (IOException e1) {}
			catch (InterruptedException e2) {}
			catch (NoSuchElementException e3) {}
		}
	}
	
	public void start()
	{
		new Thread(this).start();
	}
	
	public JPanel main()
	{
		return main;
	}
	
	public void startMonitor(String iP, int port, boolean server)
	{
		this.iP     = iP;
		this.port   = port;
		this.server = server;
		monitorTextArea.setText("START\r\n");
		tcp = new TcpIPConnection(iP, port, this.server);
		tcp.start();
	}
	
	public void stopMonitor()
	{
		monitorTextArea.append("\r\nSTOP");
		try
		{
			tcp.stop();
		}
		catch(NullPointerException e){System.out.println("error1 "+e);}
		
	}
}