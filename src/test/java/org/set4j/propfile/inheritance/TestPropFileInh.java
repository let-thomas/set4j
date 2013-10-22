package org.set4j.propfile.inheritance;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.propfile.TestPropFile;

/**
 * @author Tomas Mikenda
 *
 */
public class TestPropFileInh //extends TestPropFile
{
    /*
	@Before
	public void before()
	{
		configClass = MainChild.class;
	}
    */
    @After
    public void uninit()
    {
        Initializer.uninitialize(MainChildWithPF.class);
        //System.clearProperty("set4j.loglevel");
    }

    @Test
	public void test()
	{
		Properties p = new Properties();
		p.put("customer", "comp1");
		p.put("env", "locale");
        //p.put("set4j.loglevel","trace");

        MainChildWithPF s = Initializer.init(MainChildWithPF.class, p);

		Assert.assertEquals("svr2", s.getPrintingServer() );
		Assert.assertEquals("comp1pf", s.getA().viewName() );
		Assert.assertEquals(true, s.getA().isEnabled());
	}

    @Test
    public void testWithPropFile()
    {
        Properties p = new Properties();
        p.put("customer", "comp1");
        p.put("env", "local");
        //p.put("set4j.loglevel","trace");
        MainChildWithPF s = Initializer.init(MainChildWithPF.class, p);

        Assert.assertEquals(s.getPrintingServer(), "profichi3");
        Assert.assertEquals(s.getA().viewName(), "pfc3");
        Assert.assertEquals(s.getA().isEnabled(), false);
    }

}
