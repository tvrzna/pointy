package cz.tvrzna.pointy.http.router;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import cz.tvrzna.pointy.http.HttpContext;
import cz.tvrzna.pointy.router.PointyStaticRoute;

@ExtendWith(MockitoExtension.class)
public class PointyStaticRouteTest
{

	@Test
	public void testGetIndex()
	{
		HttpContext context = Mockito.mock(HttpContext.class);
		Mockito.when(context.getRequestUri()).thenReturn("/");

		Mockito.when(context.status(Mockito.anyInt())).thenReturn(context);
		Mockito.lenient().when(context.contentType(Mockito.anyString())).thenReturn(context);

		Mockito.lenient().doNothing().when(context).send(Mockito.any(InputStream.class));

		PointyStaticRoute route = new PointyStaticRoute("/.*", "webapp");
		route.getRouteHandler().handle(context);
	}

	@Test
	public void testExistingFile()
	{
		HttpContext context = Mockito.mock(HttpContext.class);
		Mockito.when(context.getRequestUri()).thenReturn("/css/style.css");

		Mockito.when(context.status(Mockito.anyInt())).thenReturn(context);
		Mockito.lenient().when(context.contentType(Mockito.anyString())).thenReturn(context);

		Mockito.lenient().doNothing().when(context).send(Mockito.any(InputStream.class));

		PointyStaticRoute route = new PointyStaticRoute("/.*", "webapp");
		route.getRouteHandler().handle(context);
	}

	@Test
	public void testNonExistingFile()
	{
		HttpContext context = Mockito.mock(HttpContext.class);
		Mockito.when(context.getRequestUri()).thenReturn("/something-that-does-not-exist");

		Mockito.when(context.status(Mockito.anyInt())).thenReturn(context);
		Mockito.when(context.contentType(Mockito.anyString())).thenReturn(context);
		Mockito.when(context.html()).thenReturn(context);

		Mockito.doNothing().when(context).send(Mockito.any(InputStream.class));

		PointyStaticRoute route = new PointyStaticRoute("/.*", "webapp");
		route.getRouteHandler().handle(context);
	}

	@Test
	public void testException()
	{
		HttpContext context = Mockito.mock(HttpContext.class);
		Mockito.when(context.getRequestUri()).thenReturn("/");

		Mockito.when(context.status(Mockito.anyInt())).thenReturn(context);
		Mockito.lenient().when(context.contentType(Mockito.anyString())).thenAnswer(new Answer<HttpContext>()
		{

			@Override
			public HttpContext answer(InvocationOnMock invocation) throws Throwable
			{
				throw new Exception("Test exception");
			}
		});

		PointyStaticRoute route = new PointyStaticRoute("/.*", "webapp");
		route.getRouteHandler().handle(context);
	}
}
