package userGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InterfaceSetting_PMS_FIAS implements ActionListener
{
	private JPanel    main       = new JPanel();
	private Box       label      = new Box(BoxLayout.PAGE_AXIS);
	private Box       box        = new Box(BoxLayout.PAGE_AXIS);
	
	private JCheckBox sendAck    = new JCheckBox();
	private JCheckBox ignoreNP   = new JCheckBox();
	private JCheckBox devinable  = new JCheckBox();
	private JCheckBox tvRights   = new JCheckBox();
	private JCheckBox dnd        = new JCheckBox();
	private JCheckBox message    = new JCheckBox();
	private JCheckBox xMessage   = new JCheckBox();
	private JCheckBox sendLA     = new JCheckBox();
	
	private Preferences prefs    = Preferences.userRoot().node(this.getClass().getName());
	private int         iD       = 0;
	
	public InterfaceSetting_PMS_FIAS(int iD)
	{
		this     .iD = iD;
		main     .setLayout(new BoxLayout(main,BoxLayout.LINE_AXIS));
		sendAck  .setActionCommand("sendAck");
		sendAck  .addActionListener(this);
		ignoreNP .setActionCommand("ignoreNP");
		ignoreNP .addActionListener(this);
		devinable.setActionCommand("devinable");
		devinable.addActionListener(this);
		tvRights .setActionCommand("tvRights");
		tvRights .addActionListener(this);
		dnd      .setActionCommand("dnd");
		dnd      .addActionListener(this);
		message  .setActionCommand("message");
		message  .addActionListener(this);
		xMessage .setActionCommand("xMessage");
		xMessage .addActionListener(this);
		sendLA   .setActionCommand("sendLA");
		sendLA   .addActionListener(this);
		
		sendAck  .setSelected(prefs.getBoolean(iD+"PMS_FIAS_sendAck"  , false));
		ignoreNP .setSelected(prefs.getBoolean(iD+"PMS_FIAS_ignoreNP" , false));
		devinable.setSelected(prefs.getBoolean(iD+"PMS_FIAS_devinable", false));
		tvRights .setSelected(prefs.getBoolean(iD+"PMS_FIAS_tvRights" , false));
		dnd      .setSelected(prefs.getBoolean(iD+"PMS_FIAS_dnd"      , false));
		message  .setSelected(prefs.getBoolean(iD+"PMS_FIAS_message"  , false));
		xMessage .setSelected(prefs.getBoolean(iD+"PMS_FIAS_xMessage" , false));
		sendLA   .setSelected(prefs.getBoolean(iD+"PMS_FIAS_sendLA"   , false));

		label.add(Box.createGlue());
		label.add(new JLabel("Send always an ACK  "));
		label.add(Box.createGlue());
		box  .add(sendAck);
		
		label.add(new JLabel("Ignore PMS NP Flag  "));
		label.add(Box.createGlue());
		box  .add(ignoreNP);
		
		label.add(new JLabel("Request Definable Fields  "));
		label.add(Box.createGlue());
		box  .add(devinable);
		
		label.add(new JLabel("Request TV Rights  "));
		label.add(Box.createGlue());
		box  .add(tvRights);
		
		label.add(new JLabel("Do not Disturb  "));
		label.add(Box.createGlue());
		box  .add(dnd);
		
		label.add(new JLabel("send Message Waiting  "));
		label.add(Box.createGlue());
		box  .add(message);
		
		label.add(new JLabel("Extended Message handling  "));
		label.add(Box.createGlue());
		box  .add(xMessage);
		
		label.add(new JLabel("Send LA  "));
		label.add(Box.createGlue());
		box  .add(sendLA);
		
		main .add(label);
		main .add(box);
	}
	
	public void delete()
	{
		prefs.putBoolean(iD+"PMS_FIAS_sendAck"  , false);
		prefs.putBoolean(iD+"PMS_FIAS_ignoreNP" , false);
		prefs.putBoolean(iD+"PMS_FIAS_devinable", false);
		prefs.putBoolean(iD+"PMS_FIAS_tvRights" , false);
		prefs.putBoolean(iD+"PMS_FIAS_dnd"      , false);
		prefs.putBoolean(iD+"PMS_FIAS_message"  , false);
		prefs.putBoolean(iD+"PMS_FIAS_xMessage" , false);
		prefs.putBoolean(iD+"PMS_FIAS_sendLA"   , false);
	}
	
	public boolean sendAck()
	{
		return sendAck.isSelected();
	}
	
	public boolean devinable()
	{
		return devinable.isSelected();
	}
	
	public boolean tvRights()
	{
		return tvRights.isSelected();
	}
	
	public boolean ignoreNP()
	{
		return ignoreNP.isSelected();
	}
	
	public boolean dnd()
	{
		return dnd.isSelected();
	}
	
	public boolean message()
	{
		return message.isSelected();
	}
	
	public boolean xMessage()
	{
		return xMessage.isSelected();
	}
	
	public boolean sendLA()
	{
		return sendLA.isSelected();
	}
	
	public JPanel getMain()
	{
		return main;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) 
	{
		if(evt.getActionCommand().equals("sendAck"  ))
			prefs.putBoolean(iD+"PMS_FIAS_sendAck"  , sendAck  .isSelected());
		if(evt.getActionCommand().equals("ignoreNP" ))
			prefs.putBoolean(iD+"PMS_FIAS_ignoreNP" , ignoreNP .isSelected());
		if(evt.getActionCommand().equals("devinable"))
			prefs.putBoolean(iD+"PMS_FIAS_devinable", devinable.isSelected());
		if(evt.getActionCommand().equals("tvRights" ))
			prefs.putBoolean(iD+"PMS_FIAS_tvRights" , tvRights .isSelected());
		if(evt.getActionCommand().equals("dnd"      ))
			prefs.putBoolean(iD+"PMS_FIAS_dnd"      , dnd      .isSelected());
		if(evt.getActionCommand().equals("message"  ))
			prefs.putBoolean(iD+"PMS_FIAS_message"  , message  .isSelected());
		if(evt.getActionCommand().equals("xMessage" ))
			prefs.putBoolean(iD+"PMS_FIAS_xMessage" , xMessage .isSelected());
		if(evt.getActionCommand().equals("sendLA"   ))
			prefs.putBoolean(iD+"PMS_FIAS_sendLA"   , sendLA   .isSelected());
	}
}
