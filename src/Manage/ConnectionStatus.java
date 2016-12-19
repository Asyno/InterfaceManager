package manage;

import javax.swing.JTextField;

import userGUI.Interface;

public class ConnectionStatus implements Runnable
{
	private JTextField statusText;
	private Interface  inter;
	
	public ConnectionStatus(JTextField status, Interface inter)
	{
		this.statusText = status;
		this.inter      = inter;
		
		start();
	}
	
	public void start()
	{
		new Thread(this).start();
	}

	@Override
	public void run()
	{
		while(true)
		{
			String[] status = inter.getConnectionStatus();
			
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
			if(status==null)
				statusText.setText("TCP/IP Connection is Disconnected");
			else
				// Serial connection Status
				if(status[0].equals("Serial"))
				{
					if(status[1].equals("Disconnected"))
						statusText.setText("Serial connection: Disconnected");
					else
						statusText.setText("Serial connection: connected at "+status[1]);
				}
				
				// TCP/IP connection Status
				else
				{
					if(status[0].contains("server"))
						statusText.setText("TCP/IP Server connection: Port: "+status[1]+" from "+status[0].replaceAll("server", ""));
					if(status[1].contains("Disconnected"))
						statusText.setText(status[0]+" waiting for connection");
				}
		}
		
	}

}
