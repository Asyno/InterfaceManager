package userGUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


public class InterfaceMonitor implements DocumentListener, Runnable
{
	private JTextArea   monitorTextArea = new JTextArea(17,30);
	private JScrollPane scroll          = new JScrollPane(monitorTextArea);
	private JTextField  monitorStatus   = new JTextField(20);
	private JTextField  time            = new JTextField();
	private JPanel      main            = new JPanel();
	private File        dir             = new File("Logfiles");
	private File        file;
	private Interface   inter;
	private int         lastAreaLength = 0;
	
	public InterfaceMonitor(Interface inter)
	{
		this.inter     = inter;
		main           .setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		monitorTextArea.setEditable(false);
		monitorStatus  .setEditable(false);
		time           .setEditable(false);
		time           .setHorizontalAlignment(JTextField.RIGHT);
		
		main.add(scroll);
		main.add(new JLabel(" "));
		main.add(monitorStatus);
		main.add(time);
		monitorTextArea.setText("STOP");
		monitorTextArea.getDocument().addDocumentListener(this);
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try {
				Thread.sleep(250);
				
				time.setText(getTime());
				scroll.getVerticalScrollBar().setValue(monitorTextArea.getHeight());
			}
			catch (InterruptedException e2)   {}
		}
	}

	public void start()
	{
		new Thread(this).start();
	}
	
	public JPanel main()
	{
		return main;
	}
	
	public void startMonitor()
	{
		monitorTextArea.append("\r\nSTART\r\n");
	}
	
	public void stopMonitor()
	{
		monitorTextArea.append("STOP\r\n\r\n");
		monitorStatus.setText("Disconnected");
	}
	
	public JTextArea getTextArea()
	{
		return monitorTextArea;
	}
	
	public JTextField getTextField()
	{
		return monitorStatus;
	}
	
	private String getTime()
	{
		GregorianCalendar date = new GregorianCalendar();
		
		int hour = date.get(Calendar.HOUR_OF_DAY);
		String hourLast = ""+hour;
		if(hour<10)
			hourLast = "0"+hour;
		
		int minute = date.get(Calendar.MINUTE);
		String minuteLast = ""+minute;
		if(minute<10)
			minuteLast = "0"+minute;
		
		int second = date.get(Calendar.SECOND);
		String secondLast = ""+second;
		if(second<10)
			secondLast = "0"+second;
		return hourLast+":"+minuteLast+":"+secondLast;
	}
	
	private String getDate()
	{
		GregorianCalendar date = new GregorianCalendar();
		int day = date.get(Calendar.DAY_OF_MONTH);
		String dayLast = ""+day;
		if(day<10)
			dayLast = "0"+day;
		int month = date.get(Calendar.MONTH)+1;
		String monthLast = ""+month;
		if(month<10)
			monthLast = "0"+month;
		int year = date.get(Calendar.YEAR)-2000;
		String yearLast = ""+year;
		
		return yearLast+monthLast+dayLast;
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		try
		{
			dir .mkdir();
			file = new File("Logfiles" + File.separator + (inter.getTyp()+"_"+inter.getName()+"-"+inter.getID()+"_"+getDate()+".log"));
		
			int i = e.getDocument().getLength()-lastAreaLength;
			FileWriter writer = new FileWriter(file,true);
			writer.write(e.getDocument().getText(lastAreaLength, i));
			writer.flush();
			writer.close();
			lastAreaLength = e.getDocument().getLength();
		}
		catch (BadLocationException e1) {}
		catch (IOException e1)          {JOptionPane.showMessageDialog(null, "An error ocures, please check the logfile.", "Error", JOptionPane.ERROR_MESSAGE);}
		
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {}

	@Override
	public void removeUpdate(DocumentEvent arg0) {}
}