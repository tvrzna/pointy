package cz.tvrzna.pointy.exceptions;

/**
 * The Class BadRequestException.
 *
 * @author michalt
 */
public class BadRequestException extends RuntimeException
{
	private static final long serialVersionUID = 200146466614564185L;

	/**
	 * Instantiates a new bad request exception.
	 */
	public BadRequestException()
	{
		super();
	}

	/**
	 * Instantiates a new bad request exception.
	 *
	 * @param message
	 *          the message
	 */
	public BadRequestException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new bad request exception.
	 *
	 * @param message
	 *          the message
	 * @param throwable
	 *          the throwable
	 */
	public BadRequestException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
