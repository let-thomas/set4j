package org.set4j.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.set4j.Set4Args;
import org.set4j.Set4Check;
import org.set4j.Set4Class;
import org.set4j.Set4JChecker;
import org.set4j.Set4JException;
import org.set4j.Set4Module;
import org.set4j.Set4Nullable;
import org.set4j.Set4PropFile;
import org.set4j.Set4Register;
import org.set4j.Set4SysProp;
import org.set4j.Set4SysPropPushBack;
import org.set4j.Set4Value;
import org.set4j.Set4Values;
import org.set4j.When;
import org.set4j.impl.mbean.BeanExploder;

/**
 * @author Tomas Mikenda
 *
 */
public class ClassHandler implements InvocationHandler, DynamicMBean
{
	private Class<?> mSourceClass = null;
	private String mParentName = null; 
	private AttrWrapper mAW = null;
	HashMap<String, Object> mVals = new HashMap<String, Object>();
	HashMap<String, ClassHandler> mModules = new HashMap<String, ClassHandler>();
	//!	HashMap<AttrWrapper, TreeSet<Set4Value>> mDelayedEval = new HashMap<AttrWrapper, TreeSet<Set4Value>>();
	
	protected static HashMap<String, AttrWrapper>mAllPropsRegistry = new HashMap<String, AttrWrapper>(); // FIXME TODO static?!
	private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
	
	public ClassHandler(String parentName, Class<?> aClass)
	{
		// TODO sanity check
		mParentName = parentName;
		mSourceClass = aClass;
		/*
		if (aClass.isAnnotationPresent(Set4Class.class))
		{
			Set4Class xx = aClass.getClass().getAnnotation(Set4Class.class);
			// todo uz nevim, co to melo byt
			throw new Set4JException("unhandled!");
		}
		*/
	}
	
	
	private ClassHandler(String parentName, AttrWrapper aW)
    {
		// TODO sanity check
		mParentName = parentName;
	    mAW = aW;
	    mSourceClass = mAW.getType();
    }

	public void analyze(Object instance)
	{
		List<AttrWrapper> itemList = new ArrayList<AttrWrapper>();
		itemList.addAll(getCfgMethodItems(mSourceClass));
		itemList.addAll(getCfgFieldItems(mSourceClass));
		if (itemList.size() == 0) throw new Set4JException("Class without any provided annotation: " + mSourceClass.getName());
		for (AttrWrapper aw : itemList)
		{
			String name = null;	// name for registry/parent
			if (mParentName.length() == 0)
				name = aw.getStrippedName();
			else
				name = mParentName + "." + aw.getStrippedName();
			
			// is not there aw already "pre-registered" due to dependency?
			AttrWrapper bindAW = mAllPropsRegistry.get(name);
			if (bindAW != null)
			{
				// copy aw -> bindAW
				bindAW.populateFrom(aw);
				aw = bindAW;
			}
			
			if (aw.isAnnotationPresent(Set4Module.class))
			{
				//mModules.put(aw.getStrippedName(), new ClassHandler(name, aw.getType())); 
				mModules.put(aw.getName(), new ClassHandler(name, aw));
				
				continue;
			}
			
			mAllPropsRegistry.put(name, aw);
			String sysPropName = name;
			if (aw.isAnnotationPresent(Set4SysProp.class))
			{
				Set4SysProp ann = aw.getAnnotation(Set4SysProp.class);
				if (ann.parentPrefix())
				{
					sysPropName = getParentName().length() == 0 ? ann.name() : getParentName() + "." + ann.name();
				} else
				{
					sysPropName = ann.name();
				}
			}
			aw.setSysPropName(sysPropName);
			
			aw.setInstance(instance);
		}
		// now process modules
		// check or instance sub nodes
		for (String name : mModules.keySet())
		{
			ClassHandler han = mModules.get(name);
			Object mod = null;
			if (han.getSourceClass().isInterface())
			//if (Proxy.class == instance.getClass() || Proxy.class.isAssignableFrom(instance.getClass()))
			{
				//TODO here!!! this is ok when han is iface, not for class!!!
				mod = Proxy.newProxyInstance(han.getSourceClass().getClassLoader(),
	                     new Class[] { han.getSourceClass() },
	                     han);
				mVals.put(name, mod);
				
				// in case of parent class and module interface, we have put to class our instance
				//if (han.mAW.getInstance() == null)
				AttrWrapper parentAW = mAllPropsRegistry.get(name);
				//if (parentAW.getDeclaringHandler().)
				//if (han.mAW.getValue or instance == null)
				{
					han.mAW.setInstance(instance);
					han.mAW.setValue(mod);
				}
				
			} else
			{
				// it is "original class"
				// is sub-node instantiated?
				han.mAW.setInstance(instance);
				mod = han.mAW.getValue();
				if (mod == null)
				{
					// create instance
					mod = han.createInstance();
					han.mAW.setValue(mod);
				} else
				{
					// any?
					//throw new Set4JException("Unimplemented!");
				}
			} // if proxy else 
			han.analyze(mod);
		}
	}
	public void defaults()
	{
		for (String key: mAllPropsRegistry.keySet())
		{
			AttrWrapper aw = mAllPropsRegistry.get(key);
			aw.setValueFromAnn();
			//if (aw.isFake()) throw new Set4JException("Missing referenced property: " + key);
		}
	}
	
