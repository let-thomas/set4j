package org.set4j.cond;

import junit.framework.Assert;

import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.Set4Nullable;
import org.set4j.Set4Values;
import org.set4j.Set4Value;
import org.set4j.Set4Register;
import org.set4j.When;

/**
 * @author Tomas Mikenda
 *
 */
public class SimpleCond
{
	public class Setting
	{
		@Set4Values ({
			@Set4Value(value="default"),
			@Set4Value(value="ACME", when=@When(what="name", eq="cust1")),
			@Set4Value(value="BOOT &co", when=@When(what="name", eq="cust2")),
		})
		String custName;
		
		@Set4Register
		@Set4Nullable
		String name;
	}

	@Test
	public void testNull()
	{
		Setting s = new Setting();
		Initializer.init(s);
		Assert.assertEquals("default", s.custName);
	}
	
	@Test
	public void testDefault()
	{
		Setting s = new Setting();
		s.name = "testenv";
		Initializer.init(s);
		Assert.assertEquals("default", s.custName);
	}
	@Test
	public void testAcme()
	{
		Setting s = new Setting();
		s.name = "cust1";
		Initializer.init(s);
		Assert.assertEquals("ACME", s.custName);
	}
	@Test
	public void testBoots()
	{
		Setting s = new Setting();
		s.name = "cust2";
		Initializer.init(s);
		Assert.assertEquals("BOOT &co", s.custName);
	}
}
