package userGUI;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.*;

public class InterfaceSetting implements ActionListener 
{
	private JPanel main                        = new JPanel();
	private JPanel settingPanel                = new JPanel();
	
	private CardLayout card                    = new CardLayout();
	private JComboBox<String> interTyp         = new JComboBox<String>();
	
	private InterfaceSetting_PMS_FIAS        pms_Fias;
	private InterfaceSetting_TV_Samsung_LYNC tv_Samsung_LYNC;

	private Preferences prefs    = Preferences.userRoot().node(this.getClass().getName());
	private int iD;
	private boolean started = false;
	
	public InterfaceSetting(int iD)
	{
		this.iD         = iD;
		pms_Fias        = new InterfaceSetting_PMS_FIAS(iD);
		tv_Samsung_LYNC = new InterfaceSetting_TV_Samsung_LYNC(iD);
		
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		settingPanel.setLayout(card);
		
		interTyp.addActionListener(this);
		interTyp.setActionCommand("interTyp");
		
		interTyp.addItem        ("pms_FIAS");
		interTyp.addItem        ("tv_Samsung_LYNC");
		interTyp.setSelectedItem((String)prefs.get("interTyp"+iD, "pms_FIAS"));
		started = true;
		
		main.add(new JLabel("Interface Settings"));
		main.add(new JLabel(" "));
		main.add(interTyp);
		main.add(new JLabel(" "));
		settingPanel.add(pms_Fias       .getMain(),"pms_FIAS");
		settingPanel.add(tv_Samsung_LYNC.getMain(),"tv_Samsung_LYNC");
		main.add(settingPanel);
		
		card.show(settingPanel ,(String) interTyp.getSelectedItem());
	}
	
	public JPanel getMain()
	{
		return main;
	}
	
	public String getTyp()
	{
		return (String) interTyp.getSelectedItem();
	}
	
	public Object getSetting()
	{
		switch((String)interTyp.getSelectedItem())
		{
			case("pms_FIAS"):        return pms_Fias;
			case("tv_Samsung_LYNC"): return tv_Samsung_LYNC;
		}
		return null;
	}
	
	public boolean isHTML()
	{
		switch((String)interTyp.getSelectedItem())
		{
		case("tv_Samsung_LYNC"): return true;
		}
		return false;
	}
	
	public void delete()
	{
		pms_Fias.delete();
		prefs.put("interTyp"+iD, "");
	}

	@Override
	public void actionPerformed(ActionEvent aE)
	{
		if(aE.getActionCommand().equals("interTyp"))
		{
			card.show(settingPanel ,(String) interTyp.getSelectedItem());
			if(started)
				prefs.put("interTyp"+iD,(String) interTyp.getSelectedItem());
		}
	}
}
