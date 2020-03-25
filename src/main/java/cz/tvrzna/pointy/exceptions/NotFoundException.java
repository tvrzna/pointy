package cz.tvrzna.pointy.exceptions;

/**
 * The Class NotFoundException.
 *
 * @author michalt
 */
public class NotFoundException extends RuntimeException
{
	private static final long serialVersionUID = -8906468767880218546L;

	/**
	 * Instantiates a new not found exception.
	 */
	public NotFoundException()
	{
		super();
	}

	/**
	 * Instantiates a new not found exception.
	 *
	 * @param message
	 *          the message
	 * @param cause
	 *          the cause
	 */
	public NotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new not found exception.
	 *
	 * @param message
	 *          the message
	 */
	public NotFoundException(String message)
	{
		super(message);
	}

}
