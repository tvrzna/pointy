package cz.tvrzna.pointy.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * This class handles HTTP request and response is one object, that is used in
 * Routes. <code>HttpContext</code> in response mode works as builder, until
 * <code>send</code> or <code>redirect</code> method is used.
 *
 * @since 0.1.0
 * @author michalt
 */
public class HttpContext
{
	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE_APPLICATION_HTML = "text/html";
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	private final HttpRequest request;
	private final HttpResponse response;

	/**
	 * Instantiates a new HttpContext and its {@link HttpRequest} and
	 * {@link HttpResponse}. It also tries to recognize, if response could be
	 * compressed with <code>GZIP</code>.
	 *
	 * @param socket
	 *          the socket
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public HttpContext(Socket socket) throws IOException
	{
		request = new HttpRequest(socket);
		response = new HttpResponse(socket.getOutputStream());

		String acceptEncoding = getHeader(HEADER_ACCEPT_ENCODING);
		response.setAllowedGzip(acceptEncoding != null && acceptEncoding.contains("gzip"));
	}

	/**
	 * Gets the header by <code>headerName</code>.
	 *
	 * @param headerName
	 *          the header name
	 * @return the header
	 */
	public String getHeader(String headerName)
	{
		return request.getHttpHeaders().get(headerName);
	}

	/**
	 * Sets the header.
	 *
	 * @param headerName
	 *          the header name
	 * @param headerValue
	 *          the header value
	 */
	public void setHeader(String headerName, String headerValue)
	{
		response.getHttpHeaders().put(headerName, headerValue);
	}

	/**
	 * Gets the request uri.
	 *
	 * @return the request uri
	 */
	public String getRequestUri()
	{
		return request.getUri();
	}

	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public HttpRequest getRequest()
	{
		return request;
	}

	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public HttpResponse getResponse()
	{
		return response;
	}

	/**
	 * Sets <code>HttpStatus</code> of response and returns itself.
	 *
	 * @param httpStatus
	 *          the http status
	 * @return the http context
	 */
	public HttpContext status(int httpStatus)
	{
		response.setStatus(httpStatus);
		return this;
	}

	/**
	 * Sets <code>ContentType</code> of response and returns itself.
	 *
	 * @param contentType
	 *          the content type
	 * @return the http context
	 */
	public HttpContext contentType(String contentType)
	{
		response.setContentType(contentType);
		return this;
	}

	/**
	 * Sets content type to <code>application/json</code>.<br>
	 * In future it could define how should potential object be serialized.
	 *
	 * @return the http context
	 */
	public HttpContext json()
	{
		response.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		return this;
	}

	/**
	 * Sets content type to <code>text/html</code>.
	 *
	 * @return the http context
	 */
	public HttpContext html()
	{
		response.setContentType(CONTENT_TYPE_APPLICATION_HTML);
		return this;
	}

	/**
	 * Sends the <code>String</code> content.
	 *
	 * @param content
	 *          the content
	 */
	public void send(String content)
	{
		try
		{
			response.send(content);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sends the <code>InputStream</code> content.
	 *
	 * @param is
	 *          the is
	 */
	public void send(InputStream is)
	{
		try
		{
			response.send(is);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Redirects (HTTP code 302) to defined <code>url</code>.
	 *
	 * @param url
	 *          the url
	 */
	public void redirect(String url)
	{
		try
		{
			response.redirect(url);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Redirects to defined <code>url</code>.
	 *
	 * @param url
	 *          the url
	 * @param permanent
	 *          the permanent
	 */
	public void redirect(String url, boolean permanent)
	{
		try
		{
			response.redirect(url, permanent);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
