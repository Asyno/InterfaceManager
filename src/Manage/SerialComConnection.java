package manage;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SerialComConnection implements Runnable, ActionListener
{
	// Serial Settings
	private JPanel            main        = new JPanel();
	private Box               mainBox     = new Box(BoxLayout.PAGE_AXIS);
	private JComboBox<String> comPort     = new JComboBox<String>();
	private Box               comPortBox  = new Box(BoxLayout.LINE_AXIS);
	private JComboBox<String> baudrate    = new JComboBox<String>();
	private Box               baudrateBox = new Box(BoxLayout.LINE_AXIS);
	private JComboBox<String> dataBits    = new JComboBox<String>();
	private Box               dataBitsBox = new Box(BoxLayout.LINE_AXIS);
	private JComboBox<String> stopBits    = new JComboBox<String>();
	private Box               stopBitsBox = new Box(BoxLayout.LINE_AXIS);
	private JComboBox<String> parity      = new JComboBox<String>();
	private Box               parityBox   = new Box(BoxLayout.LINE_AXIS);
	// COM Elements
	SerialPort                serialPort     = null;
	private boolean           serialPortOpen = false;
	private Preferences       prefs          = Preferences.userRoot().node(this.getClass().getName());
	private InputStream       input;
	private OutputStream      output;
	private int               iD;
	
	
	public SerialComConnection(int iD)
	{
		this.iD = iD;
		
		getSerialPorts();
		getBaudrates  ();
		getDataBits   ();
		getStopBits   ();
		getParity     ();
		
		comPort. addActionListener(this);
		comPort. setActionCommand ("comPort");
		baudrate.addActionListener(this);
		baudrate.setActionCommand ("baudrate");
		dataBits.addActionListener(this);
		dataBits.setActionCommand ("dataBits");
		stopBits.addActionListener(this);
		stopBits.setActionCommand ("stopBits");
		parity  .addActionListener(this);
		parity  .setActionCommand ("parity");
		
		if(prefs.getInt("comPort" +iD, 0)<comPort.getItemCount())
			comPort .setSelectedIndex(prefs.getInt("comPort" +iD, 0));
		baudrate.setSelectedIndex(prefs.getInt("baudrate"+iD, 0));
		dataBits.setSelectedIndex(prefs.getInt("dataBits"+iD, 0));
		stopBits.setSelectedIndex(prefs.getInt("stopBits"+iD, 0));
		parity  .setSelectedIndex(prefs.getInt("parity"  +iD, 0));
		
		Dimension dim = new Dimension(100,25);
		comPort .setMaximumSize  (new Dimension(dim));
		comPort .setPreferredSize(new Dimension(dim));
		baudrate.setMaximumSize  (new Dimension(dim));
		baudrate.setPreferredSize(new Dimension(dim));
		dataBits.setMaximumSize  (new Dimension(dim));
		dataBits.setPreferredSize(new Dimension(dim));
		stopBits.setMaximumSize  (new Dimension(dim));
		stopBits.setPreferredSize(new Dimension(dim));
		parity  .setMaximumSize  (new Dimension(dim));
		parity  .setPreferredSize(new Dimension(dim));

		comPortBox .add(new JLabel("Serial line to connect to:    "));
		comPortBox .add(Box.createGlue());
		comPortBox .add(comPort);
		
		baudrateBox.add(new JLabel("Speed (baud):"));
		baudrateBox.add(Box.createGlue());
		baudrateBox.add(baudrate);
		
		dataBitsBox.add(new JLabel("Data Bits:"));
		dataBitsBox.add(Box.createGlue());
		dataBitsBox.add(dataBits);
		
		stopBitsBox.add(new JLabel("Stop bits:"));
		stopBitsBox.add(Box.createGlue());
		stopBitsBox.add(stopBits);
		
		
		parityBox  .add(new JLabel("Parity:"));
		parityBox  .add(Box.createGlue());
		parityBox  .add(parity);
		
		mainBox    .add(Box.createGlue());
		mainBox    .add(comPortBox );
		mainBox    .add(new JLabel(" "));
		mainBox    .add(baudrateBox);
		mainBox    .add(new JLabel(" "));
		mainBox    .add(dataBitsBox);
		mainBox    .add(new JLabel(" "));
		mainBox    .add(stopBitsBox);
		mainBox    .add(new JLabel(" "));
		mainBox    .add(parityBox);
		mainBox    .add(new JLabel(" "));
		main       .add(mainBox);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case("comPort" ): prefs.putInt("comPort" +iD, comPort .getSelectedIndex()); break;
			case("baudrate"): prefs.putInt("baudrate"+iD, baudrate.getSelectedIndex()); break;
			case("dataBits"): prefs.putInt("dataBits"+iD, dataBits.getSelectedIndex()); break;
			case("stopBits"): prefs.putInt("stopBits"+iD, stopBits.getSelectedIndex()); break;
			case("parity"  ): prefs.putInt("parity"  +iD, parity  .getSelectedIndex()); break;
		}
	}
	
	public void delete()
	{
		prefs.putInt("comPort" +iD, 0);
		prefs.putInt("baudrate"+iD, 0);
		prefs.putInt("dataBits"+iD, 0);
		prefs.putInt("stopBits"+iD, 0);
		prefs.putInt("parity"  +iD, 0);
	}
	
	@Override
	public void run()
	{
		connect();
	}
	
	public void connect()
	{
		@SuppressWarnings("rawtypes")
		Enumeration        enumComm;
		CommPortIdentifier serialPortId = null;
		Boolean            foundPort    = false;
		String             portName     = (String) comPort.getSelectedItem();
		int baudrate = Integer.parseInt((String)this.baudrate.getSelectedItem());
		int dataBits = Integer.parseInt((String)this.dataBits.getSelectedItem());
		int stopBits;
		int parity = SerialPort.PARITY_NONE;
		
		if(((String)this.stopBits.getSelectedItem()).equals("1,5"))
			stopBits = SerialPort.STOPBITS_1_5;
		else
			stopBits = Integer.parseInt((String)this.stopBits.getSelectedItem());
		switch((String)this.stopBits.getSelectedItem())
		{
			case("EVEN"):
				parity = SerialPort.PARITY_EVEN;
				break;
			case("MARK"):
				parity = SerialPort.PARITY_MARK;
				break;
			case("NONE"):
				parity = SerialPort.PARITY_NONE;
				break;
			case("ODD"):
				parity = SerialPort.PARITY_ODD;
				break;
			case("SPACE"):
				parity = SerialPort.PARITY_SPACE;
				break;
		}
		
		
		if (serialPortOpen != false)
		{
			System.out.println("Serialport bereits geöffnet");
		}
		else
		{
			System.out.println("Öffne Serialport");
			enumComm = CommPortIdentifier.getPortIdentifiers();
			while(enumComm.hasMoreElements()) { 
				serialPortId = (CommPortIdentifier) enumComm.nextElement();
				if (portName.contentEquals(serialPortId.getName())) {
					foundPort = true;
					break;
				}
			}
			if (foundPort != true) {
				System.out.println("Serialport nicht gefunden: " + portName);
			}
			try {
				serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", 500);
			} catch (PortInUseException e) {
				System.out.println("Port belegt");
			}
			try {
				output = serialPort.getOutputStream();
			} catch (IOException e) {
				System.out.println("Keinen Zugriff auf OutputStream");
			}
			try {
				input = serialPort.getInputStream();
			} catch (IOException e) {
				System.out.println("Keinen Zugriff auf InputStream");
			}
			try {
				serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
				
			} catch(UnsupportedCommOperationException e) {
				System.out.println("Konnte Schnittstellen-Paramter nicht setzen");
			}
			serialPortOpen = true;
		}
	}
	
	public void stop()
	{
		if ( serialPortOpen == true) {
			System.out.println("Schließe Serialport");
			serialPort.close();
			serialPortOpen = false;
		} else {
			System.out.println("Serialport bereits geschlossen");
		}
	}
	
	public InputStream getInputStream()
	{
		return input;
	}
	
	public OutputStream getOutputStream()
	{
		return output;
	}
	
	public void start()
	{
		new Thread(this).start();
	}
	
	public JPanel getMain ()
	{
		return main;
	}
	
	public String[] getStatus()
	{
		if(serialPortOpen)
			return new String[]{"Serial",(String)comPort.getSelectedItem()};
		return new String[]{"Serial", "Disconnected"};
	}
	
	@SuppressWarnings("unchecked")
	private void getSerialPorts()
	{
		CommPortIdentifier serialPortId;
		Enumeration<CommPortIdentifier> enumComm;
		
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements())
		{
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			if(serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL)
				comPort.addItem(serialPortId.getName());
		}
	}
	
	private void getBaudrates()
	{
		baudrate.addItem("110");
		baudrate.addItem("300");
		baudrate.addItem("600");
		baudrate.addItem("1200");
		baudrate.addItem("2400");
		baudrate.addItem("4800");
		baudrate.addItem("9600");
		baudrate.addItem("14400");
		baudrate.addItem("19200");
		baudrate.addItem("38400");
		baudrate.addItem("57600");
		baudrate.addItem("115200");
		baudrate.addItem("128000");
		baudrate.addItem("256000");
	}
	
	private void getDataBits()
	{
		dataBits.addItem(""+SerialPort.DATABITS_5);
		dataBits.addItem(""+SerialPort.DATABITS_6);
		dataBits.addItem(""+SerialPort.DATABITS_7);
		dataBits.addItem(""+SerialPort.DATABITS_8);
	}
	
	private void getStopBits()
	{
		stopBits.addItem(""+SerialPort.STOPBITS_1);
		stopBits.addItem("1,5");
		stopBits.addItem(""+SerialPort.STOPBITS_2);
	}
	
	private void getParity()
	{
		parity.addItem("EVEN");
		parity.addItem("MARK");
		parity.addItem("NONE");
		parity.addItem("ODD");
		parity.addItem("SPACE");
		
	}
}
