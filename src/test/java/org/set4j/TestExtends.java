package org.set4j;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Tomas Mikenda
 *
 */
public class TestExtends
{
	@Test
	public void testBase()
	{
		MainSetBase s = Initializer.init(MainSetBase.class);
		assertEquals("base value", s.module().val());
	}
	
	@Test
	public void testExt()
	{
		MainSetExt s = Initializer.init(MainSetExt.class);
		assertEquals("ext value", s.module().val());
	}
	
}
