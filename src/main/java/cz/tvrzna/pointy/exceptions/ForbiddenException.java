package cz.tvrzna.pointy.exceptions;

/**
 * The Class ForbiddenException.
 *
 * @author michalt
 */
public class ForbiddenException extends RuntimeException
{
	private static final long serialVersionUID = 6888056145115379677L;

	/**
	 * Instantiates a new forbidden exception.
	 */
	public ForbiddenException()
	{
		super();
	}

	/**
	 * Instantiates a new forbidden exception.
	 *
	 * @param message
	 *          the message
	 * @param cause
	 *          the cause
	 */
	public ForbiddenException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new forbidden exception.
	 *
	 * @param message
	 *          the message
	 */
	public ForbiddenException(String message)
	{
		super(message);
	}

}
