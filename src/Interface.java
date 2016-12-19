import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;


public class Interface implements ActionListener
{
	private CardLayout cardLayout         = new CardLayout();
	private JPanel cardPanel              = new JPanel();
	// Toolbar
	private JToolBar toolBar              = new JToolBar();
	private JButton settingsButton        = new JButton("Settings");
	private JButton monitorButton         = new JButton("Monitor");
	// Settings
	private JPanel settingsPanel          = new JPanel();
	private JPanel settingsAll            = new JPanel();
	private JRadioButton clientRadio      = new JRadioButton("Client", true);
	private JRadioButton serverRadio      = new JRadioButton("Server");
	private ButtonGroup serverClientGroup = new ButtonGroup();
	private boolean server = false;
	private JPanel iPPortLabelPanel       = new JPanel();
	private JTextField iPField            = new JTextField(15);
	private JPanel iPPortFieldPanel       = new JPanel();
	private JTextField portField          = new JTextField(5);
	private JPanel iPPortPanel            = new JPanel();
	private JPanel interName              = new JPanel();
	private JTextField interNameText      = new JTextField(10);
	private JButton saveSettings          = new JButton("Save");
	private JPanel iPPortActualPanel      = new JPanel();
	private JTextField iPActual           = new JTextField(15);
	private JTextField portActual         = new JTextField(5);
	// Monitor
	private InterfaceMonitor interMoni    = new InterfaceMonitor();
	private JPanel monitorPanel           = new JPanel();
	private JButton moniStartStop         = new JButton("Start");
	private boolean start = false;
	
	private int iD                        = 0;
	private JPanel main                   = new JPanel();
	
	public Interface(ActionListener aL, int iD)
	{
		this.iD = iD;
		interMoni.start();
		
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
		settingsAll.setLayout(new BoxLayout(settingsAll, BoxLayout.LINE_AXIS));
		iPPortLabelPanel.setLayout(new BoxLayout(iPPortLabelPanel, BoxLayout.PAGE_AXIS));
		iPPortFieldPanel.setLayout(new BoxLayout(iPPortFieldPanel, BoxLayout.PAGE_AXIS));
		iPPortActualPanel.setLayout(new BoxLayout(iPPortActualPanel, BoxLayout.PAGE_AXIS));
		iPPortPanel.setLayout(new BoxLayout(iPPortPanel, BoxLayout.LINE_AXIS));
		interName.setLayout(new BoxLayout(interName, BoxLayout.LINE_AXIS));
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));

		toolBar.setFloatable(false);
		iPActual.setEditable(false);
		portActual.setEditable(false);
		interNameText.setMaximumSize(new Dimension(100,20));
		iPField.setMaximumSize(new Dimension(100,20));
		portField.setMaximumSize(new Dimension(40,20));
		iPActual.setMaximumSize(new Dimension(100,20));
		portActual.setMaximumSize(new Dimension(40,20));
		
		// ToolBar
		settingsButton.addActionListener(this);
		settingsButton.setActionCommand("settings");
		monitorButton.addActionListener(this);
		monitorButton.setActionCommand("monitor");
		// Settings
		clientRadio.addActionListener(this);
		clientRadio.setActionCommand("client");
		serverRadio.addActionListener(this);
		serverRadio.setActionCommand("server");
		saveSettings.addActionListener(aL);
		saveSettings.addActionListener(this);
		saveSettings.setActionCommand("saveSettings"+this.iD);
		// Monitor
		moniStartStop.addActionListener(this);
		moniStartStop.setActionCommand("moniStart");
		
		// ToolBar
		toolBar.add(settingsButton);
		toolBar.add(monitorButton);
		// Settings
		interName.add(new JLabel("Name: "));
		interName.add(interNameText);
		serverClientGroup.add(clientRadio);
		serverClientGroup.add(serverRadio);
		iPPortLabelPanel.add(new JLabel("new IP: "));
		iPPortLabelPanel.add(new JLabel("new Port: "));
		iPPortFieldPanel.add(iPField);
		iPPortFieldPanel.add(portField);
		iPPortActualPanel.add(iPActual);
		iPPortActualPanel.add(portActual);
		iPPortPanel.add(iPPortLabelPanel);
		iPPortPanel.add(iPPortFieldPanel);
		iPPortPanel.add(new JLabel("    "));
		iPPortPanel.add(iPPortActualPanel);
		settingsPanel.add(interName);
		settingsPanel.add(new JLabel(" "));
		settingsPanel.add(clientRadio);
		settingsPanel.add(serverRadio);
		settingsPanel.add(new JLabel(" "));
		settingsPanel.add(iPPortPanel);
		settingsPanel.add(new JLabel(" "));
		settingsPanel.add(saveSettings);
		settingsAll.add(new JLabel("   "));
		settingsAll.add(settingsPanel);
		// Monitor
		monitorPanel.add(interMoni.main());
		monitorPanel.add(moniStartStop);
		
		cardPanel.setLayout(cardLayout);
		cardPanel.add(settingsAll, "settings");
		cardPanel.add(monitorPanel, "monitor");
		cardLayout.show(cardPanel,  "settings");
		
		main.add(toolBar,BorderLayout.NORTH);
		main.add(cardPanel,BorderLayout.CENTER);
		main.add(new JLabel(" "),BorderLayout.SOUTH);
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("settings"))
		{
			cardLayout.show(cardPanel, "settings");
		}
		if(e.getActionCommand().equals("monitor"))
		{
			cardLayout.show(cardPanel,  "monitor");
		}
		if(e.getActionCommand().equals("client"))
		{
			iPField.setEditable(true);
			server=false;
		}
		if(e.getActionCommand().equals("server"))
		{
			iPField.setEditable(false);
			iPField.setText("");
			server=true;
		}
		if(e.getActionCommand().equals("moniStart"))
		{
			if(start)
			{
				interMoni.stopMonitor();
				moniStartStop.setText("Start");
				start = false;
			}
			else
			{
				if(portField.getText().replace(" ","").isEmpty())
					JOptionPane.showMessageDialog(null, "Pleas insert a Port Number!", "Error", JOptionPane.ERROR_MESSAGE);
				else
				{
					if( (iPField.getText().replace(" ","").isEmpty())&&!server )
						JOptionPane.showMessageDialog(null, "Pleas insert an IP Address!", "Error", JOptionPane.ERROR_MESSAGE);
					else
					{
						try
						{
							interMoni.startMonitor(iPField.getText(), Integer.parseInt(portField.getText()), server);
							moniStartStop.setText("Stop");
							start = true;
						}
						catch(NumberFormatException error)
						{
							JOptionPane.showMessageDialog(null, "Pleas insert a correct Port!", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
	}
	
	public String getName()
	{
		return interNameText.getText();
	}

	public JPanel getPanel()
	{
		return main;
	}
}
