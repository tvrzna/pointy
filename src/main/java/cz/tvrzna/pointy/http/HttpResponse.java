package cz.tvrzna.pointy.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

import cz.tvrzna.pointy.CaseInsensitiveHashMap;

/**
 * This class represents and handles HTTP response.
 *
 * @since 0.1.0
 * @author michalt
 */
public class HttpResponse
{
	private final OutputStream os;
	private boolean allowedGzip = false;
	private boolean closed = false;

	private int httpStatus = HttpStatus.OK_200;
	private final Map<String, String> httpHeaders = new CaseInsensitiveHashMap();

	/**
	 * Instantiates a new http response.
	 *
	 * @param os
	 *          the os
	 */
	public HttpResponse(OutputStream os)
	{
		this.os = os;
		setContentType("text/plain");
	}

	/**
	 * Sets the status.
	 *
	 * @param httpStatus
	 *          the new status
	 */
	public void setStatus(int httpStatus)
	{
		this.httpStatus = httpStatus;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public int getStatus()
	{
		return httpStatus;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType
	 *          the new content type
	 */
	public void setContentType(String contentType)
	{
		httpHeaders.put("Content-Type", contentType);
	}

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public String getContentType()
	{
		return httpHeaders.get("Content-Type");
	}

	/**
	 * Sends HTTP response by writing into <code>OutputStream</code>. Sending
	 * response closes whole Client connection, because after response there is no
	 * other action, that could be performed. If <code>allowedGzip</code> is set
	 * to true, it compress output with gzip.
	 *
	 * @param bodyHandler
	 *          the body handler
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	private void send(Consumer<OutputStream> bodyHandler) throws IOException
	{
		httpHeaders.put("Connection", "keep-alive");
		if (allowedGzip)
		{
			httpHeaders.put("Content-Encoding", "gzip");
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PrintWriter w = new PrintWriter(baos);

		w.write("HTTP/1.1 ");
		w.write(Integer.toString(httpStatus));
		w.write("\r\n");

		for (Map.Entry<String, String> entry : httpHeaders.entrySet())
		{
			w.write(entry.getKey());
			w.write(": ");
			w.write(entry.getValue() != null ? entry.getValue() : "");
			w.write("\r\n");
		}

		w.write("\r\n");
		w.flush();

		GZIPOutputStream gzip = null;
		if (allowedGzip)
		{
			gzip = new GZIPOutputStream(baos);
			bodyHandler.accept(gzip);
			baos.flush();
			gzip.finish();
		}
		else
		{
			bodyHandler.accept(baos);
		}

		baos.writeTo(os);

		os.close();
		if (gzip != null)
		{
			gzip.close();
		}

		closed = true;
	}

	/**
	 * Sends <code>body</code> as response.
	 *
	 * @param body
	 *          the body
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void send(String body) throws IOException
	{
		send(out -> {
			httpHeaders.put("Content-Length", Integer.toString(body != null ? body.length() : 0));
			try
			{
				out.write(body.getBytes());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	/**
	 * Sends <code>InputStream</code> as response.
	 *
	 * @param is
	 *          the is
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void send(InputStream is) throws IOException
	{
		send(out -> {
			try
			{
				int contentLength = 0;

				while (is.available() > 0)
				{
					out.write(is.read());
					contentLength++;
				}

				httpHeaders.put("Content-Length", Integer.toString(contentLength));

				is.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	/**
	 * Redirects to defined <code>url</code>.
	 *
	 * @param url
	 *          the url
	 * @param permanent
	 *          the permanent
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void redirect(String url, boolean permanent) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PrintWriter w = new PrintWriter(baos);
		w.write("HTTP/1.1 ");
		w.write(Integer.toString(permanent ? HttpStatus.MOVED_PERMANENTLY_301 : HttpStatus.FOUND_302));
		w.write("\r\n");
		w.write("Location: ");
		w.write(url);
		w.flush();

		baos.writeTo(os);
		os.close();
		closed = true;
	}

	/**
	 * Redirects once to defined <code>url</code>.
	 *
	 * @param url
	 *          the url
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void redirect(String url) throws IOException
	{
		redirect(url, false);
	}

	/**
	 * Checks if is allowed gzip.
	 *
	 * @return true, if is allowed gzip
	 */
	public boolean isAllowedGzip()
	{
		return allowedGzip;
	}

	/**
	 * Sets the allowed gzip.
	 *
	 * @param allowedGzip
	 *          the new allowed gzip
	 */
	public void setAllowedGzip(boolean allowedGzip)
	{
		this.allowedGzip = allowedGzip;
	}

	/**
	 * Checks if is closed.
	 *
	 * @return true, if is closed
	 */
	public boolean isClosed()
	{
		return closed;
	}

	/**
	 * Gets the http headers.
	 *
	 * @return the http headers
	 */
	public Map<String, String> getHttpHeaders()
	{
		return httpHeaders;
	}

}
