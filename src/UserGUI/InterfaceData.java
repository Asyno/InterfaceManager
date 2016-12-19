package userGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class InterfaceData
{
	private JPanel     main              = new JPanel();
	private JSplitPane split             = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private JToolBar   tool              = new JToolBar(JToolBar.VERTICAL);
	
	// data Guest display
	private String headerGuest[]         = {"Room Number","Guest Last Name","Guest Name","Guest Title","Guest ID","No Post"};
	private String guestRow[][];
	private DefaultTableModel guestModel = new DefaultTableModel(guestRow, headerGuest);
	private JTable tableGuest            = new JTable(guestModel);
	private final TableRowSorter<DefaultTableModel> SORTER  = new TableRowSorter<DefaultTableModel>(guestModel);
	private JScrollPane scrollGuest      = new JScrollPane(tableGuest);
	// data communication
	private String dataHeader[]          = {"Time","Source","Data","Interface"};
	private String dataRow[][];
	private DefaultTableModel dataModel  = new DefaultTableModel(dataRow,dataHeader);
	private JTable tableData             = new JTable(dataModel);
	private JScrollPane scrollData       = new JScrollPane(tableData);
	// data save
	private File       dir               = new File("System");
	private File       fileData;
	private File       fileGuest;
	private FileWriter writer;
	// data check
	private InterfaceManager manager;
	
	public InterfaceData(InterfaceManager i)
	{
		manager = i;
		tableGuest .setRowSorter(SORTER);
		tableData.getColumn("Time").setMaxWidth(120);
		tableData.getColumn("Time").setWidth(120);
		tableData.getColumn("Time").setMinWidth(120);
		tableData.getColumn("Source").setMaxWidth(50);
		tableData.getColumn("Interface").setMaxWidth(0);
		main       .setLayout     (new BorderLayout());
		scrollGuest.setMinimumSize(new Dimension(100,150));
		tool       .setName("Data Monitor");
		setGuestAfterRestart();
		setDataAfterRestart ();
		
		split.setLeftComponent (scrollGuest);
		split.setRightComponent(scrollData);
		tool .add(split, BorderLayout.CENTER);
		
		main .add(new JLabel("Interface Datas"));
		main .add(tool);
		
		try
		{
			dir .mkdir();
			fileData  = new File("System" + File.separator + ("data.log"));
			fileData.createNewFile();
			fileGuest = new File("System" + File.separator + ("guest.log"));
			fileGuest.createNewFile();
		}
		catch (IOException e1)          {JOptionPane.showMessageDialog(null, "An error ocures, please check the logfile.", "Error", JOptionPane.ERROR_MESSAGE);}
		catch (NullPointerException e2) {};
	}
	
	private void setDataAfterRestart()
	{
		try
		{
			BufferedReader r = new BufferedReader(new FileReader("System" + File.separator + ("data.log")));
			String  line;
			String  temp[];
			
			while(!((line = r.readLine())==null))
			{
				line = line.replaceAll("null","");
				temp = line.split("\\|-\\|");
				dataModel.addRow(temp);
			}
			
			r.close();
		}
		catch (NullPointerException  e) {}
		catch (FileNotFoundException e) {}
		catch (IOException e)           {JOptionPane.showMessageDialog(null, "An error ocures, please check the writing permission.", "Error", JOptionPane.ERROR_MESSAGE);}
	}
	
	private void setGuestAfterRestart()
	{
		try
		{
			BufferedReader r = new BufferedReader(new FileReader("System" + File.separator + ("guest.log")));
			String  line;
			String  temp[];
			int     n=0;
			boolean test = false;
			
			while(!((line = r.readLine())==null))
			{
				line = line.replaceAll("null","");
				temp = line.split("\\|-\\|");
				while(n<guestModel.getRowCount())
				{
					if(guestModel.getValueAt(n,0).equals(temp[0]))
					{
						test = true;
						break;
					}
					n++;
				}
				n=0;
				if(!test)
					guestModel.addRow(temp);
			}
			
			r.close();
		}
		catch (NullPointerException  e) {}
		catch (FileNotFoundException e) {}
		catch (IOException e)           {JOptionPane.showMessageDialog(null, "An error ocures, please check the writing permission.", "Error", JOptionPane.ERROR_MESSAGE);}
	}
	
	public void setGuest(String[] data)
	{
		int n = 0;
		int i = 0;
		while(n<guestModel.getRowCount())
		{
			if(data[0].equals(guestModel.getValueAt(n, 0)))
				break;
			n++;
		}
		if(n==guestModel.getRowCount())
			guestModel.addRow(data);
		else
			while(i<5)
			{
				guestModel.setValueAt(data[i],n,i);
				i++;
			}
		n=0;
		i=0;
		try
		{
			writer = new FileWriter(fileGuest, false);
			while(n<guestModel.getRowCount())
			{
				writer.write((String) guestModel.getValueAt(n,0)+"|-|");
				writer.write((String) guestModel.getValueAt(n,1)+"|-|");
				writer.write((String) guestModel.getValueAt(n,2)+"|-|");
				writer.write((String) guestModel.getValueAt(n,3)+"|-|");
				writer.write((String) guestModel.getValueAt(n,4)+"|-|");
				writer.write((String) guestModel.getValueAt(n,5)+"|-|\r\n");
				n++;
			}
			writer.flush();
			writer.close();
		}
		catch (IOException e)         {e.printStackTrace();}
		catch (NullPointerException e){};
		n=0;
	}
	
	public void setData(String time, int iD, String data)
	{
		dataModel.addRow(new String[]{time,""+iD,data,"|"+iD+"|"});
		try
		{
			writer = new FileWriter(fileData,true);
			writer.append(time+"|-|"+iD+"|-|"+data+"|-|"+"|"+iD+"|"+"\r\n");
			writer.flush();
			writer.close();
		}
		catch (IOException e)          {e.printStackTrace();try {writer.close();}catch(IOException e1) {}}
		catch (NullPointerException e) {};
	}
	
	public String[] getData(int iD)
	{
		String data[];
		if(dataModel.getRowCount()<=0)
			return null;
		int n = 0;
		for(n=0;n<dataModel.getRowCount();n++)
		{
			if(!(dataModel.getValueAt(n,3)==null)&&!((String)dataModel.getValueAt(n,3)).contains("|"+iD+"|"))
				break;
		}
		if(n<dataModel.getRowCount())
		{
			data = new String[]{(String) dataModel.getValueAt(n, 0),(String) dataModel.getValueAt(n, 1), (String)dataModel.getValueAt(n, 2)};
			checkData(n,iD);
			return data;
		}
		else
			return null;
	}
	
	private void checkData(int row,int iD)
	{
		String interTest  = manager.getInterID();
		int    interCount = interTest.split("\\|").length;
		boolean test      = true;
		
		
		if(!((String)dataModel.getValueAt(row,3)).contains("|"+iD+"|"))
			dataModel.setValueAt(dataModel.getValueAt(row,3)+"|"+iD+"|",row,3);
		
		for(int i=0;i<interCount;i++)
		{
			if(!((String)dataModel.getValueAt(row,3)).contains("|"+iD+"|"))
				test = false;
		}
		if(test)
			dataModel.removeRow(row);
		
		try
		{
			FileWriter w = new FileWriter("System" + File.separator + "data.log", false);
			
			for(int i=0;i<dataModel.getRowCount();i++)
			{
				w.write((String)dataModel.getValueAt(i,0)+"|-|");
				w.write((String)dataModel.getValueAt(i,1)+"|-|");
				w.write((String)dataModel.getValueAt(i,2)+"|-|");
				w.write((String)dataModel.getValueAt(i,3)+"\r\n");
			}
			w.flush();
			w.close();
		}
		catch (IOException e)          {e.printStackTrace();try {writer.close();}catch(IOException e1) {}}
		catch (NullPointerException e) {};
	}
	
	public JPanel getMain()
	{
		return main;
	}
	
}

