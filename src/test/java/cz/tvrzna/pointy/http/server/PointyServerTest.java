package cz.tvrzna.pointy.http.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;

import cz.tvrzna.pointy.exceptions.InternalServerErrorException;
import cz.tvrzna.pointy.http.HttpStatus;
import cz.tvrzna.pointy.router.PointyEndpoint;
import cz.tvrzna.pointy.router.PointyRoute;
import cz.tvrzna.pointy.server.PointyServer;

public class PointyServerTest
{

	@Test
	public void testPointyServer() throws InterruptedException, IOException
	{
		PointyEndpoint endpoint = new PointyEndpoint()
		{
			int counter = 0;

			PointyRoute routeA = new PointyRoute("/testEndpoint")
			{
			};

			@Override
			public void onInit()
			{
				addRoute(routeA);
				routeA.ANY("", context -> {
					counter++;
					if (counter >= 2)
					{
						throw new InternalServerErrorException("Error");
					}
					else
					{
						context.send("ok");
					}
				});
			}
		};

		PointyServer server = new PointyServer("", 0, endpoint);
		assertFalse(server.isRunning());
		server.stop();
		server.start();
		assertTrue(server.isRunning());

		assertEquals(HttpStatus.OK_200, doConnection(server.getIpAddress(), server.getPort()));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, doConnection(server.getIpAddress(), server.getPort()));

		Thread.sleep(1000l);
		server.stop();
	}

	private int doConnection(String ip, int port) throws IOException
	{
		HttpURLConnection conn = (HttpURLConnection) new URL("http://" + ip + ":" + port + "/testEndpoint").openConnection();
		conn.connect();
		return conn.getResponseCode();
	}

}
