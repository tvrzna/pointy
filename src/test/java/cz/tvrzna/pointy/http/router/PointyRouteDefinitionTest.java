package cz.tvrzna.pointy.http.router;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cz.tvrzna.pointy.router.PointyRouteDefinition;
import cz.tvrzna.pointy.router.PointyRouteHandler;

public class PointyRouteDefinitionTest
{
	private static final String URI = "/testUri";
	private static final String METHOD = "POST";
	private static final PointyRouteHandler HANDLER = context -> context.send("hello");

	@Test
	public void testGetUri()
	{
		PointyRouteDefinition def = new PointyRouteDefinition(URI, METHOD, HANDLER);
		assertEquals(URI, def.getUri());
	}

	@Test
	public void testGetMethod()
	{
		PointyRouteDefinition def = new PointyRouteDefinition(URI, METHOD, HANDLER);
		assertEquals(METHOD, def.getMethod());
	}

	@Test
	public void testGetHandler()
	{
		PointyRouteDefinition def = new PointyRouteDefinition(URI, METHOD, HANDLER);
		assertEquals(HANDLER, def.getRouteHandler());
	}
}
