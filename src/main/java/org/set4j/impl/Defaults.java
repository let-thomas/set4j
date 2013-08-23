/**
 * 
 */
package org.set4j.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Tomas Mikenda
 * 
 */
public class Defaults implements InvocationHandler
{
	public static <A extends Annotation> A of(Class<A> annotation)
	{
		return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation }, new Defaults());
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		return method.getDefaultValue();
	}

	/*
	 * http://stackoverflow.com/questions/266903/create-annotation-instance-with-defaults-in-java
	 * 
	 * Settings s = Defaults.of(Settings.class);
	 * System.out.printf("%s\n%s\n%s\n", s.a(), s.b(), s.c());
	 */
	/*
	 * nebo
	 * class GetSettings
		{
		    public static void main ( String [ ] args )
		    {
		    @ Settings final class c { }
		    Settings settings = c . class . getAnnotation ( Settings . class ) ;
		    System . out . println ( settings . aaa ( ) ) ;
		    }
		}


	 * java.lang.reflect.ParameterizedType
	 **/
}
