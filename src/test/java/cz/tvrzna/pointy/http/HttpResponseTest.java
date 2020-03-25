package cz.tvrzna.pointy.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import cz.tvrzna.pointy.http.HttpMimeTypes;
import cz.tvrzna.pointy.http.HttpResponse;
import cz.tvrzna.pointy.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class HttpResponseTest
{
	@Test
	public void testStatus()
	{
		HttpResponse response = new HttpResponse(Mockito.mock(OutputStream.class));
		response.setStatus(HttpStatus.OK_200);
		assertEquals(HttpStatus.OK_200, response.getStatus());
	}

	@Test
	public void testContentType()
	{
		HttpResponse response = new HttpResponse(Mockito.mock(OutputStream.class));
		response.setContentType(HttpMimeTypes.getMimeType("test.html"));
		assertEquals(HttpMimeTypes.getMimeType("test.html"), response.getContentType());
	}

	@Test
	public void testIsClosed() throws IOException
	{
		HttpResponse response = new HttpResponse(Mockito.mock(OutputStream.class));
		assertEquals(false, response.isClosed());
		response.send("");
		assertEquals(true, response.isClosed());
	}

	@Test
	public void testSendString() throws IOException
	{
		final String message = "{'message': 'Hello'}";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		HttpResponse response = new HttpResponse(baos);
		response.getHttpHeaders().put("TEST", null);
		response.send(message);

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
	public void testSendNullString() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		HttpResponse response = new HttpResponse(baos);
		response.send((String) null);

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
	}

	@Test
	public void testSendInputStream() throws IOException
	{
		final String message = "{'message': 'Hello'}";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(message.getBytes());

		HttpResponse response = new HttpResponse(baos);
		response.send(bais);

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
	public void testSendNullInputStream() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(baos);
		response.send((InputStream) null);

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
	}

	@Test
	public void testGzipSendString() throws IOException
	{
		final String message = "{'message': 'Hello'}";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		HttpResponse response = new HttpResponse(baos);
		response.setAllowedGzip(true);
		assertEquals(true, response.isAllowedGzip());
		response.send(message);

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
		assertNotEquals(message, body.toString());
	}

	@Test
	public void testRedirectOnce() throws IOException
	{
		final String url = "/testUrl";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(baos);
		response.redirect(url);

		assertTrue(baos.toString().contains(url));
		assertTrue(baos.toString().contains(Integer.toString(HttpStatus.FOUND_302)));
	}

	@Test
	public void testRedirectOnce2() throws IOException
	{
		final String url = "/testUrl";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(baos);
		response.redirect(url, false);

		assertTrue(baos.toString().contains(url));
		assertTrue(baos.toString().contains(Integer.toString(HttpStatus.FOUND_302)));
	}

	@Test
	public void testRedirect() throws IOException
	{
		final String url = "/testUrl";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HttpResponse response = new HttpResponse(baos);
		response.redirect(url, true);

		assertTrue(baos.toString().contains(url));
		assertTrue(baos.toString().contains(Integer.toString(HttpStatus.MOVED_PERMANENTLY_301)));
	}
}