	public void defaults_was(Object instance)
	{
		List<AttrWrapper> itemList = new ArrayList<AttrWrapper>();
		itemList.addAll(getCfgMethodItems(mSourceClass));
		itemList.addAll(getCfgFieldItems(mSourceClass));
		if (itemList.size() == 0) throw new Set4JException("Class without any provided annotation: " + mSourceClass.getName());
		
		for (AttrWrapper aw : itemList)
		{
			//Props props = null;
			//TreeSet<Set4Value> t = new TreeSet<Set4Value>(new PropComp());
			
			String name = null;	// name for registry/parent
			if (mParentName.length() == 0)
				name = aw.getStrippedName();
			else
				name = mParentName + "." + aw.getStrippedName();
			
			// is not there aw already "pre-registered" due to dependency?
			AttrWrapper bindAW = mAllPropsRegistry.get(name);
			if (bindAW != null)
			{
				// copy aw -> bindAW
				bindAW.populateFrom(aw);
				aw = bindAW;
			}
			
			aw.setSysPropName(name);
			if (aw.isAnnotationPresent(Set4Module.class))
			{
				//mModules.put(aw.getStrippedName(), new ClassHandler(name, aw.getType())); 
				mModules.put(aw.getName(), new ClassHandler(name, aw));
				
				continue;
			}
			
			mAllPropsRegistry.put(name, aw);
			aw.setInstance(instance);
			aw.setValueFromAnn();
			/*
			if (aw.isAnnotationPresent(Set4Register.class))
			{
				continue; // is in registry already (above code)
			}
			
			if (aw.isAnnotationPresent(Set4Values.class))
			{
				for (Set4Value p : aw.getAnnotation(Set4Values.class).value())
					t.add( p );
			}
			if (aw.isAnnotationPresent(Set4Value.class))
			{
				t.add( aw.getAnnotation(Set4Value.class));
			}
			if (aw.isAnnotationPresent(Set4SysProp.class))
			{
                //+Set4Value s4v = (Set4Value) Proxy.newProxyInstance(Set4Value.class.getClassLoader(), new Class[] {Set4Value.class}, new Set4ClassHandler(aw.getAnnotation(Set4SysProp.class)));
                //s4v.value = null;
                //+t.add(s4v);
				Set4SysProp ann = aw.getAnnotation(Set4SysProp.class);
				aw.setSysPropName(mParentName.length() == 0 ? ann.name() : mParentName + "." + ann.name());
			}
			
			// try to evaluate one by one
			setValueFromAnn(aw, t);
			*/
			
			/*
			String strVal = null;
			for (Set4Property p : t)
			{
				When when[] = p.when();
				if (when[0].what().length() == 0)
				{
					// direct value
					strVal = p.value();
				} else
				{
					// TODO HERE postpone if some condition is not evaluable
					
					// is/are names in registry already?
					for (int i = 0; i < when.length; i++)
					{
						String key = when[i].what();
						if (mAllPropsRegistry.containsKey(key))
						{
					// test conditions
					// postpone processing
					// mDelayedEval.put(aw, t);
						} else
						{
							
						}
					}
					throw new Set4JException("Unimplemented!");
					
				}
			}
			if (strVal == null)
				throw new Set4JException("No default value found!");
			// HERE FIXME TODO HERE !!!
			// TODO store for post processing if needed
			// ?????
			// order 2 - SysProps
			// order 3 - CmdLine
			// order 4 - DB

			// ?????
			//cast to target type
			if (Proxy.class.isAssignableFrom(instance.getClass()))
			{
				// for proxy calls
				mVals.put(aw.getName(), aw.convertString2Type(strVal)); 
			} else
			{
				// store into object
				aw.setValue(strVal);
			}
			*/
		}
		
		// now process modules
		// check or instance sub nodes
		for (String name : mModules.keySet())
		{
			ClassHandler han = mModules.get(name);
			Object mod = null;
			if (han.getSourceClass().isInterface())
			//if (Proxy.class == instance.getClass() || Proxy.class.isAssignableFrom(instance.getClass()))
			{
				//TODO here!!! this is ok when han is iface, not for class!!!
				mod = Proxy.newProxyInstance(han.getSourceClass().getClassLoader(),
	                     new Class[] { han.getSourceClass() },
	                     han);
				mVals.put(name, mod);
			} else
			{
				// it is "original class"
				// is sub-node instantiated?
				han.mAW.setInstance(instance);
				mod = han.mAW.getValue();
				if (mod == null)
				{
					// create instance
					mod = han.createInstance();
					han.mAW.setValue(mod);
				} else
				{
					// any?
					//throw new Set4JException("Unimplemented!");
				}
			} // if proxy else 
			han.defaults_was(mod);
		}
	}

