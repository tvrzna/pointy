package cz.tvrzna.pointy.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.tvrzna.pointy.CaseInsensitiveHashMap;

/**
 * The class represents incoming HTTP request. In constructor it reads
 * <code>InputStream</code> from {@link Socket}. Its parses content into headers
 * and body.
 *
 * @since 0.1.0
 * @author michalt
 */
public class HttpRequest
{
	private String method;
	private String uri;
	private Map<String, String> httpHeaders = new CaseInsensitiveHashMap();
	private List<HttpParam> params = new ArrayList<>();
	private String body;
	private String clientIp;

	/**
	 * Instantiates a new <code>HttpRequest</code>. It reads <code>InputStream</code>
	 * from {@link Socket}, parses it into Http Headers, Url Params and body. It
	 * also reads client's IP address.
	 *
	 * @param socket
	 *          the socket
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public HttpRequest(Socket socket) throws IOException
	{
		@SuppressWarnings("resource")
		InputStream is = socket.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		do
		{
			baos.write(is.read());
		} while (is.available() > 0);

		String request = new String(baos.toByteArray());
		baos.close();

		boolean readBody = false;
		String[] httpLines = request.replace("\r", "").split("\n");
		StringWriter swBody = new StringWriter();
		for (int i = 0; i < httpLines.length; i++)
		{
			String line = httpLines[i];
			if (!readBody)
			{
				if (line.indexOf(":") > 0)
				{
					String key = line.substring(0, line.indexOf(":")).trim();
					String value = line.substring(line.indexOf(":") + 1, line.length()).trim();
					httpHeaders.put(key, value);
				}
				else if (i == 0)
				{
					String[] firstLine = line.trim().split(" ");
					method = firstLine[0].trim();

					String[] strRequestUri = firstLine[1].trim().split("\\?");
					uri = strRequestUri[0].trim();
					if (strRequestUri.length > 1)
					{
						String[] params = strRequestUri[1].split("\\&");
						for (String param : params)
						{
							String[] arrParam = new String[2];

							if (param.indexOf("=") > 0)
							{
								arrParam[0] = param.substring(0, param.indexOf("="));
								arrParam[1] = param.substring(param.indexOf("=") + 1, param.length());
							}
							else
							{
								arrParam[0] = param;
							}
							HttpParam httpParam = getParameter(arrParam[0]);
							if (httpParam == null)
							{
								httpParam = new HttpParam(arrParam[0]);
								this.params.add(httpParam);
							}
							if (arrParam[1] != null)
							{
								httpParam.getValue().add(arrParam[1]);
							}
						}
					}
				}
				if (line.trim().isEmpty())
				{
					readBody = true;
				}
			}
			else
			{
				swBody.append(line);
			}
		}
		body = swBody.toString().trim();

		clientIp = socket.getRemoteSocketAddress().toString().replace("/", "");
		clientIp = clientIp.substring(0, clientIp.lastIndexOf(":"));
	}

	/**
	 * Gets the HTTP method.
	 *
	 * @return the method
	 */
	public String getMethod()
	{
		return method;
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
	 * Gets the http headers.
	 *
	 * @return the http headers
	 */
	public Map<String, String> getHttpHeaders()
	{
		return httpHeaders;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * Gets the client ip.
	 *
	 * @return the client ip
	 */
	public String getClientIp()
	{
		return clientIp;
	}

	/**
	 * Gets the parameter.
	 *
	 * @param param
	 *          the param
	 * @return the parameter
	 */
	public HttpParam getParameter(String param)
	{
		return params.stream().filter(httpParam -> httpParam.getKey().equals(param)).findFirst().orElse(null);
	}
}
