package cz.tvrzna.pointy.exceptions;

/**
 * The Class NotAuthorizedException.
 *
 * @author michalt
 */
public class NotAuthorizedException extends RuntimeException
{
	private static final long serialVersionUID = 7405720245539243844L;

	/**
	 * Instantiates a new not authorized exception.
	 */
	public NotAuthorizedException()
	{
		super();
	}

	/**
	 * Instantiates a new not authorized exception.
	 *
	 * @param message
	 *          the message
	 */
	public NotAuthorizedException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new not authorized exception.
	 *
	 * @param message
	 *          the message
	 * @param throwable
	 *          the throwable
	 */
	public NotAuthorizedException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}