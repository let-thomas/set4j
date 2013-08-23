package org.set4j.sysprop;

import org.junit.Assert;
import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.Set4Module;
import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public class TestModule
{
	public class Setting
	{
		@Set4Module
		Sub1 module = new Sub1();
	}
	public class Sub1
	{
		@Set4Value(value="banana")
		String value;
	}
	
	@Test
	public void test()
	{
		final String val = "new value";
		System.setProperty("module.value", val);
		Setting s = Initializer.init(new Setting());
		Assert.assertEquals(val, s.module.value);
		System.clearProperty("module.value");
	}
}
