package cz.tvrzna.pointy.http.router;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import cz.tvrzna.pointy.http.HttpContext;
import cz.tvrzna.pointy.router.PointyRouteHandler;

public class PointyRouteHandlerTest
{
	@Test
	public void testRouteHandler()
	{
		PointyRouteHandler handler = context -> context.send("");

		handler.handle(Mockito.mock(HttpContext.class));
	}
}
