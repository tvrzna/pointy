package cz.tvrzna.pointy.router;

import cz.tvrzna.pointy.http.HttpContext;

/**
 * This interfaces defines handler on HttpRequest.
 *
 * @since 0.1.0
 * @author michalt
 */
@FunctionalInterface
public interface PointyRouteHandler
{

	/**
	 * Handles active {@link HttpContext}.
	 *
	 * @param context
	 *          the context
	 */
	public void handle(HttpContext context);
}
