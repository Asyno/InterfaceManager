import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.*;

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
	private JButton login              = new JButton("Login");
	private JButton newInterface       = new JButton("New Interface");
	private JButton deleteInterface    = new JButton("Delete Interface");
	private JPanel header              = new JPanel();
	// interfaces Menu
	private JToolBar interfaceBar      = new JToolBar();
	private JButton[] interfaceButton  = new JButton[100];
	private JScrollPane interfacePanel = new JScrollPane(interfaceBar);
	// Interfaces
	private JLabel errorInterface      = new JLabel("    no Interface!");
	private Interface[] inter          = new Interface[100];
	private JPanel center              = new JPanel();
	private int interCount             = 0;
	private int actualInter            = 0;
	// login
	private JPanel loginPanel          = new JPanel();
	private JPanel passwordPanel       = new JPanel();
	private JPasswordField loginField  = new JPasswordField(15);
	private JButton loginButton        = new JButton("Login");
	
	public InterfaceManager()
	{
		setTitle("InterfaceManager_0.0");
		
		header.setLayout(new BoxLayout(header, BoxLayout.PAGE_AXIS));
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.LINE_AXIS));
		
		interfaceBar.setFloatable(false);
		toolBar.setFloatable(false);
		interfaceBar.setOrientation(JToolBar.VERTICAL);
		center.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		deleteInterface.setEnabled(false);
		
		newInterface.addActionListener(this);
		newInterface.setActionCommand("newInterface");
		deleteInterface.addActionListener(this);
		deleteInterface.setActionCommand("deleteInterface");
		login.addActionListener(this);
		login.setActionCommand("login");
		loginButton.addActionListener(this);
		loginButton.setActionCommand("loginButton");
		
		// header Menus
		toolBar.add(login);
		toolBar.addSeparator();
		toolBar.add(newInterface);
		toolBar.add(deleteInterface);
		header.add(toolBar);
		header.add(new JLabel(" "));
		// interface Menu
		interfaceBar.add(new JLabel("Interface Menu      "));
		// interfaces
		center.setLayout(cardLayout);
		errorInterface.setForeground(Color.RED);
		center.add(errorInterface, "noInterface");
		center.add(new JLabel("   Interface deleted"), "deleteInterface");
		// Login
		passwordPanel.add(new JLabel("Password: "));
		passwordPanel.add(loginField);
		loginPanel.add(passwordPanel);
		loginPanel.add(loginButton);
		center.add(loginPanel, "login");
		
		add(header, BorderLayout.NORTH);
		add(interfacePanel, BorderLayout.WEST);
		add(center, BorderLayout.CENTER);
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().startsWith("saveSettings"))
		{
			String interID = e.getActionCommand().replaceAll("saveSettings", "");
			interfaceButton[Integer.parseInt(interID)].setText(inter[Integer.parseInt(interID)].getName());
		}
		
		if(e.getActionCommand().equals("login"))
		{
			cardLayout.show(center, "login");
			deleteInterface.setEnabled(false);
		}
		
		if(e.getActionCommand().equals("newInterface"))
		{
			inter[interCount] = new Interface(this, interCount);
			center.add(inter[interCount].getPanel(), "inter"+interCount);
			cardLayout.show(center, "inter"+interCount);
			interfaceButton[interCount] = new JButton("Interface"+interCount);
			interfaceButton[interCount].addActionListener(this);
			interfaceButton[interCount].setActionCommand("inter"+interCount);
			interfaceBar.add(interfaceButton[interCount]);
			actualInter = interCount;
			interCount++;
			deleteInterface.setEnabled(true);
		}
		
		if(e.getActionCommand().equals("deleteInterface"))
		{
			inter[actualInter] = null;
			interfaceBar.remove(interfaceButton[actualInter]);
			cardLayout.show(center, "deleteInterface");
			deleteInterface.setEnabled(false);
				
			if(interCount>0)
				interCount--;
			actualInter = 0;
			repaint();
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

	public static void main(String[] args)
	{
		InterfaceManager iManager = new InterfaceManager();
		iManager.setVisible(true);
		iManager.setSize(400,500);
		iManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
