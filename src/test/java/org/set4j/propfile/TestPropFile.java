package org.set4j.propfile;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.set4j.Initializer;

/**
 * @author Tomas Mikenda
 *
 */
public class TestPropFile
{
	protected Class<MainS> configClass = null;
	
	@Before
	public void before()
	{
		configClass = MainS.class;
        System.setProperty("set4j.loglevel","trace");
	}

    @After
    public void uninit()
    {
        Initializer.uninitialize(MainS.class);
        System.clearProperty("set4j.loglevel");
    }


    @Test
	public void testNoCfg()
	{
		try
        {
            MainS s = Initializer.init(configClass);
            // should not reach, error about missing vals
			Assert.assertEquals(s.getPrintingServer(), "localhost");
		} catch (org.set4j.Set4JException e)
		{
			Assert.assertTrue("Unexpected ex: " + e, e.getMessage().startsWith("No value found for item"));
		}
	}

	@Test
	public void testBasic1Cfg()
	{
		Properties p = new Properties();
		p.put("customer", "comp1");
		p.put("env", "local");
        //p.put("set4j.loglevel","trace");

		MainS s = Initializer.init(configClass, p);

        Assert.assertEquals(s.getPrintingServer(), "serverpf-full");
		Assert.assertEquals(s.getA().viewName(), "roger");
		Assert.assertEquals(s.getA().isEnabled(), true);
	}
	
	@Test
	public void testOverrideAllCfg()
	{
		Properties p = new Properties();
		p.put("env", "mikmik");
		p.put("customer", "hedge2");
		p.put("printingServer", "nie mame");
		p.put("a.viewName", "dbuser");
		p.put("a.enabled", "false");

        MainS s = Initializer.init(configClass, p);

        Assert.assertEquals(s.getPrintingServer(), "nie mame");
	}

	@Test
	public void testOverrideMustCfg()
	{
		Properties p = new Properties();
		p.put("env", "localhost");
		p.put("customer", "firmaAG4");
		p.put("a.viewName", "dbuser");
		p.put("a.enabled", "true");
		p.put("Set4PropFile:location", ".");
		/*
		p.put("printingServer", "nie mame");
		*/

        MainS s = Initializer.init(configClass, p);
		
		Assert.assertEquals("localhost", s.getEnv());
		Assert.assertEquals(Customer.firmaAG4, s.getCustomer());
		Assert.assertEquals(s.getA().viewName(), "dbuser");
		Assert.assertEquals(s.getA().isEnabled(), true);
		//Assert.assertEquals(s.getPrintingServer(), "myown");
		Assert.assertEquals(s.getPrintingServer(), "svr1");//from defaults
		
	}
	
	@Test
	public void testFromSysProps()
    {
	    System.setProperty("customer", "comp1");
	    System.setProperty("env", "local");

		MainS s = Initializer.init(configClass);
		
		Assert.assertEquals(s.getPrintingServer(), "serverpf-full");
		Assert.assertEquals(s.getA().viewName(), "roger");
		Assert.assertEquals(true, s.getA().isEnabled());
	    
		System.clearProperty("customer");
		System.clearProperty("env");
		
    }
	
}
