package org.set4j.sysprop;

import junit.framework.Assert;

import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.Set4SysProp;
import org.set4j.Set4SysPropPushBack;
import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public class TestPropPushBack
{
	public interface Set
	{
		@Set4Value(value="false")
		@Set4SysProp(name="log4j.init")
		@Set4SysPropPushBack
		boolean log4jInit();
	}
	
	@Test
	public void test()
	{
		System.clearProperty("log4j.init"); // just to be sure if defined
		Set s = Initializer.init(Set.class);
		
		Assert.assertEquals("false", String.valueOf(s.log4jInit()));
		Assert.assertEquals("false", System.getProperty("log4j.init"));
		System.clearProperty("log4j.init"); // just to be sure if defined
	}
}
