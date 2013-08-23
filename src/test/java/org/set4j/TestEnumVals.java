package org.set4j;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tomas Mikenda
 *
 */
public class TestEnumVals
{
	public enum Enum {one, two, three};
	public class Setting
	{
		@Set4Values ({
			@Set4Value(value="two" )// cannot use Enum.qwe.toString() - must be constant :-(
			//,@Set4Property(value="")
		})
		Enum enumVal;
	}
	@Test
	public void TestEnumVal()
	{
		Setting s = Initializer.init(new Setting());
		Assert.assertEquals(Enum.two, s.enumVal);
	}
	
}
