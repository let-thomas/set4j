package org.set4j;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tomas Mikenda
 *
 */
public class TestBasics
{
	interface Basics
	{
		@Set4Value(value="direct")
		String getDirectValue();
		
		@Set4Value(value="${sp:user.name}")
		String getSysPropValue();

		@Set4Value(value="${directValue}")
		String getDirectValueCopy();
	}
	
	@Test
	public void test()
	{
		Basics s = Initializer.init(Basics.class);
		Assert.assertEquals("direct", s.getDirectValue());
		Assert.assertEquals(System.getProperty("user.name"), s.getSysPropValue());
		Assert.assertEquals("direct", s.getDirectValueCopy());
	}
}
