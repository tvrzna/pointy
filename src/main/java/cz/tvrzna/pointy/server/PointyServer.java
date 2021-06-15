package cz.tvrzna.pointy.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import cz.tvrzna.pointy.http.HttpContext;
import cz.tvrzna.pointy.router.PointyEndpoint;

/**
 * Simple HTTP Server, that serves on predefined <code>ipAddress</code> and
 * <code>port</code> web application, that is defined by {@link PointyEndpoint}
 * using standard {@link ServerSocket}. <br>
 * <code>ipAddress</code> defines interface, that will provide output for
 * server. If server should run on localhost, it could be set to
 * <code>127.0.0.1</code>. If it should run on all interfaces, it could be set
 * to <code>0.0.0.0</code>, otherwise it could be set to specific interface
 * (e.g. 192.168.1.1 to run on local intranet). <br>
 * If <code>port</code> is set as <code>-1</code>, JVM finds unused port to
 * attach server to it. <br>
 * Each request is handled via independent <code>Thread</code>, default maximum
 * of parallel requests is 16, but it set to own value.<br>
 * Request, that is added into queue, has 30 seconds to be handled, otherwise
 * connection is closed. The queue is defined as FIFO (First in, first out).<br>
 * After new instance is created, server needs to be started via
 * {@link PointyServer#start()} method.<br>
 * For stopping server it needs to be invoked {@link PointyServer#stop()} method.
 *
 * @since 0.1.0
 * @author michalt
 */
public class PointyServer
{
	private ServerSocket server;
	private final String ipAddress;
	private final int port;
	private final long waitTimeoutMs;
	private final PointyEndpoint endpoint;

	private final Semaphore semaphore;

	/**
	 * Instantiates a new <code>PointyServer</code> on defined
	 * <code>ipAddress</code>, <code>port</code>. Server will provide defined
	 * {@link PointyEndpoint}. In this case it allows to create up to 16
	 * <code>Thread</code>s to handle incoming requests and 30 second waiting timeout.
	 *
	 * @param ipAddress
	 *          the ip address
	 * @param port
	 *          the port
	 * @param endpoint
	 *          the endpoint
	 */
	public PointyServer(String ipAddress, int port, PointyEndpoint endpoint)
	{
		this(ipAddress, port, endpoint, 16, 30000l);
	}

	/**
	 * Instantiates a new <code>PointyServer</code> on defined
	 * <code>ipAddress</code>, <code>port</code>. Server will provide defined
	 * {@link PointyEndpoint}. <code>maxThreads</code> defines, how many requests
	 * could be handled in parallel.
	 *
	 * @param ipAddress
	 *          the ip address
	 * @param port
	 *          the port
	 * @param endpoint
	 *          the endpoint
	 * @param maxThreads
	 *          the max threads
	 */
	public PointyServer(String ipAddress, int port, PointyEndpoint endpoint, int maxThreads, long waitTimeoutMs)
	{
		this.ipAddress = ipAddress;
		this.port = port;
		this.endpoint = endpoint;
		this.waitTimeoutMs = waitTimeoutMs;
		semaphore = new Semaphore(maxThreads > 0 ? maxThreads : 1, true);
	}

	/**
	 * Starts the <code>PointyServer</code> as independent <code>Thread</code>,
	 * unless is already running.<br>
	 * As first step is invoking {@link PointyEndpoint#onInit()} method. It is
	 * expected, that this method contains code, that prepares any route, that
	 * should be handled by this <code>PointyServer</code>.<br>
	 * Any incoming request asks for permit to be handled. If permit is acquired,
	 * the request is handled as independent <code>Thread</code> in
	 * {@link PointyEndpoint#handle(HttpContext)}. Request could wait up to <code>waitTimeoutMs</code>
	 * milliseconds to be handled, otherwise connection is closed.<br>
	 */
	public void start()
	{
		if (isRunning())
		{
			return;
		}
		try
		{
			InetAddress address = InetAddress.getByName(ipAddress);
			server = new ServerSocket(port, 0, address);
			new Thread("pointyServer")
			{
				@Override
				public void run()
				{
					try
					{
						endpoint.onInit();

						while (!server.isClosed())
						{
							try
							{
								@SuppressWarnings("resource")
								Socket client = server.accept();
								if (semaphore.tryAcquire(waitTimeoutMs, TimeUnit.MILLISECONDS))
								{
									new Thread("pointyServerSocket")
									{
										@Override
										public void run()
										{
											try
											{
												endpoint.handle(new HttpContext(client));
												client.close();
											}
											catch (Exception e)
											{
												e.printStackTrace();
											}
											finally
											{
												semaphore.release();
											}
										};
									}.start();
								}
								else
								{
									client.close();
								}
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Stops the <code>PointyServer</code>, if is running.
	 */
	public void stop()
	{
		if (server != null)
		{
			try
			{
				server.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the port, on which is <code>PointyServer</code> started. If server is
	 * not running, it returns <code>-1</code>.
	 *
	 * @return the port
	 */
	public int getPort()
	{
		if (server != null && !server.isClosed())
		{
			return server.getLocalPort();
		}
		return -1;
	}

	/**
	 * Gets the IP address, on which is server started. If server is not running,
	 * it return empty <code>String</code>.
	 *
	 * @return the ip address
	 */
	public String getIpAddress()
	{
		if (server != null && !server.isClosed())
		{
			return server.getInetAddress().getHostAddress();
		}
		return "";
	}

	/**
	 * Checks if is running.
	 *
	 * @return true, if is running
	 */
	public boolean isRunning()
	{
		return server != null && !server.isClosed();
	}
}
