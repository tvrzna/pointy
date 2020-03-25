package cz.tvrzna.pointy;

import java.util.HashMap;

/**
 * The Class defines <code>HashMap&lt;String, String&gt;</code>, where handling
 * the <code>key</code> is case insensitive.
 *
 * @since 0.1.0
 * @author michalt
 */
public class CaseInsensitiveHashMap extends HashMap<String, String>
{
	private static final long serialVersionUID = -7989053167461710859L;

	/**
	 * Put.
	 *
	 * @param key
	 *          the key
	 * @param value
	 *          the value
	 * @return the string
	 */
	@Override
	public String put(String key, String value)
	{
		return super.put(key.toLowerCase(), value);
	}

	/**
	 * Gets the.
	 *
	 * @param key
	 *          the key
	 * @return the string
	 */
	@Override
	public String get(Object key)
	{
		return super.get(((String) key).toLowerCase());
	}

}
