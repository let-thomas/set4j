package org.set4j;


/**
 * @author Tomas Mikenda
 *
 */
public class Set4JChecker
{
	public static void assertNotNull(Object value) throws Set4JException
	{
		if (value == null) throw new Set4JException("Assertion fails");
	}
}
