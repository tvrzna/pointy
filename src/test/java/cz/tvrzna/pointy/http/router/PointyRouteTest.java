package cz.tvrzna.pointy.http.router;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cz.tvrzna.pointy.router.PointyRoute;
import cz.tvrzna.pointy.router.PointyRouteDefinition;

public class PointyRouteTest
{
	private static final String URI = "/testUri";
	private static final String SUB_URI = "/sub";

	@Test
	public void testGetUri()
	{
		PointyRoute route = new PointyRoute(URI);
		assertEquals(URI, route.getUri());
	}

	@Test
	public void testAddGET()
	{
		PointyRoute route = new PointyRoute(URI);

		assertEquals(0, route.getRouteDefs().size());
		route.GET(SUB_URI, context -> context.send(""));
		assertEquals(1, route.getRouteDefs().size());

		PointyRouteDefinition def = route.getRouteDefs().get(0);
		assertEquals(SUB_URI, def.getUri());
		assertEquals("GET", def.getMethod());
	}

	@Test
	public void testAddPOST()
	{
		PointyRoute route = new PointyRoute(URI);

		assertEquals(0, route.getRouteDefs().size());
		route.POST(SUB_URI, context -> context.send(""));
		assertEquals(1, route.getRouteDefs().size());

		PointyRouteDefinition def = route.getRouteDefs().get(0);
		assertEquals(SUB_URI, def.getUri());
		assertEquals("POST", def.getMethod());
	}

	@Test
	public void testAddPUT()
	{
		PointyRoute route = new PointyRoute(URI);

		assertEquals(0, route.getRouteDefs().size());
		route.PUT(SUB_URI, context -> context.send(""));
		assertEquals(1, route.getRouteDefs().size());

		PointyRouteDefinition def = route.getRouteDefs().get(0);
		assertEquals(SUB_URI, def.getUri());
		assertEquals("PUT", def.getMethod());
	}

	@Test
	public void testAddGet()
	{
		PointyRoute route = new PointyRoute(URI);

		assertEquals(0, route.getRouteDefs().size());
		route.ANY(SUB_URI, context -> context.send(""));
		assertEquals(1, route.getRouteDefs().size());

		PointyRouteDefinition def = route.getRouteDefs().get(0);
		assertEquals(SUB_URI, def.getUri());
		assertEquals("ANY", def.getMethod());
	}
}
