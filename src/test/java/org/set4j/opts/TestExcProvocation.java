/**
 * 
 */
package org.set4j.opts;

import junit.framework.Assert;

import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.Set4JException;
import org.set4j.Set4Value;
import org.set4j.Set4Values;
import org.set4j.Set4Class;
import org.set4j.When;

/**
 * @author Tomas Mikenda
 *
 */
@Set4Class(fieldPrefix="m")
public class TestExcProvocation
{
	@Set4Values({
		@Set4Value(value="one" ),
		@Set4Value(value="two", when=@When(what="noExistProp", eq="banan"))
		})
	String mName = null;
	public String getName()
	{
		return mName;
	}
	
	@Test//(expected=org.set4j.Set4JException.class)
	public void TestFailNoField()
	{
		try 
		{
			Initializer.init(this); //should fail because of missing noExistProp
	    } catch (Set4JException e)
	    {
			// but do not use @Test//(expected=org.set4j.Set4JException.class)
			// since we will test the content of message
	    	Assert.assertEquals("Missing referenced property: noExistProp", e.getMessage());
	    }
	}
	
	@Test//(expected=org.set4j.Set4JException.class)
	public void TestFailNoAnn()
	{
		try
        {
	        Initializer.init(new Object());
        } catch (Set4JException e)
        {
        	//String msg = 
        	Assert.assertEquals("Class without any provided annotation: java.lang.Object", e.getMessage());
        }
	}
}
