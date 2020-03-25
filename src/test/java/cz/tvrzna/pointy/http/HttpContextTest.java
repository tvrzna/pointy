package cz.tvrzna.pointy.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import cz.tvrzna.pointy.http.HttpContext;
import cz.tvrzna.pointy.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class HttpContextTest
{
	private static final String HTTP_METHOD = "POST";
	private static final String HTTP_URI = "/rest/downloads/request";
	private static final String HTTP_BODY = "{ \"downloadUrl\" : \"https://localhost:1400/sample-download-file\" }";
	private static final String HTTP_HEADER_NAME = "token";
	private static final String HTTP_HEADER_VALUE = "t8JJHzUSDU93Tk_LKVl508OYJ45djfsmAh-iT9x10hw=";
	private static final String HTTP_CLIENT_IP = "127.0.0.1";

	private static final String HTTP_REQUEST = HTTP_METHOD + " " + HTTP_URI + "?test=abc&test=def HTTP/1.1\n" + "Host: localhost:1400\n" + "Content-Type: application/json\n" +
			"Accept: application/json\n" + "client: q4Z9AoVuPXm8oUtGrJ\n" + "user: __#int_dx_user#__\n" + HTTP_HEADER_NAME + ": " + HTTP_HEADER_VALUE + "\n" +
			"Cache-Control: no-cache\n" + "\n" + HTTP_BODY;

	@Mock
	private Socket socket;

	@Mock
	private SocketAddress socketAddress;

	@BeforeEach
	public void constructSocket() throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(HTTP_REQUEST.getBytes());
		Mockito.lenient().when(socket.getInputStream()).thenReturn(bais);
		Mockito.lenient().when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.lenient().when(socketAddress.toString()).thenReturn(HTTP_CLIENT_IP + ":1555/");
	}

	@Test
	public void testGetHeader() throws IOException
	{
		HttpContext context = new HttpContext(socket);
		assertEquals(HTTP_HEADER_VALUE, context.getHeader(HTTP_HEADER_NAME));
	}

	@Test
	public void testSetHeader() throws IOException
	{
		final String headerKey = "testHeader";
		final String headerValue = "testValue";

		HttpContext context = new HttpContext(socket);
		context.setHeader(headerKey, headerValue);

		assertEquals(headerValue, context.getResponse().getHttpHeaders().get(headerKey));
	}

	@Test
	public void testGetRequestUri() throws IOException
	{
		HttpContext context = new HttpContext(socket);
		assertEquals(HTTP_URI, context.getRequestUri());
	}

	@Test
	public void testGetRequest() throws IOException
	{
		HttpContext context = new HttpContext(socket);
		assertNotNull(context.getRequest());
	}

	@Test
	public void testGetResponse() throws IOException
	{
		HttpContext context = new HttpContext(socket);
		assertNotNull(context.getResponse());
	}

	@Test
	public void testAllowGzip() throws IOException
	{
		String HTTP_REQUEST = HTTP_METHOD + " " + HTTP_URI + "?test=abc&test=def HTTP/1.1\n" + "Host: localhost:1400\n" + "Content-Type: application/json\n" +
				"Accept: application/json\n" + "client: q4Z9AoVuPXm8oUtGrJ\n" + "user: __#int_dx_user#__\n" + HTTP_HEADER_NAME + ": " + HTTP_HEADER_VALUE + "\n" +
				"Cache-Control: no-cache\n" + "Accept-Encoding: gzip\n" + "\n" + HTTP_BODY;

		ByteArrayInputStream bais = new ByteArrayInputStream(HTTP_REQUEST.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);

		HttpContext context = new HttpContext(socket);
		assertTrue(context.getResponse().isAllowedGzip());
	}

	@Test
	public void testContentType() throws IOException
	{
		String HTTP_REQUEST = HTTP_METHOD + " " + HTTP_URI + "?test=abc&test=def HTTP/1.1\n" + "Host: localhost:1400\n" + "Content-Type: application/json\n" +
				"Accept: application/json\n" + "client: q4Z9AoVuPXm8oUtGrJ\n" + "user: __#int_dx_user#__\n" + HTTP_HEADER_NAME + ": " + HTTP_HEADER_VALUE + "\n" +
				"Cache-Control: no-cache\n" + "Accept-Encoding: none\n" + "\n" + HTTP_BODY;

		ByteArrayInputStream bais = new ByteArrayInputStream(HTTP_REQUEST.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);

		final String contentType = "application/xml";

		HttpContext context = new HttpContext(socket).contentType(contentType);
		assertEquals(contentType, context.getResponse().getContentType());
	}

	@Test
	public void testStatus() throws IOException
	{
		final int status = HttpStatus.BAD_REQUEST_400;

		HttpContext context = new HttpContext(socket).status(status);
		assertEquals(status, context.getResponse().getStatus());
	}

	@Test
	public void testJson() throws IOException
	{
		HttpContext context = new HttpContext(socket).json();
		assertEquals("application/json", context.getResponse().getContentType());
	}

	@Test
	public void testHtml() throws IOException
	{
		HttpContext context = new HttpContext(socket).html();
		assertEquals("text/html", context.getResponse().getContentType());
	}

	@Test
	public void testRedirect() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Mockito.when(socket.getOutputStream()).thenReturn(baos);

		HttpContext context = new HttpContext(socket);
		context.redirect("redirect");

		assertTrue(baos.toString().contains("redirect"));
		assertTrue(baos.toString().contains(Integer.toString(HttpStatus.FOUND_302)));
	}

	@Test
	public void testRedirect2() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Mockito.when(socket.getOutputStream()).thenReturn(baos);

		HttpContext context = new HttpContext(socket);
		context.redirect("redirect", true);

		assertTrue(baos.toString().contains("redirect"));
		assertTrue(baos.toString().contains(Integer.toString(HttpStatus.MOVED_PERMANENTLY_301)));
	}

	@Test
	public void testRedirect3() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Mockito.when(socket.getOutputStream()).thenReturn(baos);

		HttpContext context = new HttpContext(socket);
		context.redirect("redirect", false);

		assertTrue(baos.toString().contains("redirect"));
		assertTrue(baos.toString().contains(Integer.toString(HttpStatus.FOUND_302)));
	}

	@Test
	public void testSendString() throws IOException
	{
		final String message = "{'message': 'Hello'}";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		HttpContext context = new HttpContext(socket);
		context.send(message);

		String strResponse = baos.toString();
		String[] lines = strResponse.replace("\r", "").split("\n");

		StringBuilder body = new StringBuilder();

		boolean isBody = false;
		for (String line : lines)
		{
			if (line.isEmpty())
			{
				isBody = true;
				continue;
			}
			if (isBody)
			{
				if (body.length() > 0)
				{
					body.append("\n");
				}
				body.append(line);
			}
		}
		assertEquals(message, body.toString());
	}

	@Test
	public void testSendInputStream() throws IOException
	{
		final String message = "{'message': 'Hello'}";
		ByteArrayInputStream bais = new ByteArrayInputStream(message.getBytes());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Mockito.when(socket.getOutputStream()).thenReturn(baos);
		HttpContext context = new HttpContext(socket);
		context.send(bais);

		String strResponse = baos.toString();
		String[] lines = strResponse.replace("\r", "").split("\n");

		StringBuilder body = new StringBuilder();

		boolean isBody = false;
		for (String line : lines)
		{
			if (line.isEmpty())
			{
				isBody = true;
				continue;
			}
			if (isBody)
			{
				if (body.length() > 0)
				{
					body.append("\n");
				}
				body.append(line);
			}
		}
		assertEquals(message, body.toString());
	}
}
