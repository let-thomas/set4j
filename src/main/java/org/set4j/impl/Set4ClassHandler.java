package org.set4j.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.set4j.Set4SysProp;
import org.set4j.Set4Value;
import org.set4j.When;

/**
 * @author Tomas Mikenda
 *
 */
public class Set4ClassHandler implements InvocationHandler
{
	String value = null;
	boolean sysProp = false;
	String sysPropName = null;
	//When when[]
			
	public Set4ClassHandler(Set4SysProp aSysProp)
	{
		sysProp = true;
		sysPropName = aSysProp.name();
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object obj, Method method, Object[] arg2) throws Throwable
	{
		if (method.getName().equals("sysProp"))
		{
			return sysProp;
		}
		if (method.getName().equals("when"))
		{
			return new When[] {};
		}
		if (method.getName().equals("value"))
		{
			return Set4Value.NULL;
		}
		if (method.getName().equals("sysPropName"))
		{
			return sysPropName;
		}
		// TODO Auto-generated method stub
		//return null;
		throw new Throwable("ToDo:" + obj.getClass().getName() + ": " + method.getName());
	}

}
