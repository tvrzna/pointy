package cz.tvrzna.pointy.http.router;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import cz.tvrzna.pointy.exceptions.InternalServerErrorException;
import cz.tvrzna.pointy.http.HttpContext;
import cz.tvrzna.pointy.router.PointyEndpoint;
import cz.tvrzna.pointy.router.PointyRoute;
import cz.tvrzna.pointy.router.PointyRouteGroup;
import cz.tvrzna.pointy.router.PointyStaticRoute;

@ExtendWith(MockitoExtension.class)
public class PointyEndpointTest
{

	@Mock
	private Socket socket;

	@Mock
	private SocketAddress socketAddress;

	private String readResponse(ByteArrayOutputStream baos)
	{
		String strResponse = baos.toString();
		String[] lines = strResponse.replace("\r", "").split("\n");

		StringBuilder body = new StringBuilder();

		boolean isBody = false;
		for (String line : lines)
		{
			if (line.isEmpty())
			{
				isBody = true;
				continue;
			}
			if (isBody)
			{
				if (body.length() > 0)
				{
					body.append("\n");
				}
				body.append(line);
			}
		}
		return body.toString();
	}

	@Test
	public void testNoRoutes() throws IOException
	{
		final String httpRequest = "GET /test/url/blabla HTTP/1.1\n\ntest message";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
			}
		};
		endpoint.handle(context);

		assertEquals("404 File Not Found", readResponse(baos));
	}

	@Test
	public void testSingleRoute() throws IOException
	{
		final String httpRequest = "GET /test/route HTTP/1.1\n\ntest message";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyRoute route = new PointyRoute("/test");
		route.ANY("/route", ctx -> ctx.send("ok"));

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addRoute(route);
			}
		};
		endpoint.onInit();
		endpoint.handle(context);

		assertEquals("ok", readResponse(baos));
	}

	@Test
	public void testSingleRouteWithFilter() throws IOException
	{
		final String httpRequest = "GET /test/route HTTP/1.1\n\ntest message";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyRouteGroup group = new PointyRouteGroup("");
		group.ANY("/.*", ctx -> ctx.setHeader("TEST", "abc"));

		PointyRoute route = new PointyRoute("/test");
		route.ANY("/route", ctx -> ctx.send("ok"));
		group.addRoute(route);

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addRouteGroup(group);
			}
		};
		endpoint.onInit();
		endpoint.handle(context);

		assertEquals("ok", readResponse(baos));
	}

	@Test
	public void testMultipleRoutes() throws IOException
	{
		final String httpRequest = "GET /test2/unroute HTTP/1.1\n\ntest message";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyRoute route = new PointyRoute("/test");
		route.ANY("/unroute", ctx -> ctx.send("nok1"));
		route.ANY("/route", ctx -> ctx.send("ok1"));

		PointyRoute route2 = new PointyRoute("/test2");
		route2.ANY("/unroute", ctx -> ctx.send("nok2"));
		route2.ANY("/route", ctx -> ctx.send("ok2"));

		PointyRoute route3 = new PointyRoute("/test3");
		route3.ANY("/unroute", ctx -> ctx.send("nok3"));
		route3.ANY("/route", ctx -> ctx.send("ok3"));

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addRoute(route);
				addRoute(route2);
				addRoute(route3);
			}
		};
		endpoint.onInit();
		endpoint.handle(context);

		assertEquals("nok2", readResponse(baos));
	}

	@Test
	public void testSingleRouteGroup() throws IOException
	{
		final String httpRequest = "GET /group/test/get HTTP/1.1\n\ntest message";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyRouteGroup group = new PointyRouteGroup("/group");
		PointyRoute route = new PointyRoute("/test");
		route.GET("/get", ctx -> ctx.send("ok"));
		group.addRoute(route);

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addRouteGroup(group);
			}
		};
		endpoint.onInit();
		endpoint.handle(context);

		assertEquals("ok", readResponse(baos));
	}

	@Test
	public void testSingleRouteGroupWithoutRoute() throws IOException
	{
		final String httpRequest = "GET /group/get HTTP/1.1\n\ntest message";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyRouteGroup group = new PointyRouteGroup("/group");
		group.GET("/get", ctx -> ctx.send("ok"));

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addRouteGroup(group);
			}
		};
		endpoint.onInit();
		endpoint.handle(context);

		assertEquals("ok", readResponse(baos));
	}

	@Test
	public void testMultipleRouteGroups() throws IOException
	{
		final String httpRequest = "POST /group/test3/get HTTP/1.1\n\ntest message";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyRouteGroup group = new PointyRouteGroup("/group");
		PointyRoute route = new PointyRoute("/test");
		route.GET("/get", ctx -> ctx.send("ok1"));
		group.addRoute(route);

		PointyRouteGroup group2 = new PointyRouteGroup("/group");
		PointyRoute route2 = new PointyRoute("/test2");
		route2.GET("/get", ctx -> ctx.send("ok2"));
		group2.addRoute(route2);

		PointyRouteGroup group3 = new PointyRouteGroup("/group");
		PointyRoute route3 = new PointyRoute("/test3");
		route3.GET("/get", ctx -> ctx.send("ok3"));
		route3.POST("/get", ctx -> ctx.send("nok3"));
		group3.addRoute(route3);

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addRouteGroup(group);
				addRouteGroup(group2);
				addRouteGroup(group3);
			}
		};
		endpoint.onInit();
		endpoint.handle(context);

		assertEquals("nok3", readResponse(baos));
	}

	@Test
	public void testNestedRouteGroups() throws IOException
	{
		final String httpRequest = "POST /group/subgroup/next-subgroup/route/uri HTTP/1.1\n\ntest message";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyRouteGroup group = new PointyRouteGroup("/group");
		PointyRouteGroup subGroup = new PointyRouteGroup("/subgroup");
		PointyRouteGroup subGroupB = new PointyRouteGroup("/subgroupB");
		PointyRouteGroup nextSubGroup = new PointyRouteGroup("/next-subgroup");

		group.addRouteGroup(subGroupB);
		group.addRouteGroup(subGroup);
		subGroup.addRouteGroup(nextSubGroup);

		PointyRoute route = new PointyRoute("/route");
		route.ANY("/uri", ctx -> ctx.send("ok"));
		nextSubGroup.addRoute(route);

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addRouteGroup(group);
			}
		};
		endpoint.onInit();
		endpoint.handle(context);

		assertEquals("ok", readResponse(baos));
	}

	@Test
	public void testServeStatic() throws IOException
	{
		final String httpRequest = "GET / HTTP/1.1";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyStaticRoute staticRoute = new PointyStaticRoute("/", "testapp");

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addStaticRoute(staticRoute);
			}
		};
		endpoint.onInit();
		endpoint.handle(context);

		assertTrue(readResponse(baos).length() > 500);
	}

	@Test
	public void testServeStaticNotFound() throws IOException
	{
		final String httpRequest = "GET /somthing-that-should-not-exist HTTP/1.1";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyStaticRoute staticRoute = new PointyStaticRoute("/", "webapp");

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addStaticRoute(staticRoute);
			}
		};
		endpoint.onInit();
		endpoint.handle(context);

		assertEquals("404 File Not Found", readResponse(baos));
	}

	@Test
	public void testExceptionHandler() throws IOException
	{
		final String httpRequest = "GET /route HTTP/1.1";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpContext context = new HttpContext(socket);

		PointyRoute route = new PointyRoute("/route");
		route.ANY("", ctx -> {
			throw new InternalServerErrorException("Expected exception");
		});

		PointyEndpoint endpoint = new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				addRoute(route);
			}
		};
		endpoint.onInit();
		endpoint.setExceptionHandler(endpoint.getExceptionHandler());

		endpoint.handle(context);

		assertEquals("Expected exception", readResponse(baos));
	}

}
