package org.set4j.sysprop;

import org.junit.Assert;
import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public class TestSimple
{
	public class Setting
	{
		@Set4Value(value = "acme")
		String customer;
	}
	
	@Test
	public void testDefault()
	{
		Setting s = Initializer.init(new Setting());
		Assert.assertEquals("acme", s.customer);
	}
	
	@Test
	public void testPropOverride()
	{
		final String myName = "My company";
		System.setProperty("customer", myName);
		Setting s = Initializer.init(new Setting());
		Assert.assertEquals(myName, s.customer);
	}

}
