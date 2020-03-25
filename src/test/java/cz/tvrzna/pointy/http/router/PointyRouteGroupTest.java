package cz.tvrzna.pointy.http.router;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cz.tvrzna.pointy.router.PointyRoute;
import cz.tvrzna.pointy.router.PointyRouteGroup;

public class PointyRouteGroupTest
{
	private static final String URI = "/testUri";
	private static final String SUB_URI = "/sub";

	@Test
	public void testGetUri()
	{
		PointyRouteGroup group = new PointyRouteGroup(URI);
		assertEquals(URI, group.getUri());
	}

	@Test
	public void testAddRoute()
	{
		PointyRouteGroup group = new PointyRouteGroup(URI);
		PointyRoute route = new PointyRoute(SUB_URI);
		assertEquals(0, group.getRoutes().size());
		group.addRoute(route);
		assertEquals(1, group.getRoutes().size());
	}

	@Test
	public void testAddRouteGroup()
	{
		PointyRouteGroup group = new PointyRouteGroup(URI);
		PointyRouteGroup subGroup = new PointyRouteGroup(SUB_URI);
		assertEquals(0, group.getRouteGroups().size());
		group.addRouteGroup(subGroup);
		assertEquals(1, group.getRouteGroups().size());
	}
}