	/**
     * @param instance
     * @param t
     */
    private void setValueFromAnn(AttrWrapper aw, TreeSet<Set4Value> t)
    {
    	boolean nullable = aw.isAnnotationPresent(Set4Nullable.class);
		String strVal = null;
		Set4Value usedProp = null;
		int usedLevel = -1;
		//String sysPropName = aw.getName();
		//aw.setSysPropName(mParentName.length() > 0 ? mParentName + '.' + aw.getName() : aw.getName()); 
		for (Set4Value p : t)
		{
			When when[] = p.when();
			if (when.length == 0 || when[0].what().length() == 0)
			{
				// direct value
				strVal = p.value();
				if (strVal == Set4Value.NULL) strVal = null;
				if (usedLevel == 0)
					throw new Set4JException("Ambiguous conditions, " +p+ " and " + usedProp);
				usedProp = p;
				usedLevel = 0;
				/*
				if (p.sysProp() || p.sysPropName().length() > 0)
				{
					if (p.sysPropName().length() > 0)
						//sysPropName = p.sysPropName();
						aw.setSysPropName(mParentName.length() > 0 ? mParentName + '.' + p.sysPropName() : p.sysPropName()); 
					else
						//sysPropName = aw.getStrippedName();
						aw.setSysPropName(mParentName.length() > 0 ? mParentName + '.' + aw.getStrippedName() : aw.getStrippedName()); 
				}
				*/
			} else
			{
				
				for (int i = 0; i < when.length; i++)
				{
					String key = when[i].what();
					// is name in registry already?
					if (mAllPropsRegistry.containsKey(key))
					{
						AttrWrapper attrValue = mAllPropsRegistry.get(key);
						
						//bind as observer
						attrValue.addObserver(aw);
						
						Object testValue = attrValue.getValue();
						// test conditions
						boolean test = false;
						if (testValue !=null)
						{
							if (when[i].eq().length() > 0)
							{
								test = testValue.equals(when[i].eq());
							} else
							if (when[i].ne().length() > 0)
							{
								test = ! testValue.equals(when[i].eq());
							} else
							{
								throw new Set4JException("Unimplemented!");
							}
						} else
						{
							if (when[i].notNull().length() > 0)
							{
								test = !Boolean.valueOf(when[i].notNull());
							}
						}
						
						if (test)
						{
							strVal = p.value();
							if (strVal == Set4Value.NULL) strVal = null;
							if (p.when().length == usedLevel)
								throw new Set4JException("Ambiguous conditions, " +p+ " and " + usedProp);
							usedLevel = p.when().length;
							usedProp = p;
							/*
							if (p.sysProp() || p.sysPropName().length() > 0)
							{
								if (p.sysPropName().length() > 0)
									//sysPropName = p.sysPropName();
									aw.setSysPropName(mParentName.length() > 0 ? mParentName + '.' + p.sysPropName() : p.sysPropName()); 
								else
									//sysPropName = aw.getStrippedName();
									aw.setSysPropName(mParentName.length() > 0 ? mParentName + '.' + aw.getStrippedName() : aw.getStrippedName()); 
							}
							*/
						}
					} else
					{
						// check if key exists, if not create 'pre' one
						//mDelayedEval.put(aw, t);
						AttrWrapper keyAW = mAllPropsRegistry.get(key);
						if (keyAW == null)
						{
							keyAW = new AttrWrapper();
							mAllPropsRegistry.put(key, keyAW);
						}
						keyAW.addObserver(aw);
						return;
					}
				}
				/**/
				//throw new Set4JException("Unimplemented!");
				
			}
		}
		
		
		if (strVal == null) return;
		aw.setValue(strVal);
    }


	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable
	{
		String mthName = arg1.getName();
		Object val = null;
		
		if (mthName.startsWith("get") || mthName.startsWith("is") || (mVals.containsKey(mthName) && arg1.getReturnType() != null))
		{
			val = mVals.get(arg1.getName());
		} else
		// TODO is initialized?
		// TODO is set?
		// TODO check args
		if (mthName.equals("toString"))
		{
			return "todo: toString;" + hashCode();
		} else
		{
			throw new Set4JException("Unimplemented! mth=" + this.getSourceClass().getName() + '.' + mthName  );
		}
		return val;
	}
	
