package cz.tvrzna.pointy.http;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class HttpMimeTypes.
 *
 * @since 0.1.0
 * @author michalt
 */
public class HttpMimeTypes
{
	private static final Map<String, String> MIME_TYPES = new HashMap<>();

	static
	{
		MIME_TYPES.put(".html", "text/html");
		MIME_TYPES.put(".css", "text/css");
		MIME_TYPES.put(".js", "application/javascript");

		MIME_TYPES.put(".eot", "application/vnd.ms-fontobject");
		MIME_TYPES.put(".otf", "application/x-font-opentype");
		MIME_TYPES.put(".svg", "image/svg+xml");
		MIME_TYPES.put(".ttf", "application/x-font-ttf");
		MIME_TYPES.put(".woff", "application/font-woff");
		MIME_TYPES.put(".woff2", "application/font-woff2");
	}

	/**
	 * Instantiates a new http mime types.
	 */
	private HttpMimeTypes()
	{
	}

	/**
	 * Gets the mime type from <code>fileName</code>.
	 *
	 * @param fileName
	 *          the file name
	 * @return the mime type
	 */
	public static String getMimeType(String fileName)
	{
		if (fileName.lastIndexOf(".") > 0)
		{
			String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase();
			if (suffix != null)
			{
				return MIME_TYPES.get(suffix);
			}
		}
		return "application/octet-stream";
	}
}
