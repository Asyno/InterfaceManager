package manage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import userGUI.InterfaceData;

public class InterfaceTV_Samsung_LYNC implements InterfaceTyp
{
	
	
	public InterfaceTV_Samsung_LYNC(InterfaceData data, int iD, Object setting)
	{
		
	}

	@Override
	public String[] getData(String data)
	{
		System.out.println(data);
		return null;
	}

	@Override
	public String[] getNextSend()
	{
		return null;
	}
	
	@Override
	public HttpUriRequest getNextRequest(String host, int port)
	{
		HttpGet httpget = new HttpGet("http://127.0.0.1//openapi/display");
		return httpget;
	}

	@Override
	public HttpServer getServerSettings(int port)
	{

		HttpProcessor httpproc = HttpProcessorBuilder.create()
				.add(new ResponseDate())
				.add(new ResponseServer("InterfaceManager-HTTP/1.1"))
				.add(new ResponseContent())
				.add(new ResponseConnControl())
				.build();
		
		HttpRequestHandler myRequestHandler = new HttpRequestHandler()
		{
			public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException
			{
				response.setHeader("Content-Type", "text/xml; charset=UTF-8");
				response.setStatusCode(HttpStatus.SC_OK);
				response.setEntity(new StringEntity("<?xml version=\"1.0\" encoding=\"UTF-8\"><response><command>checkin</command><statuscode>0</statuscode></response>",StandardCharsets.UTF_8));
			}
		};


		SocketConfig socketConfig = SocketConfig.custom()
				.setSoTimeout(15000)
				.setTcpNoDelay(true)
				.build();
		
		HttpServer server = ServerBootstrap.bootstrap()
				.setListenerPort(port)
				.setHttpProcessor(httpproc)
				.setSocketConfig(socketConfig)
				.registerHandler("/openapi/display",myRequestHandler)
				.create();
		
		return server;
	}
	
	

}
