package cz.tvrzna.pointy.http;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class HttpParam.
 *
 * @author michalt
 */
public class HttpParam
{
	private final String key;
	private final List<String> value = new ArrayList<>();

	/**
	 * Instantiates a new http param.
	 *
	 * @param key
	 *          the key
	 */
	public HttpParam(String key)
	{
		this.key = key;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public List<String> getValue()
	{
		return value;
	}

}
