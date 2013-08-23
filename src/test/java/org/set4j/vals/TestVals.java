package org.set4j.vals;

import static junit.framework.Assert.*;

import java.util.Properties;

import org.junit.Test;
import org.set4j.Initializer;

/**
 * @author Tomas Mikenda
 *
 */
public class TestVals
{
	@Test
	public void testEval()
	{
		Setting s = Initializer.init(Setting.class);
		
		assertEquals(123, s.server().port());
		assertEquals("localhost", s.server().server());
		assertEquals("http://localhost:123", s.url());
	}
	
	@Test
	public void testRun2()
	{
		Properties p = new Properties();
		p.put("run", "run2");
		Setting s = Initializer.init(Setting.class, p);
		
		assertEquals("acme.com", s.server().server());
		assertEquals(112299, s.server().port());
		assertEquals("http://acme.com:112299", s.url());
	}
}
