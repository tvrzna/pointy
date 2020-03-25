package cz.tvrzna.pointy.router;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines <code>List&lt;PointyRouteDefinition&gt;</code>, that is
 * server to <code>PointyEndpoint</code> for handling <code>HttpRequest</code>.
 *
 * @since 0.1.0
 * @author michalt
 */
public class PointyRoute
{
	private final String uri;
	private final List<PointyRouteDefinition> routeDefs = new ArrayList<>();

	/**
	 * Instantiates a new <code>PointyRoute</code> handling defined
	 * <code>uri</code>.
	 *
	 * @param uri
	 *          the uri
	 */
	public PointyRoute(String uri)
	{
		this.uri = uri;
	}

	/**
	 * Gets the <code>List</code> of {@link PointyRouteDefinition}
	 *
	 * @return the route defs
	 */
	public List<PointyRouteDefinition> getRouteDefs()
	{
		return routeDefs;
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
	 * Creates new {@link PointyRouteDefinition} on defined <code>path</code> with
	 * defined {@link PointyRouteHandler} for GET method.
	 *
	 * @param path
	 *          the path
	 * @param handler
	 *          the handler
	 */
	public void GET(String path, PointyRouteHandler handler)
	{
		routeDefs.add(new PointyRouteDefinition(path, "GET", handler));
	}

	/**
	 * Creates new {@link PointyRouteDefinition} on defined <code>path</code> with
	 * defined {@link PointyRouteHandler} for POST method.
	 *
	 * @param path
	 *          the path
	 * @param handler
	 *          the handler
	 */
	public void POST(String path, PointyRouteHandler handler)
	{
		routeDefs.add(new PointyRouteDefinition(path, "POST", handler));
	}

	/**
	 * Creates new {@link PointyRouteDefinition} on defined <code>path</code> with
	 * defined {@link PointyRouteHandler} for PUT method.
	 *
	 * @param path
	 *          the path
	 * @param handler
	 *          the handler
	 */
	public void PUT(String path, PointyRouteHandler handler)
	{
		routeDefs.add(new PointyRouteDefinition(path, "PUT", handler));
	}

	/**
	 * Creates new {@link PointyRouteDefinition} on defined <code>path</code> with
	 * defined {@link PointyRouteHandler} for any method.
	 *
	 * @param path
	 *          the path
	 * @param handler
	 *          the handler
	 */
	public void ANY(String path, PointyRouteHandler handler)
	{
		routeDefs.add(new PointyRouteDefinition(path, "ANY", handler));
	}
}
