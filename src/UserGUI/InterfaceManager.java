package userGUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.*;
import java.util.prefs.Preferences;

import javax.swing.*;


public class InterfaceManager extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CardLayout cardLayout      = new CardLayout();
	// menus
	private JToolBar toolBar           = new JToolBar();
	private JButton  login             = new JButton("Login");
	private JButton  newInterface      = new JButton("New Interface");
	private JButton  deleteInterface   = new JButton("Delete Interface");
	private JButton  showData          = new JButton("Show Data");
	private Box      header            = new Box(BoxLayout.PAGE_AXIS);
	// interfaces Menu
	private JToolBar    interfaceBar   = new JToolBar();
	private JButton[]   interfaceButton= new JButton[100];
	private JScrollPane interfacePanel = new JScrollPane(interfaceBar);
	// Interfaces
	private JLabel      errorInterface = new JLabel("    no Interface!");
	private Interface[] inter          = new Interface[100];
	private JLabel[]    interName      = new JLabel[100];
	private Box[]       interBox       = new Box[100];
	private JPanel      center         = new JPanel();
	private int         interCount     = 0;
	private int         actualInter    = 0;
	private Preferences prefs          = Preferences.userRoot().node(this.getClass().getName());
	// login
	private JPanel loginPanel          = new JPanel();
	private Box passwordPanel          = new Box(BoxLayout.LINE_AXIS);
	private JPasswordField loginField  = new JPasswordField(15);
	private JButton loginButton        = new JButton("Login");
	// Data
	private InterfaceData data         = new InterfaceData(this);
	
	public InterfaceManager()
	{
		setTitle("InterfaceManager_0.0");
		
		interfaceBar   .setFloatable(false);
		toolBar        .setFloatable(false);
		interfaceBar   .setOrientation(JToolBar.VERTICAL);
		center         .setBorder(BorderFactory.createLineBorder(Color.BLACK));
		deleteInterface.setEnabled(false);
		
		newInterface   .addActionListener(this);
		newInterface   .setActionCommand("newInterface");
		deleteInterface.addActionListener(this);
		deleteInterface.setActionCommand("deleteInterface");
		login          .addActionListener(this);
		login          .setActionCommand("login");
		loginButton    .addActionListener(this);
		loginButton    .setActionCommand("loginButton");
		showData       .addActionListener(this);
		showData       .setActionCommand("showData");
		
		// header Menus
		toolBar       .add(login);
		toolBar       .addSeparator();
		toolBar       .add(newInterface);
		toolBar       .add(deleteInterface);
		toolBar       .add(showData);
		header        .add(toolBar);
		header        .add(new JLabel(" "));
		// interface Menu
		interfaceBar  .add(new JLabel("Interface Menu      "));
		// interfaces
		center        .setLayout(cardLayout);
		errorInterface.setForeground(Color.RED);
		center        .add(errorInterface, "noInterface");
		center        .add(new JLabel("   Interface deleted"), "deleteInterface");
		// Login
		passwordPanel .add(new JLabel("Password: "));
		passwordPanel .add(loginField);
		loginPanel    .add(passwordPanel);
		loginPanel    .add(loginButton);
		center        .add(loginPanel, "login");
		// Data
		center        .add(data.getMain(), "data");
		
		add(header        , BorderLayout.NORTH );
		add(interfacePanel, BorderLayout.WEST  );
		add(center        , BorderLayout.CENTER);
		
		setLastInterfaces();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().startsWith("saveSettings"))
		{
			int iD = Integer.parseInt(e.getActionCommand().replaceAll("saveSettings", ""));
			if(!inter[iD].getName().equals(""))
			{
				interfaceButton[iD].setText(inter[iD].getName()+" - "+iD);
				interName[iD]      .setText(inter[iD].getName()+" - "+iD);
				prefs.put("Interface"+iD  , inter[iD].getName()+" - "+iD);
			}
		}
		
		if(e.getActionCommand().equals("login"))
		{
			cardLayout.show(center, "login");
			deleteInterface.setEnabled(false);
		}
		
		if(e.getActionCommand().equals("newInterface"))
		{
			int newID = interCount;
			for(int i=0;i<interCount;i++)
			{
				if((inter[i]==null))
				{
					newID = i;
					break;
				}
			}
			inter[newID]           = new Interface(this, newID, data   );
			interfaceButton[newID] = new JButton  ("Interface - "+newID);
			interfaceButton[newID].addActionListener(this);
			interfaceButton[newID].setActionCommand ("inter"+newID);
			interfaceBar.add (interfaceButton[newID]);
			prefs       .put("Interface"+newID, "Interface - "+newID);
			String temp = prefs.get("ID_DB", "");
			prefs       .put("ID_DB", temp+"-"+newID);
			interName[newID] = new JLabel(" "+"Interface - "+newID+" ");
			interName[newID].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			interBox [newID] = new Box(BoxLayout.PAGE_AXIS);
			interBox [newID].add (interName[newID]);
			interBox [newID].add (new JLabel(" "));
			interBox [newID].add (inter[newID].getPanel());
			center      .add (interBox[newID], "inter"+newID);
			cardLayout  .show(center, "inter"+newID);
			actualInter = newID;
			interCount++;
			deleteInterface.setEnabled(true);
		}
		
		if(e.getActionCommand().equals("deleteInterface"))
		{
			try {inter[actualInter].deleteInterface();} catch (NullPointerException error){System.out.println(error);}
			inter[actualInter]     = null;
			interName[actualInter] = null;
			interfaceBar   .remove(interfaceButton[actualInter]);
			prefs          .remove("Interface"+actualInter);
			prefs.put("ID_DB", prefs.get("ID_DB", "").replaceAll("-"+actualInter, ""));
			cardLayout     .show(center, "deleteInterface");
			deleteInterface.setEnabled(false);
				
			if(interCount>0)
				interCount--;
			actualInter = 0;
			repaint();
		}
		
		if(e.getActionCommand().equals("showData"))
		{
			cardLayout.show(center, "data");
		}
		
		if(e.getActionCommand().startsWith("inter"))
		{
			String test;
			cardLayout.show(center, e.getActionCommand());
			test = e.getActionCommand().replaceFirst("inter", "");
			actualInter = Integer.parseInt(test);
			deleteInterface.setEnabled(true);
		}
	}
	
	public void setLastInterfaces()
	{
		String iD_dB[] = (prefs.get("ID_DB", "").split("\\-"));
		int n          = 0;
		int tempID;
		int startID    = -1;
		
		if(iD_dB.length>0)
		{
			while(n<iD_dB.length)
			{
				if(!iD_dB[n].equals(""))
				{
					tempID                  = Integer.parseInt(iD_dB[n]);
					if(startID==-1)
						startID = tempID;
					inter[tempID]           = new Interface(this, tempID, data   );
					interfaceButton[tempID] = new JButton  (prefs.get("Interface"+tempID, "Interface - "+tempID));
					interfaceButton[tempID].addActionListener(this);
					interfaceButton[tempID].setActionCommand ("inter"+tempID);
					interfaceBar.add (interfaceButton[tempID]);
					interName[tempID] = new JLabel(" "+prefs.get("Interface"+tempID, "Interface - "+tempID)+" ");
					interName[tempID].setBorder(BorderFactory.createLineBorder(Color.BLACK));
					interBox [tempID] = new Box(BoxLayout.PAGE_AXIS);
					interBox [tempID].add (interName[tempID]);
					interBox [tempID].add (new JLabel(" "));
					interBox [tempID].add (inter    [tempID].getPanel());
					center.add (interBox[tempID], "inter"+tempID);
					interCount++;
				}
				n++;
			}
			n=0;
			cardLayout.show(center, "inter"+startID);
			actualInter = startID;
		}
	}
	
	public String getInterID()
	{
		String iD = "";
		int    n  = 0;
		while(n<interCount)
		{
			iD += "|"+inter[n].getID()+"|";
			n++;
		}
		n=0;
		return iD;
	}

	public static void main(String[] args)
	{
		InterfaceManager iManager = new InterfaceManager();
		iManager.setVisible(true);
		iManager.setSize(600,550);
		iManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
