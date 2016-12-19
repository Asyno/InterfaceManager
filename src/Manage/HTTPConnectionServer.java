package manage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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

public class HTTPConnectionServer implements Runnable
{
	private InterfaceTyp interTyp;
	private HttpServer   server;
	private int          port;
	
	public HTTPConnectionServer(InterfaceTyp interTyp)
	{
		this.interTyp = interTyp;
	}
	
	public void start(int port)
	{
		this.port = port;
		new Thread(this).start();
	}

	@Override
	public void run()
	{
		server = interTyp.getServerSettings(port);
		try
		{
			server.start();
			server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			
			Runtime.getRuntime().addShutdownHook(new Thread(){@Override public void run() {server.shutdown(5,  TimeUnit.SECONDS);}});
		}
		catch (InterruptedException | IOException e) {e.printStackTrace();}
	}
	
	public void stop()
	{
		server.stop();
	}
}
