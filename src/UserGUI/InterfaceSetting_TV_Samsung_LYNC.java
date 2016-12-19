package userGUI;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class InterfaceSetting_TV_Samsung_LYNC
{
	private JPanel main = new JPanel();
	
	
	public InterfaceSetting_TV_Samsung_LYNC(int iD)
	{
		main.add(new JLabel("Samsung LYNC Settings"));
	}
	
	public JPanel getMain()
	{
		return main;
	}
}
