package org.set4j.propfile.inheritance;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.propfile.TestPropFile;

/**
 * @author Tomas Mikenda
 *
 */
public class TestPropFileInh extends TestPropFile
{
    /*
	@Before
	public void before()
	{
		configClass = MainChild.class;
	}
    */
	
	@Test
	public void test()
	{
		Properties p = new Properties();
		p.put("customer", "comp1");
		p.put("env", "local");
		
		MainChild s = Initializer.init(MainChild.class, p);
	
		
		Assert.assertEquals(s.getPrintingServer(), "svr3");
		Assert.assertEquals(s.getA().viewName(), "roger");
		Assert.assertEquals(s.getA().isEnabled(), true);
		
	}

}
