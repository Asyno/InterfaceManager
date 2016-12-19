package userGUI;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.Preferences;

import javax.swing.*;

import manage.ConnectionStatus;
import manage.HTTPConnection;
import manage.InterfaceCommunication;
import manage.MaxLengthDoc;
import manage.SerialComConnection;
import manage.TcpIPConnection;


public class Interface implements ActionListener
{
	private CardLayout   cardLayout           = new CardLayout();
	private JPanel       cardPanel            = new JPanel();
	// Toolbar
	private JToolBar     toolBar              = new JToolBar();
	private JButton      settingsButton       = new JButton("Settings");
	private JButton      interfaceButton      = new JButton("Interface");
	private JButton      monitorButton        = new JButton("Monitor");
	// Settings
	private JPanel       settingsPanel        = new JPanel();
	private Box          settingsAll          = new Box(BoxLayout.PAGE_AXIS);
	private JRadioButton clientRadio          = new JRadioButton("Client", true);
	private JRadioButton serverRadio          = new JRadioButton("Server");
	private ButtonGroup  serverClientGroup    = new ButtonGroup();
	private boolean      server               = false;
	private JPanel       iPPortLabelPanel     = new JPanel();
	private JTextField   iPField              = new JTextField(15);
	private JPanel       iPPortFieldPanel     = new JPanel();
	private JTextField   portField            = new JTextField(5);
	private JPanel       iPPortPanel          = new JPanel();
	private JPanel       interName            = new JPanel();
	private JTextField   interNameText        = new JTextField(10);
	private JButton      saveSettings         = new JButton("Save");
	private JPanel       iPPortActualPanel    = new JPanel();
	private JTextField   iPActual             = new JTextField(15);
	private JTextField   portActual           = new JTextField(5);
	private Preferences  prefs                = Preferences.userRoot().node(this.getClass().getName());
	private Box          iPSettings           = new Box(BoxLayout.PAGE_AXIS);
	private JRadioButton tcpRadio             = new JRadioButton("TCP/IP", true);
	private JRadioButton serialRadio          = new JRadioButton("Serial");
	private ButtonGroup  connectionGroup      = new ButtonGroup();
	private JPanel       connectionRadioPanel = new JPanel();
	private JPanel       connectionTypPanel   = new JPanel();
	
	private TcpIPConnection     tcp;
	private HTTPConnection      http;
	private SerialComConnection serial;
	// Interface Settings
	private JPanel interfacePanel             = new JPanel();
	private InterfaceSetting interSetting;
	// Monitor
	private JPanel  monitorPanel              = new JPanel();
	private JButton moniStartStop             = new JButton("Start");
	private boolean start                     = false;
	private InterfaceMonitor interMoni;
	
	private JPanel main                       = new JPanel();
	private int    iD                         = 0;
	private String name;

	private InterfaceCommunication interInput;
	private InterfaceData          data;
	
