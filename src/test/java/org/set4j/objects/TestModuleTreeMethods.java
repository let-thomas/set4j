package org.set4j.objects;

import junit.framework.Assert;

import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public class TestModuleTreeMethods
{
	
	
	/*
	public class Sub1
	{
		@Set4Property(value="sub1")
		String val = null;
	}
	public class Sub2
	{
		@Set4Property(value="21")
		int val;
	}
	*/
	
	@Test
	public void testCreated()
	{
		Method_Main main = new Method_Main();
		main.sub1 = new WithFields_Sub1();
		main.sub2 = new WithFields_Sub2();
		main.sub3 = this;
		
		Initializer.init(main);
		Assert.assertEquals("sub1", main.sub1.val);
		Assert.assertEquals(21, main.sub2.val);
	}

	@Test
	public void testPartiallyCreated()
	{
		Method_Main main = new Method_Main();
		main.sub1 = new WithFields_Sub1();
		
		Initializer.init(main);
		Assert.assertEquals("sub1", main.sub1.val);
		Assert.assertEquals(21, main.sub2.val);
		
	}

	@Test
	public void testSubMissing()
	{
		Method_Main main = new Method_Main();
		
		Initializer.init(main);
		Assert.assertEquals("sub1", main.sub1.val);
		Assert.assertEquals(21, main.sub2.val);
		
	}

	@Test
	public void testMakeObjects()
	{
		
		Method_Main main = Initializer.init(Method_Main.class);
		Assert.assertEquals("sub1", main.getSub1().val);
		Assert.assertEquals(21, main.getSub2().val);
		
	}
	
}
