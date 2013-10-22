package org.set4j.objects;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.set4j.Initializer;

/**
 * @author Tomas Mikenda
 *
 */
public class TestModuleTreeFields
{
	@Test
	public void testCreated()
	{
		WithFields_Main main = new WithFields_Main();
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
		WithFields_Main main = new WithFields_Main();
		main.sub1 = new WithFields_Sub1();
		
		Initializer.init(main);
		Assert.assertEquals("sub1", main.sub1.val);
		Assert.assertEquals(21, main.sub2.val);
		
	}

	@Test
	public void testSubMissing()
	{
		WithFields_Main main = new WithFields_Main();
		
		Initializer.init(main);
		Assert.assertEquals("sub1", main.sub1.val);
		Assert.assertEquals(21, main.sub2.val);
		
	}

	@Test
	public void testMakeObjects()
	{
		
		WithFields_Main main = Initializer.init(WithFields_Main.class);
		Assert.assertEquals("sub1", main.sub1.val);
		Assert.assertEquals(21, main.sub2.val);
	}

    @After
    public void uninit()
    {
        Initializer.uninitialize(WithFields_Main.class);
    }

}