	public Interface(ActionListener aL, int iD, InterfaceData data)
	{
		this.iD      = iD;
		this.data    = data;
		name = "Interface - "+iD;
		interSetting = new InterfaceSetting(iD);
		serial       = new SerialComConnection(iD);
		interMoni    = new InterfaceMonitor(this);
		interMoni.start();
		new ConnectionStatus(interMoni.getTextField(),this);
		
		iPActual   .setText    ( prefs.get(iD+"IP"  , ""));
		portActual .setText    ( prefs.get(iD+"port", ""));
		clientRadio.setSelected(!prefs.getBoolean(iD+"server", false));
		serverRadio.setSelected( prefs.getBoolean(iD+"server", false));
		tcpRadio   .setSelected( prefs.getBoolean(iD+"tcp"   , true ));
		serialRadio.setSelected(!prefs.getBoolean(iD+"tcp"   , true ));
		
		server = prefs.getBoolean(iD+"server", false);
		if(server)
			iPField.setEditable(false);
		
		settingsPanel    .setLayout(new BoxLayout(settingsPanel    , BoxLayout.PAGE_AXIS));
		iPPortLabelPanel .setLayout(new BoxLayout(iPPortLabelPanel , BoxLayout.PAGE_AXIS));
		iPPortFieldPanel .setLayout(new BoxLayout(iPPortFieldPanel , BoxLayout.PAGE_AXIS));
		iPPortActualPanel.setLayout(new BoxLayout(iPPortActualPanel, BoxLayout.PAGE_AXIS));
		iPPortPanel      .setLayout(new BoxLayout(iPPortPanel      , BoxLayout.LINE_AXIS));
		main             .setLayout(new BoxLayout(main             , BoxLayout.PAGE_AXIS));

		toolBar           .setFloatable(false);
		iPActual          .setEditable (false);
		portActual        .setEditable (false);
		interNameText     .setMaximumSize(new Dimension(100,20));
		iPField           .setMaximumSize(new Dimension(100,20));
		portField         .setMaximumSize(new Dimension(40,20));
		iPActual          .setMaximumSize(new Dimension(100,20));
		portActual        .setMaximumSize(new Dimension(40,20));
		portField         .setDocument   (new MaxLengthDoc(5));
		connectionTypPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		// ToolBar
		settingsButton .addActionListener(this);
		settingsButton .setActionCommand ("settings");
		interfaceButton.addActionListener(this);
		interfaceButton.setActionCommand ("interface");
		monitorButton  .addActionListener(this);
		monitorButton  .setActionCommand ("monitor");
		// Settings
		clientRadio  .addActionListener(this);
		clientRadio  .setActionCommand ("client");
		serverRadio  .addActionListener(this);
		serverRadio  .setActionCommand ("server");
		saveSettings .addActionListener(aL  );
		saveSettings .addActionListener(this);
		saveSettings .setActionCommand ("saveSettings"+this.iD);
		iPField      .addActionListener(this);
		iPField      .setActionCommand ("saveSettings");
		portField    .addActionListener(this);
		portField    .setActionCommand ("saveSettings");
		interNameText.addActionListener(aL  );
		interNameText.addActionListener(this);
		interNameText.setActionCommand ("saveSettings"+this.iD);
		// Interface Settings
		tcpRadio     .addActionListener(this);
		tcpRadio     .setActionCommand ("tcp/ip");
		serialRadio  .addActionListener(this);
		serialRadio  .setActionCommand ("serial");
		// Monitor
		moniStartStop.addActionListener(this);
		moniStartStop.setActionCommand ("moniStart");
		
		
		// ToolBar
		toolBar.add(settingsButton );
		toolBar.add(interfaceButton);
		toolBar.add(monitorButton  );
		// Settings
		interName        .add(new JLabel("Name: "));
		interName        .add(interNameText);
		connectionGroup  .add(tcpRadio);
		connectionGroup  .add(serialRadio);
		connectionRadioPanel.add(tcpRadio);
		connectionRadioPanel.add(serialRadio);
		settingsPanel    .add(interName);
		settingsPanel    .add(new JLabel(" "));
		settingsPanel    .add(new JLabel("Connection Typ"));
		settingsPanel    .add(connectionRadioPanel);
		// TCP IP
		serverClientGroup.add(clientRadio);
		serverClientGroup.add(serverRadio);
		iPPortLabelPanel .add(new JLabel("new Host/IP: "));
		iPPortLabelPanel .add(new JLabel("new Port: "));
		iPPortFieldPanel .add(iPField);
		iPPortFieldPanel .add(portField);
		iPPortActualPanel.add(iPActual);
		iPPortActualPanel.add(portActual);
		iPPortPanel      .add(iPPortLabelPanel);
		iPPortPanel      .add(iPPortFieldPanel);
		iPPortPanel      .add(new JLabel("    "));
		iPPortPanel      .add(iPPortActualPanel);
		iPSettings       .add(clientRadio);
		iPSettings       .add(serverRadio);
		iPSettings       .add(new JLabel(" "));
		iPSettings       .add(iPPortPanel);
		iPSettings       .add(new JLabel(" "));
		iPSettings       .add(saveSettings);
		// Connection Typ
		connectionTypPanel.setLayout(cardLayout);
		connectionTypPanel.add(iPSettings      ,"tcp/ip");
		connectionTypPanel.add(serial.getMain(),"serial");
		if(serialRadio.isSelected())
			cardLayout.show(connectionTypPanel ,"serial");
		
		settingsAll      .add(new JLabel("   "));
		settingsAll      .add(settingsPanel);
		settingsAll      .add(connectionTypPanel);
		//Interface Settings
		interfacePanel.add(interSetting.getMain());
		// Monitor
		monitorPanel.add(interMoni.main());
		monitorPanel.add(new JLabel(" "));
		monitorPanel.add(moniStartStop);
		
		cardPanel .setLayout(cardLayout);
		cardPanel .add      (settingsAll   , "settings" );
		cardPanel .add      (interfacePanel, "interface");
		cardPanel .add      (monitorPanel  , "monitor"  );
		cardLayout.show     (cardPanel     , "settings" );
		
		main.add(toolBar        ,BorderLayout.NORTH );
		main.add(cardPanel      ,BorderLayout.CENTER);
		main.add(new JLabel(" "),BorderLayout.SOUTH );
	}
	
