package org.set4j;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tomas Mikenda
 *
 */
public class TestIfaceIns
{
	public static interface TestIf
	{
		@Set4Values({
			@Set4Value(value = "main")
		})
		String getEnv();
		
		@Set4Value( value = "2")
		int getNumber();
	}
	@Test
	public void test1()
	{
		TestIf setting = (TestIf)Initializer.init(TestIf.class);
		Assert.assertNotNull(setting);

		Assert.assertEquals(2, setting.getNumber());
	}
}
