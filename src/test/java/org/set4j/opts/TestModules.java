package org.set4j.opts;

import junit.framework.Assert;

import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.Set4Module;
import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public class TestModules
{
	public interface Main
	{
		@Set4Module Sub1 getSub1();
		@Set4Module Sub2 getSub2();
	}
	public interface Sub1
	{
		@Set4Value(value = "Value from module 1")
		String getSubVal1();
	}
	public interface Sub2
	{
		@Set4Value(value = "21")
		int getSub2Val();
	}
	
	@Test
	public void testInitProxy()
	{
		Main main = Initializer.init(Main.class);
		Assert.assertEquals("Value from module 1", main.getSub1().getSubVal1());
		Assert.assertEquals(21, main.getSub2().getSub2Val());
	}
}
