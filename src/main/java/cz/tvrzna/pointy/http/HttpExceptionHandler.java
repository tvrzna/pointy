package cz.tvrzna.pointy.http;

/**
 * This class defines, how <code>exceptions</code> thrown from Routes should be
 * handled.
 *
 * @since 0.1.0
 * @author michalt
 */
public class HttpExceptionHandler
{

	/**
	 * Handles the exception.
	 *
	 * @param exception
	 *          the exception
	 * @param context
	 *          the context
	 */
	public void handle(Exception exception, HttpContext context)
	{
		context.status(HttpStatus.INTERNAL_SERVER_ERROR_500).send(exception.getMessage());
	}
}
