package manage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import javax.swing.JTextArea;

import userGUI.Interface;
import userGUI.InterfaceData;


public class InterfaceCommunication implements Runnable
{
	private JTextArea   monitor;
	private InputStream inputStream;
	private OutputStream outputStream;
	private String      input      = "";
	private String      output[]   = null;
	
	private InterfaceTyp interTyp;
	private Interface inter;
	private boolean serial;
    
	
	public InterfaceCommunication(JTextArea monitor, Interface inter, String typ, Object setting, InterfaceData data, int iD, boolean serial)
	{
		this.monitor      = monitor;
		this.inter        = inter;
		this.inputStream  = inter.getInputStream ();
		this.outputStream = inter.getOutputStream();
		this.serial       = serial;
		switch(typ)
		{
			case("pms_FIAS"):        interTyp = new InterfacePMS_FIAS       (data,iD,setting); break;
			case("tv_Samsung_LYNC"): interTyp = new InterfaceTV_Samsung_LYNC(data,iD,setting); break;
		}
		
		this.start();
	}
	
	public void start()
	{
		new Thread(this).start();
	}
	
	@Override
	public void run()
	{
		try {Thread.sleep(1000);} catch (InterruptedException e1) {e1.printStackTrace();}
		// Initial Startup
		output = interTyp.getData("start");
		sendData(output);
		
		// Communication
		try
		{
			while(true)
			{
				Thread.sleep(250);
				
				// Get data -----------------------------------------------------------
				if(!(inputStream==null)&&inputStream.available()>0)
				{
					int     count = 0;
					int     bcc[] = new int[100];
					boolean start = false;
					
					while(!input.endsWith(""))
					{
						int data = inputStream.read();
						
						if((Integer.toHexString(data).equals("2")))
						{
							input += (char)data;
							start = true;
						}
						
						if(!(Integer.toHexString(data).equals("ffffffff"))&&!(Integer.toHexString(data).equals("2"))&&start)
						{
							bcc[count]=data;
							input += (char)data;
							count++;
						}
					}
					monitor.append(("RX:"+input));
					if(!serial)
						monitor.append("\r\n");
					else
					{
						if(serial && (createBCC(bcc)!=inputStream.read()) )
						{
							outputStream.write(15);
							input = null;
							monitor.append(" - TX: <NAK>\r\n");
						}
						else
						{
							outputStream.write(6);
							monitor.append(" - TX: <ACK>\r\n");
						}
					}
					
					start = false;
					
					output = interTyp.getData(input);
					
					if(!(output==null))
						sendData(output);
									
					input  = "";
					output = null;
				}
				// Send data -----------------------------------------------------------
				sendData(interTyp.getNextSend());
			}
		}
		catch (IOException e) {}
		catch (InterruptedException e1) {e1.printStackTrace();}
	}
	
	public void endCommunication()
	{
		output=interTyp.getData("stop");
		if(outputStream!=null)
			sendData(output);
	}
	
	private void sendData(String output[])
	{
		int n = 0;
		boolean sendOk = false;
		
		while(!(output==null)&&n<output.length)
		{
			sendOk = false;
			while(!sendOk)
			{
				try
				{
					outputStream.write((""+output[n]+"").getBytes());
					
					if(serial)
					{
						// send BBC
						byte[] test = (output[n]+"").getBytes();
						String bbc  = "0";
						
						for(int i=0;i<test.length;i++)
							bbc = ""+(Integer.parseInt(Integer.toHexString(Integer.parseInt(bbc)),16)^Integer.parseInt(Integer.toHexString(test[i]),16));
						outputStream.write(Integer.parseInt(bbc));
						monitor.append("TX:"+""+output[n]+"");

						// get response
						Thread.sleep(500);
						if(!(inputStream==null)&&inputStream.available()>0)
						{
							boolean responseNAK = true;
							while(responseNAK)
							{
								int count = 0;
								switch(Integer.toHexString(inputStream.read()))
								{
								case("6"):
									monitor.append(" - RX: <ACK>\r\n");
									responseNAK = false;
									break;
								case("15"):
									monitor.append(" - RX: <NAK>\r\n");
									if(count<10)
									{
										outputStream.write((""+output[n]+""+Integer.parseInt(bbc)).getBytes());
										monitor.append("TX:"+""+output[n]+"");
										count++;
									}
									else
										responseNAK = false;
									break;
								}
							}
							responseNAK = false;
						}
					}
					else
						monitor.append("TX:"+""+output[n]+"\r\n");
					
					Thread.sleep(250);
					sendOk=true;
				}
				catch (SocketException e)      {inter.connect();}
				catch (InterruptedException e) {e.printStackTrace();}
				catch (IOException e)          {e.printStackTrace();}
				catch (NullPointerException e)
				{
					monitor.append("\r\n");
					try {Thread.sleep(5000);} catch (InterruptedException e1) {}
					inputStream  = inter.getInputStream();
					outputStream = inter.getOutputStream();
					inter.connect();
				}
			}
			n++;
		}
		sendOk = false;
		n=0;
	}
	
	private int createBCC(int data[])
	{
		int bcc = 0;
		
		for(int i=0;i<data.length;i++)
			bcc = Integer.parseInt(Integer.toHexString(bcc),16)^Integer.parseInt(Integer.toHexString(data[i]),16);
			
		return bcc;
	}
}