	public void setValue(String name, Object value)
	{
		mVals.put(name, value);
	}
	
	public Object getValue(String name)
	{
		return mVals.get(name);
	}

	public HashMap<String, ClassHandler> getModules()
    {
    	return mModules;
    }

	public Class<?> getSourceClass()
    {
    	return mSourceClass;
    }


	/**
     * @return
     */
    public Object createInstance()
    {
    	Object instance;
	    // is it class or abstract class or interface?
		if (mSourceClass.isInterface() )//|| Modifier.isAbstract ( mSourceClass.getModifiers()))
		{
			
			instance = Proxy.newProxyInstance(mSourceClass.getClassLoader(),
                     new Class[] { mSourceClass },
                     this);
		} else if (Modifier.isAbstract ( mSourceClass.getModifiers()))
		{
			throw new Set4JException("Error creating class " + mSourceClass.getName() + "; it is not possible to use abstract classes!");
			/*
			Constructor<?> ctor;
            try
            {
	            ctor = mSourceClass.getConstructor(new Class[] { int.class });
	            instance = ctor.newInstance(new Object[] { 1 });
            } catch (Exception e)
            {
	            e.printStackTrace();
	            
            }
			*/
				
		} else // is class
		{
			try
            {
	            instance = mSourceClass.newInstance();
            } catch (Exception e)
            {
            	e.printStackTrace();
            	if (mSourceClass.getName().contains("$"))
            		throw new Set4JException("Error instantiate inner class " + mSourceClass.getName(), e);
            	else
            		throw new Set4JException("Error instancing class " + mSourceClass.getName() + "; inner class?", e);
            }
		}
		return instance;
    }


	/**
     * @return
     */
    public Collection<? extends AttrWrapper> getCfgMethodItems(Class<?> settingClass)
    {
    	return getCfgMethodItems(settingClass, new HashMap<String, AttrWrapper>());
    }
    
    public Collection<? extends AttrWrapper> getCfgMethodItems(Class<?> settingClass, Map<String, AttrWrapper> hash)
    {
    	if (settingClass == null) return hash.values();
    	if (Object.class == settingClass) return hash.values();

        for (Method m : settingClass.getDeclaredMethods())
    	{
    		if ((m.isAnnotationPresent(Set4Value.class) 
    				|| m.isAnnotationPresent(Set4Values.class) 
    				|| m.isAnnotationPresent(Set4Args.class)
    				|| m.isAnnotationPresent(Set4Module.class)
    				|| m.isAnnotationPresent(Set4Register.class)
    				|| m.isAnnotationPresent(Set4SysProp.class)
    				)
    			&& !hash.containsKey(m.getName()))
    		{
        		AttrWrapper aw = new AttrWrapper(m, this);
        		hash.put(m.getName(), aw);
    		}
    	}
        //for (Class cls : settingClass.getGenericInterfaces() )
        for (Class<?> cls : settingClass.getInterfaces() )
        {
        	getCfgMethodItems(cls, hash);
        }
        
        getCfgMethodItems(settingClass.getSuperclass(), hash);
        
        return hash.values();
    }


