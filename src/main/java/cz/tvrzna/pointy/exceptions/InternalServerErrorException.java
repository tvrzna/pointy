package cz.tvrzna.pointy.exceptions;

/**
 * The Class InternalServerErrorException.
 *
 * @author michalt
 */
public class InternalServerErrorException extends RuntimeException
{

	private static final long serialVersionUID = -6410156648742232148L;

	/**
	 * Instantiates a new internal server error exception.
	 */
	public InternalServerErrorException()
	{
		super();
	}

	/**
	 * Instantiates a new internal server error exception.
	 *
	 * @param message
	 *          the message
	 * @param cause
	 *          the cause
	 */
	public InternalServerErrorException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new internal server error exception.
	 *
	 * @param message
	 *          the message
	 */
	public InternalServerErrorException(String message)
	{
		super(message);
	}

}
