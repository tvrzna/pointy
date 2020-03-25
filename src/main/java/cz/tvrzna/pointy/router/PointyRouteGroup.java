package cz.tvrzna.pointy.router;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines <code>List</code>s of {@link PointyRoute}s, @{link
 * {@link PointyRouteGroup}s and {@link PointyStaticRoute}s. Because it extends
 * {@link PointyRoute}, it could also have its own {@link PointyRouteDefinition}s.
 *
 * @since 0.1.0
 * @author michalt
 */
public class PointyRouteGroup extends PointyRoute
{
	private final List<PointyRoute> routes = new ArrayList<>();
	private final List<PointyRouteGroup> routeGroups = new ArrayList<>();
	private final List<PointyStaticRoute> staticRoutes = new ArrayList<>();

	/**
	 * Instantiates a new pointy route group.
	 *
	 * @param uri
	 *          the uri
	 */
	public PointyRouteGroup(String uri)
	{
		super(uri);
	}

	/**
	 * Adds the route.
	 *
	 * @param route
	 *          the route
	 */
	public void addRoute(PointyRoute route)
	{
		routes.add(route);
	}

	/**
	 * Adds the route group.
	 *
	 * @param routeGroup
	 *          the route group
	 */
	public void addRouteGroup(PointyRouteGroup routeGroup)
	{
		routeGroups.add(routeGroup);
	}

	/**
	 * Adds the static route.
	 *
	 * @param staticRoute
	 *          the static route
	 */
	public void addStaticRoute(PointyStaticRoute staticRoute)
	{
		staticRoutes.add(staticRoute);
	}

	/**
	 * Gets the routes.
	 *
	 * @return the routes
	 */
	public List<PointyRoute> getRoutes()
	{
		return routes;
	}

	/**
	 * Gets the route groups.
	 *
	 * @return the route groups
	 */
	public List<PointyRouteGroup> getRouteGroups()
	{
		return routeGroups;
	}

	/**
	 * Gets the static routes.
	 *
	 * @return the static routes
	 */
	public List<PointyStaticRoute> getStaticRoutes()
	{
		return staticRoutes;
	}
}