	/**
     * @param settingClass
     * @return
     */
    public Collection<? extends AttrWrapper> getCfgFieldItems(Class<?> settingClass)
    {
    	return getCfgFieldItems(settingClass, new HashMap<String, AttrWrapper>());
    }
    public Collection<? extends AttrWrapper> getCfgFieldItems(Class<?> settingClass, Map<String, AttrWrapper> hash)
    {
    	if (settingClass == null) return hash.values();
    	if (Object.class == settingClass) return hash.values();
    	
    	/*int fprefixLen = 0;
    	if (settingClass.isAnnotationPresent(Set4Class.class))
    	{
    		Set4Class sc = settingClass.getAnnotation(Set4Class.class);
    		fprefixLen = sc.fieldPrefix().length();
    	}*/
    	//for (Field f: getFields(settingClass))
    	for (Field f: settingClass.getDeclaredFields())
    	{
    		if ((f.isAnnotationPresent(Set4Value.class) 
    				|| f.isAnnotationPresent(Set4Values.class) 
    				|| f.isAnnotationPresent(Set4Args.class)
    				|| f.isAnnotationPresent(Set4Module.class)
    				|| f.isAnnotationPresent(Set4Register.class)
    				|| f.isAnnotationPresent(Set4Nullable.class)
    				|| f.isAnnotationPresent(Set4SysProp.class)
    				)
    			&& !hash.containsKey(f.getName()))
    		{
        		AttrWrapper aw = new AttrWrapper(f, this);
        		hash.put(f.getName(), aw);
    		}
    	}
    	getCfgFieldItems(settingClass.getSuperclass(), hash);
    	
        return hash.values();
    }


	/**
     * 
     *
    public void delayedEval()
    {
	    // grab all postponed init
    	grabAllDelayed();
    	
    	int size = mDelayedEval.size();
    	while (!mDelayedEval.isEmpty())
    	{
    		for (AttrWrapper aw : mDelayedEval.keySet())
    		{
    			TreeSet<Set4Value> t = mDelayedEval.remove(aw);
    			//mDelayedEval.
    			//???break
    			/*
    			boolean haveAll = true;
    			for (Set4Property p : t)
    			{
    				When when[] = p.when();
					// is/are names in registry already?
					for (int i = 0; i < when.length; i++)
					{
						String key = when[i].what();
						 
						if (! mAllPropsRegistry.containsKey(key))
						{
							haveAll = false;
							break;
						}
					}
					if (!haveAll) break;
    			}
    			if (!haveAll) continue;
    			
    			// do evaluate
    			 * 
    			 ** /
    			aw.getDeclaringHandler().setValueFromAnn(aw, t, aw.isAnnotationPresent(Set4Nullable.class)); 
    			
    		}
    		
    		// fail if no change
    		if (size == mDelayedEval.size())
    		{
    			StringBuffer buf = new StringBuffer();
    			for (AttrWrapper aw : mDelayedEval.keySet())
        		{
    				buf.append( aw.getName() ).append(',');
        		}
    			throw new Set4JException("Evaluation not possible (undefined value) or Conditions evaluation deadlock! list not set:" + buf.toString());
    		}
    		size = mDelayedEval.size();
    			
    	}
    	
    	//HashMap<AttrWrapper, TreeSet<Set4Property>> all = new HashMap<AttrWrapper, TreeSet<Set4Property>>();
    	
	    
    }
    
    private HashMap<AttrWrapper, TreeSet<Set4Value>> grabAllDelayed()
    {
		for (String name : mModules.keySet())
		{
			ClassHandler han = mModules.get(name);
			mDelayedEval.putAll(han.grabAllDelayed());
		}
		return mDelayedEval;
    }
    /**/
    
	private static boolean mTraceSuperClass = true;
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
	
