package cz.tvrzna.pointy.router;

/**
 * This class defines behaviour on defined <code>uri</code> with specific
 * <code>method</code>.
 *
 * @since 0.1.0
 * @author michalt
 */
public class PointyRouteDefinition
{
	private final String uri;
	private final String method;
	private final PointyRouteHandler routeHandler;

	/**
	 * Instantiates a new pointy route definition.
	 *
	 * @param uri
	 *          the uri
	 * @param method
	 *          the method
	 * @param routeHandler
	 *          the route handler
	 */
	public PointyRouteDefinition(String uri, String method, PointyRouteHandler routeHandler)
	{
		this.uri = uri;
		this.method = method;
		this.routeHandler = routeHandler;
	}

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * Gets the route handler.
	 *
	 * @return the route handler
	 */
	public PointyRouteHandler getRouteHandler()
	{
		return routeHandler;
	}
}
