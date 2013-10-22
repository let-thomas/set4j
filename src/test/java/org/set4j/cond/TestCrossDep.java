package org.set4j.cond;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.Set4Module;
import org.set4j.Set4Nullable;
import org.set4j.Set4Register;
import org.set4j.Set4Value;
import org.set4j.Set4Values;
import org.set4j.When;

/**
 * @author Tomas Mikenda
 *
 */
public class TestCrossDep
{
	public class A
	{
		
		@Set4Register
		@Set4Nullable
		String name;

		
		@Set4Values ({
			@Set4Value(value="svr1", when=@When(what="b.custName", eq="default")),
			@Set4Value(value="svr2", when=@When(what="b.custName", eq="ACME")),
			@Set4Value(value="svr3", when=@When(what="b.custName", eq="BOOT &co"))
		})
		String server;
		
		@Set4Module
		public B b;
	}
	
	public static class B
	{
		@Set4Values ({
			@Set4Value(value="default"),
			@Set4Value(value="ACME", when=@When(what="name", eq="cust1")),
			@Set4Value(value="BOOT &co", when=@When(what="name", eq="cust2"))
		})
		String custName;
		
	}
	
	@Test
	public void testNull()
	{
		A a = new A();
		Initializer.init(a);
		Assert.assertEquals("default", a.b.custName);
		Assert.assertEquals("svr1", a.server);
	}
	
	@Test
	public void testDefault()
	{
		A a = new A();
		a.name = "testenv";
		Initializer.init(a);
		Assert.assertEquals("default", a.b.custName);
		Assert.assertEquals("svr1", a.server);
	}
	@Test
	public void testAcme()
	{
		A a = new A();
		a.name = "cust1";
		Initializer.init(a);
		Assert.assertEquals("ACME", a.b.custName);
		Assert.assertEquals("svr2", a.server);
	}	
	@Test
	public void testBoots()
	{
		A a = new A();
		a.name = "cust2";
		Initializer.init(a);
		Assert.assertEquals("BOOT &co", a.b.custName);
		Assert.assertEquals("svr3", a.server);
	}

    @After
    public void uninit()
    {
        Initializer.uninitialize(A.class);
    }

}