	public void check(Object instance)
	{
		if (mSourceClass.isAnnotationPresent(Set4Class.class))
		{
			Set4Class set = mSourceClass.getAnnotation(Set4Class.class);
			engine.put(set.jsName(), instance);
		}
		for (String key: mAllPropsRegistry.keySet())
		{
			AttrWrapper aw = mAllPropsRegistry.get(key);
			Object val = aw.getValue();
			
			if (aw.isAnnotationPresent(Set4Check.class))
			{
				Set4Check check = aw.getAnnotation(Set4Check.class);
				if (check.check().length() > 0)
				{
					if ("assertNotNull".equals(check.check())) 
					{
						Set4JChecker.assertNotNull(val);
					} else
					{
						throw new Set4JException("Unknown check method: " + check.check());
					}
				}
				if (check.js().length() > 0)
				{
					//throw new RuntimeException("ToDo");
					try
                    {
	                    engine.eval(check.js());
                    } catch (ScriptException e)
                    {
	                    // TODO Auto-generated catch block
	                    //e.printStackTrace();
	                    throw new Set4JException("Check exception: " + e, e);
                    }
				}
			} else
			if (val == null) 
			{
				if (!aw.isAnnotationPresent(Set4Nullable.class)) throw new Set4JException("No value found for item '" + key + "' !");
			}
			//aw.toString();
		}
	}
	
	public void overrideFromSysProps()
	{
		overrideFromSysProps(false);
	}
	
	public void overrideFromSysProps(boolean forbidOverride)//Object instance)
	{
		for (String key: mAllPropsRegistry.keySet())
		{
			AttrWrapper aw = mAllPropsRegistry.get(key);
			if (aw.isFake()) throw new Set4JException("Missing referenced property: " + key);
			String strVal = System.getProperty(aw.getSysPropName());
			if (strVal == null) continue;
			aw.setValue(strVal);
			aw.forbidOverride();
		}
	}

	/**
     * 
     */
    public void propsPushBack()
    {
		for (String key: mAllPropsRegistry.keySet())
		{
			AttrWrapper aw = mAllPropsRegistry.get(key);
			if (aw.isAnnotationPresent(Set4SysPropPushBack.class))
			{
				String name = key;
				if (aw.isAnnotationPresent(Set4SysProp.class))
				{
					name = aw.getAnnotation(Set4SysProp.class).name();
				}
				System.setProperty(name, aw.getValue().toString());
			}
		}
    }
	
	public void overrides(Properties p)
	{
		overrides(p, false);
	}
	
	public void overrides(Properties p, boolean forbidOverride)
	{
		if (p == null) return;
		/*for (String key: mAllPropsRegistry.keySet())
		{
			AttrWrapper aw = mAllPropsRegistry.get(key);
			String strVal = p.getProperty(key);
			if (strVal == null) continue;
			aw.setValue(strVal);
		}*/
		for (Object key: p.keySet())
		{
			AttrWrapper aw = mAllPropsRegistry.get(key);
			if (aw == null) continue; // or report problem?
			String strVal = p.getProperty((String)key);
			if (strVal == null) continue;
			aw.setValue(strVal);
			aw.forbidOverride();
		}

	}
	
	public void clearRegistry()
	{
		mAllPropsRegistry.clear();
	}


	public String getParentName()
    {
    	return mParentName;
    }


	/**
     * 
     */
    public void throwErrorIfNotDeclared()
    {
		for (String key: mAllPropsRegistry.keySet())
		{
			AttrWrapper aw = mAllPropsRegistry.get(key);
			if (aw.isFake()) throw new Set4JException("Missing referenced property: " + key);
		}
    }


