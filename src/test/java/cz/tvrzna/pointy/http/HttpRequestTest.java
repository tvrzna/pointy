package cz.tvrzna.pointy.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HttpRequestTest
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
	public void testGetMethod() throws IOException
	{
		HttpRequest request = new HttpRequest(socket);
		assertEquals(HTTP_METHOD, request.getMethod());
	}

	@Test
	public void testGetUri() throws IOException
	{
		HttpRequest request = new HttpRequest(socket);
		assertEquals(HTTP_URI, request.getUri());
	}

	@Test
	public void testGetBody() throws IOException
	{
		HttpRequest request = new HttpRequest(socket);
		assertEquals(HTTP_BODY, request.getBody());
	}

	@Test
	public void testGetHttpHeader() throws IOException
	{
		HttpRequest request = new HttpRequest(socket);
		assertEquals(HTTP_HEADER_VALUE, request.getHttpHeaders().get(HTTP_HEADER_NAME));
	}

	@Test
	public void testAnotherConstructors() throws IOException
	{
		final String httpRequest = "POST /rest/downloads/request?singleParam HTTP/1.1\n" + "Host: localhost:1400\n" + "Content-Type: application/json\n" +
				"Accept: application/json\n" + "client: q4Z9AoVuPXm8oUtGrJ\n" + "user: __#int_dx_user#__\n" + "token: t8JJHzUSDU93Tk_LKVl508OYJ45djfsmAh-iT9x10hw=\n" +
				"Cache-Control: no-cache\n" + "\n" + "{ \"downloadUrl\" : \"https://localhost:1400/sample-download-file\" }";

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpRequest request = new HttpRequest(socket);
		assertEquals(HTTP_CLIENT_IP, request.getClientIp());
	}

	@Test
	public void testAnotherConstructors2() throws IOException
	{
		final String httpRequest = "POST /rest/downloads/request HTTP/1.1\n" + "Host: localhost:1400\n" + "Content-Type: application/json\n" + "Accept: application/json\n" +
				"client: q4Z9AoVuPXm8oUtGrJ\n" + "user: __#int_dx_user#__\n" + "token: t8JJHzUSDU93Tk_LKVl508OYJ45djfsmAh-iT9x10hw=\n" + "Cache-Control: no-cache\n" + "\n" +
				"{ \"downloadUrl\" : \"https://localhost:1400/sample-download-file\" }";

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpRequest request = new HttpRequest(socket);
		request.toString();
	}

	@Test
	public void testPostFormData() throws IOException
	{
		final String httpRequest = "POST / HTTP/1.1\n" + "Host: localhost:1400\n" + "Origin: http://localhost:8080\n" + "Content-Type: application/x-www-form-urlencoded\n" + "\n" +
				"area=something+useful&data=something+less+useful";

		ByteArrayInputStream bais = new ByteArrayInputStream(httpRequest.getBytes());
		Mockito.when(socket.getInputStream()).thenReturn(bais);
		Mockito.when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
		Mockito.when(socketAddress.toString()).thenReturn("127.0.0.1:1555/");

		HttpRequest request = new HttpRequest(socket);
		assertEquals("something+useful", request.getPostParameter("area").getValue().get(0));
		assertEquals("something+less+useful", request.getPostParameter("data").getValue().get(0));
		assertNull(request.getParameter("area"));
		assertNull(request.getParameter("data"));
	}
}
