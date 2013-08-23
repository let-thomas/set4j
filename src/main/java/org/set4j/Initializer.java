package org.set4j;

import org.set4j.impl.ClassHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

//import com.tieto.setting.Setting;
//import com.tieto.setting.SysProp;
import org.set4j.impl.ClassHandler;

/**
 * @author Tomas Mikenda
 *
 */
public class Initializer
{
	private static boolean mTraceSuperClass = true;
	public enum LogLevel { DEBUG, INFO, ERROR };
	protected static LogLevel logLevel = LogLevel.INFO;  
	
	public static <T> T init(T settingObject) throws Set4JException
	{
		return initClass(settingObject.getClass(), settingObject, null, null);
	}
	
	public static <T> T init(Class<T> settingClass) throws Set4JException
    {
		return initClass(settingClass, null, null, null);
    }

	/**
	 * @param settingClass
	 * @param overrides - forced values, usable for unit tests initialization.
	 * @return
	 * @throws Set4JException
	 */
	public static <T> T init(Class<T> settingClass, Properties overrides) throws Set4JException
    {
		return initClass(settingClass, null, null, overrides);
    }
	
	public static <T> T init(T settingObject, String args[]) throws Set4JException
	{
		return initClass(settingObject.getClass(), settingObject, args, null);
	}


	/**
     * @param string
     * @param settingObject
     * @param args
     */
    //private static <T> T initClass(String prefix, Class<T> settingClass, T settingObject, String[] args)
    @SuppressWarnings("unchecked")
    private static <T> T initClass(Class<? extends Object> settingClass, T settingObject, String[] args, Properties overrides)
    	throws Set4JException
    {
    	String prefix = "";
		T instance = settingObject;
		ClassHandler handler = new ClassHandler(prefix, settingClass);
		handler.clearRegistry(); // !!! FIXME TODO - this implies having only one registry !!!
		
		if (instance == null)
		{
			instance = (T)handler.createInstance();
		}
		//System.out.println("init: " + settingClass.getName() + "; inst: " + instance + "; handler" + handler);
		/*
		// TODO journal (i.e. value history)
		// for journal-ing it is needed to do it in reverse order (I think) i.e. from
		// but then there is problem with setting some values which comes later, like dyn name of prop files ... 
		// 1. set defaults
		handler.defaults(instance);
		
		// 2. fire error if there are not existing (fake) wrappers
		handler.throwErrorIfNotDeclared();
		
		// 3. propfile(s)
		handler.loadFromPropFiles(overrides);
		
		// order 4 - SysProps
		handler.overrideFromSysProps();
		
		// order 4 - CmdLine
		// from args[]
		
		// order 5 - DB
		
		// order 6 overrides
		handler.overrides(overrides);
		*/
		handler.analyze(instance);
		handler.defaults();
		
		// top down values pushing
		handler.overrides(overrides, true);
		//handler.overrides(System.getProperties(), true);
		handler.overrideFromSysProps(true);
		handler.loadFromPropFiles(true);
		//handler.defaults();
		//handler.defaults(instance);
		handler.throwErrorIfNotDeclared();
		
		// last. run check
		handler.check(instance);
		
		// properties pushback
		handler.propsPushBack();
		
		// TODO: allow again setting values?
		// for mbean?
		handler.registerMBean();
		
		return instance;
    }

	public static Field[] getFields(@SuppressWarnings("rawtypes") Class cls)
	{
		ArrayList<Field> aFields = new ArrayList<Field>();
		if (cls != null && cls != Object.class)
		{
			for (Field fld : cls.getDeclaredFields())
			{
				aFields.add(fld);
			}
			if (mTraceSuperClass)
				for (Field fld : getFields(cls.getSuperclass()))
				{
					aFields.add(fld);
				}
		}
		Field ret[] = new Field[aFields.size()];
		return aFields.toArray(ret);
	}
    
}