	/**
     * 
     */
    public void loadFromPropFiles(boolean forbidOverride)//Properties overrides)
    {
    	Set4PropFile pfp = getClassAnnotation(Set4PropFile.class);
    	//Set4PropFile pfp = mSourceClass.getAnnotation(Set4PropFile.class);
    	//if (isClassAnnotationPresent(Set4PropFile.class))
    	if (pfp != null)
    	{
    		//Set4PropFile pfp = mSourceClass.getAnnotation(Set4PropFile.class);
    		int levels = pfp.files().length;
    		String root = pfp.location();
    		if (root == null) root = "";
    		else root = root + '/';
    		Properties[] pff = new Properties[levels];
    		if (LocationType.file == pfp.type())
    		{
				throw new Set4JException("TODO");
    		} else
    		if (LocationType.resource == pfp.type())
    		{
    			int idx = 0;
    			for (String fname : pfp.files())
    			{
    				String fileName = substVars(root + fname, null);
    				InputStream io = getResourceStream(fileName);
    				pff[idx] = new Properties();
    				if (io == null) System.err.println("set4j: Cannot locate resource " + fname + " -> " + fileName);
                    else
	                    try
                        {
	                    	pff[idx].load(io);
                        } catch (IOException e)
                        {
	                        e.printStackTrace();
                        }
    				idx++;
    			}
    		} else throw new Set4JException("Unknown location type! " + pfp.type());
    		// must fill it in reverse order
    		for (int idx = pff.length-1; idx >-1; idx--)
    		{
    			overrides(pff[idx], forbidOverride);
    		}
    	}
	    
    }
    


	/**
     * @param class1
     * @return
     */
    private boolean isClassAnnotationPresent(Class<? extends Annotation> annClass)
    {
    	return isClassAnnotationPresent(annClass, mSourceClass);
    }
    /**
     * @param annClass
     * @param srcClass
     * @return
     */
    private boolean isClassAnnotationPresent(Class<? extends Annotation> annClass, Class<?> srcClass)
    {
    	if (srcClass == null || Object.class == srcClass) return false;
    	if (srcClass.isAnnotationPresent(annClass)) return true;
    	return isClassAnnotationPresent(annClass, srcClass.getSuperclass());
    }
    
    /**
     * Returns annotation of the class and its superclass if defined, null otherwise.
     * @param annClass
     * @return
     */
    private <A extends Annotation> A getClassAnnotation(Class<A> annClass)
    {
    	return getClassAnnotation(annClass, mSourceClass);
    }
    /**
     * Returns first mathing annotation of the class and its superclass if defined, null otherwise.
     * 
     * @param annClass
     * @param srcClass
     * @return
     */
    public static <A extends Annotation> A getClassAnnotation(Class<A> annClass, Class<?> srcClass)
    {
    	if (srcClass == null || Object.class == srcClass) return null;
    	if (srcClass.isAnnotationPresent(annClass)) return srcClass.getAnnotation(annClass);
    	if (srcClass.isInterface())
    	{
            for (Class<?> cls : srcClass.getInterfaces() )
            {
            	A ann = getClassAnnotation(annClass, cls);
            	if (ann != null) return ann;
            }
    		
    		
    	}
    	
    	return getClassAnnotation(annClass, srcClass.getSuperclass());
    }


	private InputStream getResourceStream(String resName)
    {
		ClassLoader ldr = Thread.currentThread().getContextClassLoader();
		//log.debug("Trying load " + resName + " using " + ldr);
		InputStream res = ldr.getResourceAsStream(resName);
		
		if (res == null)
		{
			ldr = getClass().getClassLoader();
			//log.debug("Trying load " + resName + " using " + ldr);
			res = ldr.getResourceAsStream(resName);
		}
		
		if (res == null)
		{
			ldr = ClassLoader.getSystemClassLoader();
			//log.debug("Trying load " + resName + " using " + ldr);
			res = ldr.getResourceAsStream(resName);
		}
		
		if (res == null)
		{
			//log.debug("Trying load " + resName + " using ClassLoader.getSystemResource");
			res = ClassLoader.getSystemResourceAsStream(resName);
		}
		
		//if (res == null) throw new Set4JException("Cannot ");
		return res;
    }
    
    static String DELIM_START = "${";
    static char   DELIM_STOP  = '}';
    static int DELIM_START_LEN = 2;
    static int DELIM_STOP_LEN  = 1;
	public static String substVars(String val, Properties overrides) throws IllegalArgumentException
	{
		return substVars(val, overrides, null);
	}
    
