
package manage;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.bootstrap.HttpServer;

import userGUI.InterfaceData;
import userGUI.InterfaceSetting_PMS_FIAS;

public class InterfacePMS_FIAS implements InterfaceTyp
{	
	private InterfaceData interData;
	private boolean       isStarted = false;
	private boolean       isStoped  = true;
	private int           iD;
	
	private InterfaceSetting_PMS_FIAS setting;
	
	private int n         = 0;
	private int sendCount = 0;
	
	
	public InterfacePMS_FIAS(InterfaceData data, int iD, Object setting)
	{
		this.interData = data;
		this.iD        = iD;
		this.setting   = (InterfaceSetting_PMS_FIAS)setting;
	}
	
	@Override
	public String[] getData(String dataString)
	{
		char   temp[];
		String output[] = null;
		String data = dataString.replaceAll("", "").replaceAll("", "");
		data = data.replaceAll("(DA\\d*\\|)", "");
		data = data.replaceAll("(TI\\d*\\|)", "");
		
		if(data.equals("start"))
			output = new String[]{"LS|"+getTime()};
		if(data.equals("stop"))
			output = new String[]{"LE|"+getTime()};
		
		temp = data.toCharArray();
		
		switch(temp[0]+""+temp[1])
		{
			case("LS"):
				isStoped = false;
				if(isStarted)
					output = new String[]{"LA|"+getTime()};
				else
				{
					if(setting.xMessage())
						output = new String[19];
					else
						output = new String[11];
					output[0] = "LD|"+getTime()+"|IFPB|V#1.0|RT52|";
					output[1] = "LR|RIGI|FLDATIRNGSG#GNGFGTGAGDGVGLNP";
					if(setting.tvRights())
						output[1] += "TVVR";
					if(setting.devinable())
						output[1] += "A0A1A2A3A4A5A6A7A8A9";
					output[1] += "SF";
					output[2] = "LR|RIGC|FLDATIRNGSG#ROGNGFGTGAGDGVGLNP";
					if(setting.tvRights())
						output[2] += "TVVR";
					if(setting.devinable())
						output[2] += "A0A1A2A3A4A5A6A7A8A9";
					output[2] += "SF";
					output[3] = "LR|RIGO|FLDATIRNGSG#"+"SF";
					output[4] = "LR|RIRE|FLRNG#CSCTRS";
					if(setting.dnd())
						output[4] += "DN";
					if(setting.tvRights())
						output[4] += "TV";
					if(setting.message())
						output[4] += "MLVM";
					output[5] = "LR|RIPS|FLDATIRNPTDUDDTACTMAM#MPSOS1D1SCT1T2T3|";
					output[6] = "LR|RIPA|FLRNASDATIWSP#SO|";
					output[7] = "LR|RIWR|FLRNDATI|";
					output[8] = "LR|RIWC|FLRNDATI|";
					output[9] = "LR|RIWA|FLRNDATIAS|";
					if(setting.xMessage())
					{
						output[10] = "LR|RIXL|FLDATIRNG#MIMT|";
						output[11] = "LR|RIXT|FLDATIRNG#MIMT|";
						output[12] = "LR|RIXD|FLDATIRNG#MI|";
						output[13] = "LR|RIXM|FLDATIRNG#MI|";
						output[14] = "LR|RIXR|FLDATIRNG#|";
						output[15] = "LR|RIXI|FLDATIRNG#BDBIDCF#FD|";
						output[16] = "LR|RIXB|FLDATIRNG#BA|";
						output[17] = "LR|RIXC|FLDATIRNG#BAASCT|";
						output[18] = "LA|"+getTime();
					}
					else
						output[10] = "LA|"+getTime();
					isStarted = true;
				}
				break;
				
			case("GI"):
				String dataGI[] = data.split("\\|");
				String sendGI[] = new String[6];
				char   tempGI[];
				n = 0;
				
				while(n<dataGI.length)
				{
					tempGI = null;
					tempGI = dataGI[n].toCharArray();
					
					if(tempGI.length>1)
					{
						switch(tempGI[0]+""+tempGI[1])
						{
							case("RN"):
								sendGI[0] = getData(dataGI[n],tempGI);
								break;
							case("GN"):
								sendGI[1] = getData(dataGI[n],tempGI);
								break;
							case("GF"):
								sendGI[2] = getData(dataGI[n],tempGI);
								break;
							case("GT"):
								sendGI[3] = getData(dataGI[n],tempGI);
								break;
							case("G#"):
								sendGI[4] = getData(dataGI[n],tempGI);
								break;
							case("NP"):
								if(getData(dataGI[n],tempGI).equals("Y")&&(!setting.ignoreNP()))
									sendGI[5] = "Yes";
								else
									sendGI[5] = "No";
								break;
						}
					}
					n++;
				}
				interData.setGuest(sendGI);
				interData.setData(getDataTime(), iD, data);
				n = 0;
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
				
			case("GC"):
				String dataGC[] = data.split("\\|");
				String sendGC[] = new String[6];
				char   tempGC[];
				n = 0;
				
				while(n<dataGC.length)
				{
					tempGC = null;
					tempGC = dataGC[n].toCharArray();
					
					if(tempGC.length>1)
					{
						switch(tempGC[0]+""+tempGC[1])
						{
							case("RN"):
								sendGC[0] = getData(dataGC[n],tempGC);
								break;
							case("RO"):
								String gcRO[] = {getData(dataGC[n],tempGC),"VACANT","","","","",""};
								interData.setGuest(gcRO);
								break;
							case("GN"):
								sendGC[1] = getData(dataGC[n],tempGC);
								break;
							case("GF"):
								sendGC[2] = getData(dataGC[n],tempGC);
								break;
							case("GT"):
								sendGC[3] = getData(dataGC[n],tempGC);
								break;
							case("G#"):
								sendGC[4] = getData(dataGC[n],tempGC);
								break;
							case("NP"):
								if(getData(dataGC[n],tempGC).equals("Y")&&(!setting.ignoreNP()))
									sendGC[5] = "Yes";
								else
									sendGC[5] = "No";
								break;
						}
					}
					n++;
				}
				interData.setGuest(sendGC);
				interData.setData(getDataTime(), iD, data);
				n = 0;
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
				
			case("GO"):
				n = 0;
				String dataGO[] = data.split("\\|");
				String sendGO[] = new String[6];
				char   tempGO[];
				
				while(n<dataGO.length)
				{
					tempGO = null;
					tempGO = dataGO[n].toCharArray();
					
					if(tempGO.length>1)
					{
						switch(tempGO[0]+""+tempGO[1])
						{
							case("RN"):
								sendGO[0] = getData(dataGO[n],tempGO);
								break;
						}
					}
					n++;
				}
				sendGO[1] = "VACANT";
				sendGO[2] = "";
				sendGO[3] = "";
				sendGO[4] = "";
				sendGO[5] = "";
				interData.setGuest(sendGO);
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("RE"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("PS"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("PA"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("WR"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("WC"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("WA"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("XL"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("XT"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("XD"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("XM"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("XR"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("XI"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("XB"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("XC"):
				interData.setData(getDataTime(), iD, data);
				if(setting.sendAck())
					output = new String[]{""+6};
				break;
			case("LE"):
				if(setting.sendAck())
					output = new String[]{""+6,"LE|"+getTime()};
				else
					output = new String[]{"LE|"+getTime()};
				isStoped = true;
				break;
		}

		return output;
	}
	
	@Override
	public String[] getNextSend()
	{
		if(!isStoped)
		{
			String data[] = interData.getData(iD);
			String temp[];
			String send;
			
			if(!(data == null))
			{
				temp = data[2].split("\\|");
				send = temp[0]+getTime();
				for(int i=1;i<temp.length;i++)
					send += temp[i];
				return new String[]{send};
			}
			else
				sendCount++;
				if((sendCount>100)&&(setting.sendLA()))
				{
					sendCount = 0;
					return new String[]{"LA|"+getTime()};
				}
				else
					return null;
		}
		else
			return null;
	}
	
	private String getData(String data, char array[])
	{
		int n = 2;
		String returnString = "";
		while(n<data.length())
		{
			returnString += ""+array[n];
			n++;
		}
		return returnString;
	}
	
	private String getDataTime()
	{
		GregorianCalendar date = new GregorianCalendar();
		String time;
		int year  = date.get(Calendar.YEAR)-2000;
		
		int month = date.get(Calendar.MONTH)+1;
		String monthLast = ""+month;
		if(month<10)
			monthLast = "0"+month;
		
		int day   = date.get(Calendar.DAY_OF_MONTH);
		String dayLast = ""+day;
		if(day<10)
			dayLast = "0"+day;
		
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
		
		time = year+"."+monthLast+"."+dayLast+" - "+hourLast+":"+minuteLast+":"+secondLast;
		
		return time;
	}
	
	private String getTime()
	{
		GregorianCalendar date = new GregorianCalendar();
		String time;
		int year  = date.get(Calendar.YEAR)-2000;
		
		int month = date.get(Calendar.MONTH)+1;
		String monthLast = ""+month;
		if(month<10)
			monthLast = "0"+month;
		
		int day   = date.get(Calendar.DAY_OF_MONTH);
		String dayLast = ""+day;
		if(day<10)
			dayLast = "0"+day;
		
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
		
		time = "DA"+year+""+monthLast+""+dayLast+"|TI"+hourLast+""+minuteLast+""+secondLast+"|";
		
		return time;
	}

	@Override
	public HttpUriRequest getNextRequest(String host, int port)
	{
		return null;
	}

	@Override
	public HttpServer getRequestHandler(HttpServer server)
	{
		return null;
	}
}
