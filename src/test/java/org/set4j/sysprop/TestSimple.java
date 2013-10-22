package org.set4j.sysprop;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
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

    @BeforeClass
    static public void grr()
    {
        //System.setProperty("set4j.loglevel", "debug");
        //System.setProperty("set4j.test", "true");
    }
	
	@Test
	public void testDefault()
	{
        System.setProperty("set4j.loglevel", "debug");
        System.clearProperty("customer");  // seems somehow persisted for longer time :-(
		Setting s = Initializer.init(new Setting());
		Assert.assertEquals("acme", s.customer);
	}
	
	@Test
	public void testPropOverride()
	{
        System.setProperty("set4j.loglevel", "debug");
		final String myName = "My company";
        System.out.println("B1");
		System.setProperty("customer", myName);
		Setting s = Initializer.init(new Setting());
        System.out.println("B2");
        Assert.assertEquals(myName, s.customer);
        System.out.println("B3");
	}

    @After
    public void uninit()
    {
        Initializer.uninitialize(Setting.class);
    }
}