	public static String substVars(String val, Properties overrides, AttrWrapper noErrDoObserveAttr) throws IllegalArgumentException
	{

		StringBuffer sbuf = new StringBuffer();

		int i = 0;
		int j, k;

		while (true)
		{
			j = val.indexOf(DELIM_START, i);
			if (j == -1)
			{
				// no more variables
				if (i == 0)
				{ // this is a simple string
					return val;
				} else
				{ // add the tail string which contains no variables and return
				  // the result.
					sbuf.append(val.substring(i, val.length()));
					return sbuf.toString();
				}
			} else
			{
				sbuf.append(val.substring(i, j));
				k = val.indexOf(DELIM_STOP, j);
				if (k == -1)
				{
					throw new IllegalArgumentException('"' + val
					        + "\" has no closing brace. Opening brace at position " + j + '.');
				} else
				{
					j += DELIM_START_LEN;
					String key = val.substring(j, k);
					// first try in System properties
					AttrWrapper aw = mAllPropsRegistry.get(key);
					String replacement = null;
					if (aw != null)
					{
						replacement = aw.getValue() == null ? null : aw.getValue().toString();
						
						// add to observers to be notified in case of change!
						if (noErrDoObserveAttr != null) aw.addObserver(noErrDoObserveAttr);
						
						if (replacement == null) return ""; // does not matter for now, comes later (hopefully)
					}
						
					
					// then try props parameter
					if (replacement == null && overrides != null)
					{
						replacement = overrides.getProperty(key);
					}
					// then try props parameter
					if (replacement == null && key.startsWith("sp:"))
					{
						replacement = System.getProperty(key.substring(3));
					}
					

					if (replacement != null)
					{
						// Do variable substitution on the replacement string
						// such that we can solve "Hello ${x2}" as "Hello p1"
						// the where the properties are
						// x1=p1
						// x2=${x1}
						String recursiveReplacement = substVars(replacement, overrides, noErrDoObserveAttr);
						sbuf.append(recursiveReplacement);
					} else
					{
						if (noErrDoObserveAttr != null)
						{
							System.out.println("set4j: not found " + key + " in " + noErrDoObserveAttr.getName() +"; added observer");
							if (aw == null)
							{
								/* create fake one? must exist though!
								aw = new AttrWrapper();
    							ClassHandler.mAllPropsRegistry.put(key, aw);
    							*/
								throw new Set4JException("No item " + key + "; defined in " + noErrDoObserveAttr.getName());
							}
							//aw.addObserver(noErrDoObserveAttr);
						} else
						{
							//System.err.println("set4j: cannot find");
							throw new Set4JException("No value found for item " + key + " in value: " + val);
						}
					}
					i = k + DELIM_STOP_LEN;
				}
			}
		}
	}

	//
	// MBean stuff
	// ===========
	//

	/**
     * 
     */
    public void registerMBean()
    {
    	/*
    	String namePrefix = "set4j:";
    	MBeanServer beanServer = MBeanServerFactory.newMBeanServer();
    	try
        {
	        ObjectName beanName = new ObjectName(namePrefix + "node=main");
	        ObjectInstance oi = beanServer.createMBean(this.getClass().getName(), beanName);
        } catch (Exception e)
        {
        	throw new Set4JException(e);
        }
		*/
    	new BeanExploder().createBeans(this);
    }

    public ClassHandler()
    {
    	// for MBean
    }

	/* (non-Javadoc)
     * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String arg0) throws AttributeNotFoundException, MBeanException, ReflectionException
    {
	    // TODO Auto-generated method stub
	    return null;
    }


	/* (non-Javadoc)
     * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
     */
    @Override
    public AttributeList getAttributes(String[] arg0)
    {
	    // TODO Auto-generated method stub
	    return null;
    }


	/* (non-Javadoc)
     * @see javax.management.DynamicMBean#getMBeanInfo()
     */
    @Override
    public MBeanInfo getMBeanInfo()
    {
	    // TODO Auto-generated method stub
    	MBeanInfo info = new MBeanInfo(null, null, null, null, null, null);
	    return info;
    }


	/* (non-Javadoc)
     * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
     */
    @Override
    public Object invoke(String arg0, Object[] arg1, String[] arg2) throws MBeanException, ReflectionException
    {
	    // TODO Auto-generated method stub
	    return null;
    }


	/* (non-Javadoc)
     * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
     */
    @Override
    public void setAttribute(Attribute arg0) throws AttributeNotFoundException, InvalidAttributeValueException,
            MBeanException, ReflectionException
    {
	    // TODO Auto-generated method stub
	    
    }


	/* (non-Javadoc)
     * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
     */
    @Override
    public AttributeList setAttributes(AttributeList arg0)
    {
	    // TODO Auto-generated method stub
	    return null;
    }



}
