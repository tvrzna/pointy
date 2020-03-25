package cz.tvrzna.pointy.router;

import cz.tvrzna.pointy.http.HttpContext;
import cz.tvrzna.pointy.http.HttpExceptionHandler;
import cz.tvrzna.pointy.http.HttpRequest;

/**
 * This class defines abstract <code>Endpoint</code>, that provides
 * {@link PointyRoute}s, {@link PointyRouteGroup}s and {@link PointyStaticRoute}s to
 * <code>PointyServer</code>.<br>
 * Main function, except for storing available routes, is also to make decision,
 * which route and its <code>PointyRouteDefinition</code> matches to requested
 * <code>URI</code> and <code>Method</code>.<br>
 * <code>URI</code> is checked with <code>Regex</code>, that means, that more
 * Routes can handle variable count of <code>URIs</code>, but when
 * <code>HttpResponse</code> sends its response, no other route will be
 * invoked.<br>
 * <code>PointyEndpoint</code> is extension of {@link PointyRouteGroup}, that
 * extends {@link PointyRoute}, that adds functionality to be stand-alone Route
 * without any other routes. <br><br>
 * <b>NOTE:</b> Please, avoid using another <code>PointyEndpoint</code> as
 * {@link PointyRoute} or {@link PointyRouteGroup}, one <code>PointyEndpoint</code> is
 * enough per one <code>PointyServer</code> and using another
 * <code>PointyEndpoint</code> inside another will not bring any advantage.
 *
 * @since 0.1.0
 * @author michalt
 */
public abstract class PointyEndpoint extends PointyRouteGroup
{

	/**
	 * Instantiates a new pointy endpoint.
	 */
	public PointyEndpoint()
	{
		super("");
	}

	/**
	 * Instantiates a new pointy endpoint.
	 *
	 * @param uri
	 *          the uri
	 */
	public PointyEndpoint(String uri)
	{
		super(uri);
	}

	private HttpExceptionHandler exceptionHandler = new HttpExceptionHandler();

	/**
	 * Method invoked during <code>PointyServer</code>.start().<br>
	 * This method is used for initialization of routes and route groups.
	 */
	public abstract void onInit();

	/**
	 * Handles {@link HttpContext} and according parameters defined in its
	 * {@link HttpRequest} it decides, which {@link PointyRouteDefinition} inside
	 * each <code>Route</code> should be invoked.<br>
	 * If any {@link PointyRouteDefinition} sends response, no other Route will be
	 * invoked. <br>
	 * If no {@link PointyRouteDefinition} sends response, by default it returns 404
	 * Error response. If any {@link PointyRouteDefinition} throws any
	 * <code>Exception</code>, it is catched and handled in
	 * <code>exceptionHandler</code>. The {@link HttpExceptionHandler} could be
	 * overriden and replaced by using
	 * {@link #setExceptionHandler(HttpExceptionHandler)}.
	 *
	 * @param context
	 *          the context
	 */
	public void handle(HttpContext context)
	{
		try
		{
			handleRouteGroup(null, this, context);
			if (context.getResponse().isClosed())
			{
				return;
			}
		}
		catch (Exception e)
		{
			exceptionHandler.handle(e, context);
			return;
		}
		context.status(404).send("404 File Not Found");
	}

	/**
	 * Handles single {@link PointyRouteGroup} and tries to find corresponding
	 * {@link PointyRouteDefinition}. If response was send, no other
	 * {@link PointyRouteDefinition} will be invoked.
	 *
	 * @param parentUri
	 *          the parent uri
	 * @param routeGroup
	 *          the route group
	 * @param context
	 *          the context
	 */
	private void handleRouteGroup(String parentUri, PointyRouteGroup routeGroup, HttpContext context)
	{
		String uri = (parentUri != null ? parentUri : "").concat(routeGroup.getUri());
		handleRoute(parentUri, routeGroup, context);
		if (context.getResponse().isClosed())
		{
			return;
		}
		for (PointyRouteGroup childRouteGroup : routeGroup.getRouteGroups())
		{
			handleRouteGroup(uri, childRouteGroup, context);
			if (context.getResponse().isClosed())
			{
				return;
			}
		}
		for (PointyRoute route : routeGroup.getRoutes())
		{
			handleRoute(uri, route, context);
			if (context.getResponse().isClosed())
			{
				return;
			}
		}
		for (PointyStaticRoute staticRoute : getStaticRoutes())
		{
			handleRouteDef(uri, staticRoute, context);
			if (context.getResponse().isClosed())
			{
				return;
			}
		}
	}

	/**
	 * Handles single {@link PointyRoute} and tries to find corresponding
	 * {@link PointyRouteDefinition}. If response was send, no other
	 * {@link PointyRouteDefinition} will be invoked.
	 *
	 * @param parentUri
	 *          the parent uri
	 * @param route
	 *          the route
	 * @param context
	 *          the context
	 */
	private void handleRoute(String parentUri, PointyRoute route, HttpContext context)
	{
		String uri = (parentUri != null ? parentUri : "").concat(route.getUri());
		for (PointyRouteDefinition def : route.getRouteDefs())
		{
			handleRouteDef(uri, def, context);
			if (context.getResponse().isClosed())
			{
				return;
			}
		}
	}

	/**
	 * Checks, if {@link PointyRouteDefinition} matches requested <code>URI</code>
	 * and <code>Method</code>.
	 *
	 * @param parentUri
	 *          the parent uri
	 * @param def
	 *          the def
	 * @param context
	 *          the context
	 */
	private void handleRouteDef(String parentUri, PointyRouteDefinition def, HttpContext context)
	{
		String uri = (parentUri != null ? parentUri : "").concat(def.getUri()).replace("//", "/");
		if (context.getRequest().getUri().matches(uri) && ("ANY".equals(def.getMethod()) || def.getMethod().equals(context.getRequest().getMethod())))
		{
			def.getRouteHandler().handle(context);
			if (context.getResponse().isClosed())
			{
				return;
			}
		}
	}

	/**
	 * Gets the exception handler.
	 *
	 * @return the exception handler
	 */
	public HttpExceptionHandler getExceptionHandler()
	{
		return exceptionHandler;
	}

	/**
	 * Sets the exception handler.
	 *
	 * @param exceptionHandler
	 *          the new exception handler
	 */
	public void setExceptionHandler(HttpExceptionHandler exceptionHandler)
	{
		this.exceptionHandler = exceptionHandler;
	}
}
