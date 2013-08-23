package org.set4j;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Tomas Mikenda
 *
 */
public class TestSingleton
{
	@Test
	public void test()
	{
		Settings me = Initializer.init(Settings.class);
		Assert.assertEquals("value from base", me.getBaseValue());
	}

	
}
