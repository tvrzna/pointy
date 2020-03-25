package cz.tvrzna.pointy.router;

import java.io.InputStream;

import cz.tvrzna.pointy.http.HttpMimeTypes;
import cz.tvrzna.pointy.http.HttpStatus;

/**
 * This class serves static content of web server.
 *
 * @since 0.1.0
 * @author michalt
 */
public class PointyStaticRoute extends PointyRouteDefinition
{

	/**
	 * Instantiates a new pointy static route.
	 *
	 * @param uri
	 *          the uri
	 * @param classpathDirectory
	 *          the classpath directory
	 */
	public PointyStaticRoute(String uri, String classpathDirectory)
	{
		super(uri, "ANY", context -> {
			String filePath = context.getRequestUri();
			if (filePath.equals("/"))
			{
				filePath = "/index.html";
			}
			filePath = classpathDirectory.concat(filePath).replace("//", "/");
			try
			{
				InputStream is = PointyStaticRoute.class.getClassLoader().getResourceAsStream(filePath);
				if (is != null)
				{
					context.setHeader("Cache-Control", "public;max-age=3600");
					context.status(HttpStatus.OK_200).contentType(HttpMimeTypes.getMimeType(filePath)).send(is);
					is.close();
				}
				else
				{
					context.status(HttpStatus.NOT_FOUND_404).html().send("<html><head><title>404 Not Found</title></head><body><h1>404 Not Found</h1></body></html>");
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}
}
