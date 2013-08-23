package org.set4j.cmdline;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.set4j.CmdLine;
import org.set4j.Set4Args;

//@CmdHelpIfError
@Deprecated
public class TestSingleOpts
{
	@Set4Args(option="logname", optAttr="user", help="SVN's user")
	private String mUser = null ;
	public String getUser()
    {
    	return mUser;
    }


	@Test
	public void test1()
	{
		final String args[] = new String[] {"-logname", "somename"};
		CmdLine.loadParams(args, this);
		Assert.assertEquals("somename", getUser());
	}
	
	@Test
	public void test2()
	{
		final String args[] = new String[]{};
		CmdLine.loadParams(args, this);
		Assert.assertNull(getUser());
	}
	
	
	//@Test
	//(expected = ExceptionCmdLine.class)
	@Ignore
	public void testHelp()
	{
		final String args[] = new String[] {"bla"};
		//try
        {
			CmdLine.loadParams(args, this);
	        
        } /*catch (Exception e)
        {
        	System.out.println(e);
        	e.printStackTrace();
        }*/
	}
	
}