	public void deleteInterface()
	{
		interSetting.delete();
		serial      .delete();
		prefs.put       ("interName"+iD, "");
		prefs.put       (iD+"IP"       , "");
		prefs.put       (iD+"port"     , "");
		prefs.putBoolean(iD+"tcp"      , true );
		prefs.putBoolean(iD+"server"   , false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("tcp/ip"))
		{
			cardLayout.show(connectionTypPanel, "tcp/ip");
			prefs.putBoolean(iD+"tcp", true);
		}
		
		if(e.getActionCommand().equals("serial"))
		{
			cardLayout.show(connectionTypPanel, "serial");
			prefs.putBoolean(iD+"tcp", false);
		}
		
		if(e.getActionCommand().equals("settings"))
			cardLayout.show(cardPanel, "settings");
		
		if(e.getActionCommand().equals("interface"))
			cardLayout.show(cardPanel, "interface");
		
		if(e.getActionCommand().equals("monitor"))
			cardLayout.show(cardPanel,  "monitor");
		
		if(e.getActionCommand().equals("client"))
		{
			iPField.setEditable(true);
			server=false;
			prefs.putBoolean(iD+"server", false);
		}
		
		
		if(e.getActionCommand().equals("server"))
		{
			iPField .setEditable(false);
			iPField .setText("");
			iPActual.setText("");
			server=true;
			prefs.putBoolean(iD+"server", true);
		}
		
		if(e.getActionCommand().contains("saveSettings"))
		{
			if(!iPField.getText().equals(""))
			{
				iPActual.setText(iPField.getText());
				prefs.put(iD+"IP", iPActual.getText());
			}
			if(!portField.getText().equals(""))
			{
				portActual.setText(portField.getText());
				prefs.put(iD+"port", portActual.getText());
			}
			iPField.setText("");
			portField.setText("");
			name = interNameText.getText();
			prefs.put("interName"+iD, name);
		}
		
		if(e.getActionCommand().equals("moniStart"))
		{
			if(start)
			{
				if(interInput!=null)
					interInput.endCommunication();
				interInput=null;
				if(tcpRadio.isSelected())
				{
					if(interSetting.isHTML())
						http.stop();
					else
						tcp.stop();
				}
				else
					serial.stop();
				interMoni.stopMonitor();
				moniStartStop.setText("Start");
				start = false;
			}
			else
			{
				if(portActual.getText().replace(" ","").isEmpty())
					JOptionPane.showMessageDialog(null, "Pleas insert a Port Number!", "Error", JOptionPane.ERROR_MESSAGE);
				else
				{
					if( (iPActual.getText().replace(" ","").isEmpty())&&!server )
						JOptionPane.showMessageDialog(null, "Pleas insert an IP Address!", "Error", JOptionPane.ERROR_MESSAGE);
					else
					{
						try
						{
							if(interSetting.isHTML())
							{
								if(tcpRadio.isSelected())
									http = new HTTPConnection(iPActual.getText(),Integer.parseInt(portActual.getText()),interSetting.getTyp(),interSetting.getSetting(),data,interMoni.getTextArea(),iD);
								else
									JOptionPane.showMessageDialog(null, "The selected HTML Interface isn't compatible with a serial connection!", "Error", JOptionPane.ERROR_MESSAGE);
							}
							else
							{
								if(tcpRadio.isSelected())
									tcp = new TcpIPConnection(iPActual.getText(), Integer.parseInt(portActual.getText()), server);
								if(serialRadio.isSelected())
									serial.start();
								interInput = new InterfaceCommunication(interMoni.getTextArea(),this, interSetting.getTyp(), interSetting.getSetting(),data,iD,serialRadio.isSelected());
							}
							interMoni.startMonitor();
							moniStartStop.setText("Stop");
							start = true;
						}
						catch (NumberFormatException error) {JOptionPane.showMessageDialog(null, "Pleas insert a correct Port!", "Error", JOptionPane.ERROR_MESSAGE);}
					}
				}
			}
		}
	}
	
	public String getName()
	{
		return prefs.get("interName"+iD, "Interface - "+iD);
	}
	
	public int getID()
	{
		return iD;
	}
	
	public String getTyp()
	{
		return interSetting.getTyp();
	}

	public JPanel getPanel()
	{
		return main;
	}
	
	public void connect()
	{
		if(tcp!=null)
		{
			if(tcpRadio.isSelected())
				tcp.connect();
			else
				serial.connect();
		}
	}
	
	public OutputStream getOutputStream()
	{
		try
		{
			if(serialRadio.isSelected())
			{
				if(serial!=null)
					return serial.getOutputStream();
				else
					return null;
			}
			else
				return tcp.getOutputStream();
		}
		catch (IOException e) {return null;}
	}
	
	public InputStream getInputStream()
	{
		try
		{
			if(!tcpRadio.isSelected())
			{
				if(serial!=null)
					return serial.getInputStream();
				else
					return null;
			}
			return tcp.getInputStream();
		}
		catch (IOException e) {return null;}
	}
	
	public String[] getConnectionStatus()
	{
		if(tcpRadio.isSelected())
		{
			if(tcp!=null)
				return tcp.getStatus();
			else
				return null;
		}
		return serial.getStatus();
	}
}
