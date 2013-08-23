package org.set4j.objects.mix;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Tomas Mikenda
 *
 */
public class TestObjIfMix
{
	@Test
	public void test()
	{
		Conf s = Conf.getInstance();
		assertFalse(s.getMod().isEnabled());
		assertEquals("hodnota", s.mod.getValue());
	}
	
	//@Test
	@Ignore
	public void testAsTest()
	{
		// fail("todo"); /// co jsem tim chtel rict? 
	}
	
}
