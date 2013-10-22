package org.set4j.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

import org.set4j.Set4JException;
import org.set4j.Set4Nullable;
import org.set4j.Set4Register;
import org.set4j.Set4SysProp;
import org.set4j.Set4Value;
import org.set4j.Set4Values;
import org.set4j.When;

/**
 * @author Tomas Mikenda
 *
 */
public class AttrWrapper 
	// implements Comparable
	extends Observable
	implements Observer
{
	private String mName = null;
	private String mStrippedName = null;
	private String mSysPropName = null;
	private Field mField = null;
	private Method mMethod = null;
	private Object mInstance = null;	// instance is either direct object or proxy with handler
	private ClassHandler mDeclaringHandler = null;
	//private TreeSet<String> mDependant = new TreeSet<String>();
	private boolean mFake = false;
	private boolean mFixed = false;
	
	protected AttrWrapper()
	{
		// only for fake creation!!!
		mFake = true;
	}
	
	public AttrWrapper(Method method, ClassHandler declaringHandler)
    {
	    super();
	    mName = method.getName(); //.substring(3);
	    mMethod = method;
	    mDeclaringHandler = declaringHandler;
    }


	public AttrWrapper(Field field, ClassHandler declaringHandler)
    {
	    super();
	    mName = field.getName();
	    mField = field;
	    mDeclaringHandler = declaringHandler;
    }
	
	//public AttrWrapper()


	public String getName()
    {
    	return mName;
    }
	
	public String getStrippedName()
	{
		if (mStrippedName == null)
		{
			if (mMethod != null)
			{
				String name = mMethod.getName();
				if (name.startsWith("set")) mStrippedName = mMethod.getName().substring(3);
				else if (name.startsWith("get")) mStrippedName = mMethod.getName().substring(3);
				else if (name.startsWith("is")) mStrippedName = mMethod.getName().substring(2);
				else mStrippedName = mMethod.getName();
				// lower? TODO
				mStrippedName = mStrippedName.substring(0, 1).toLowerCase() + mStrippedName.substring(1);
			} else
				// TODO field prefix
				//String name = f.getName().substring(fprefixLen);
				mStrippedName = mName;
		}
		
		return mStrippedName;
	}
	
	protected Class<?> getType()
	{
		if (mField != null) 
			return mField.getType();
		else
			return mMethod.getReturnType();
	}


	/**
     * @param aClass
     * @return
     */
    public boolean isAnnotationPresent(Class<? extends Annotation> aClass)
    {
    	if (mField != null) return mField.isAnnotationPresent(aClass);
    	return mMethod.isAnnotationPresent(aClass);
    }


	/**
     * @param aClass
     * @return
     */
    public <T extends Annotation>T getAnnotation(Class<T> aClass)
    {
    	if (mField != null) return mField.getAnnotation(aClass);
    	return mMethod.getAnnotation(aClass);
    }
    
	@SuppressWarnings("unchecked")
    public Object convertString2Type(String strValue)
	{
		Object aValue = null;
		Class aFldCls = getType();
		
		try
		{
			
			if (String.class == aFldCls)
			{
				aValue = strValue;
			} else
			if (boolean.class.isAssignableFrom(aFldCls)) // is it boolean?
			{
				aValue = new Boolean(strValue);
			} else
			if (long.class.isAssignableFrom(aFldCls))// || long.class == aFldCls)
			{
				aValue = new Long(strValue);
			} else
			if (int.class.isAssignableFrom(aFldCls))// || int.class == aFldCls)
			{
				aValue = new Integer(strValue);
			} else
			if (aFldCls.isEnum())
			{
				aValue = Enum.valueOf(aFldCls, strValue);
			} else
			{
				throw new Set4JException("Unhandled case! " + aFldCls.toString());
			}
		} 
		catch (Set4JException se)
		{
			throw se;
		}
		catch (Exception e)
		{
			throw new Set4JException("Error assigning value: " + strValue + " to "  + mDeclaringHandler.getClass().getName() + "." + getName() + ": " + e, e);
		}
		return aValue;
	}
	
	public Object getValue()
	{
		try
		{
			if (mField != null)
			{
				mField.setAccessible(true);
	        	return mField.get(getInstance());
			} if (Proxy.isProxyClass(mInstance.getClass()) && Proxy.getInvocationHandler(mInstance) instanceof ClassHandler)
			{
				ClassHandler ch = (ClassHandler)Proxy.getInvocationHandler(mInstance);
				return ch.getValue(mName);
				
			} else
			{
				return mMethod.invoke(getInstance(), (Object[])null);
			}
		} catch (Exception e)
		{
			throw new Set4JException("Internal get invocation error: " + e, e);
		}
			
	}
	public void setValue(String strValue)
	{
		setValue(convertString2Type(strValue));
	}
	public void setValue(Object value)
	{
        if (Log.isDebug())
        {
            StringBuffer msg = new StringBuffer();
            if (mField != null) msg.append(mField.getName());
            else msg.append(mMethod.getName());
            msg.append("='").append(value).append("'");
            if (mFixed)
            {
                msg.append(" SKIPPING!");
                Log.trace(msg.toString());
                return;
            } else
            {
                Log.trace(msg.toString());
            }
        }
		
		// should ensure types? ... will fail anyway
		String setterName = null; 
		try
		{
			if (mField != null)
			{
				mField.setAccessible(true);
				mField.set(getInstance(), value);
			} else
			{
				if (Proxy.isProxyClass(mInstance.getClass()) && Proxy.getInvocationHandler(mInstance) instanceof ClassHandler)
				{
					ClassHandler ch = (ClassHandler)Proxy.getInvocationHandler(mInstance);
					ch.setValue(mMethod.getName(), value);
					/*
					//((Proxy)mInstance).getInvocationHandler(proxy)
					InvocationHandler ih = Proxy.getInvocationHandler(mInstance); // handling the case of proxy (interface)
					try
                    {
						//Method mo = Method.class.newInstance();
	                    ih.invoke(mInstance, null, new Object[] {value});
                    } catch (Throwable e)
                    {
	                    //e.printStackTrace();
	                    throw new Set4JException("Internal SET invocation error2: " + e, e);
                    }
					//Method m = Method
					//throw new Set4JException("Internal SET invocation error2: ");
					 */
				} else
				{
					// find setter
					setterName = "s" + mMethod.getName().substring(1);
					Class<?> arg = mMethod.getReturnType();
					Method m = getInstance().getClass().getMethod(setterName, new Class[] { arg });
					m.invoke(getInstance(), new Object[] { value });
				}
			}
		
		} catch (Set4JException e)
		{
			throw e;
		} catch (NoSuchMethodException e)
		{
			throw new Set4JException("There is not setter ("+setterName+") in class " + mDeclaringHandler.getSourceClass().getName());
		} catch (Exception e)
		{
			throw new Set4JException("Internal SET invocation error: " + e, e);
		}
		
		// let observers known
	    setChanged();
	    notifyObservers();
		
	}


	public void setInstance(Object instance)
    {
    	mInstance = instance;
    }


	public Object getInstance()
    {
		if (mInstance == null) throw new Set4JException("(internal) object Instance is not set!");
    	return mInstance;
    }


	public ClassHandler getDeclaringHandler()
    {
    	return mDeclaringHandler;
    }

	public String getSysPropName()
    {
    	return mSysPropName;
    }


	public void setSysPropName(String sysPropName)
    {
    	mSysPropName = sysPropName;
    }


	@Override
    public String toString()
    {
	    return "AttrWrapper [mName=" + mName + ", mStrippedName=" + mStrippedName 
	    		//+ ", mField=" + mField 
	    		//+ ", mMethod=" + mMethod 
	    		//+ ", mInstance=" + mInstance 
	    		+ "]";
    }


	/* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg)
    {
	    // TODO FIXME update Observer !!!
	    //System.out.println("updated " + mName + "; changed is " + o);
	    //mDeclaringHandler.defaults(mInstance);
	    setValueFromAnn();
    }


	/**
     * @param aw
     */
    public void populateFrom(AttrWrapper aw)
    {
    	mName = aw.mName;
    	mStrippedName = aw.mStrippedName;
    	mSysPropName = aw.mSysPropName;
    	mField = aw.mField;
    	mMethod = aw.mMethod;
    	mInstance = aw.mInstance;	// instance is either direct object or proxy with handler
    	mDeclaringHandler = aw.mDeclaringHandler;
    	mFake = false;
    }


	/**
     * 
     *
    public void changed()
    {
	    this.setChanged();
	    notifyObservers();
    }*/

	/**
     * 
     */
    public void setValueFromAnn()
    {
		if (isAnnotationPresent(Set4Register.class))
		{
			//return; // is in registry already (code in class handler)
		}
		
		TreeSet<Set4Value> t = new TreeSet<Set4Value>(new PropComp());
		if (isAnnotationPresent(Set4Values.class))
		{
			for (Set4Value p : getAnnotation(Set4Values.class).value())
				t.add( p );
		}
		if (isAnnotationPresent(Set4Value.class))
		{
			t.add( getAnnotation(Set4Value.class));
		}
		/*
		if (isAnnotationPresent(Set4SysProp.class))
		{
            //+Set4Value s4v = (Set4Value) Proxy.newProxyInstance(Set4Value.class.getClassLoader(), new Class[] {Set4Value.class}, new Set4ClassHandler(aw.getAnnotation(Set4SysProp.class)));
            //s4v.value = null;
            //+t.add(s4v);
			Set4SysProp ann = getAnnotation(Set4SysProp.class);
			setSysPropName(mDeclaringHandler.getParentName().length() == 0 ? ann.name() : mDeclaringHandler.getParentName() + "." + ann.name());
		}
		*/
		
		// try to evaluate one by one
		setValueFromAnn(this, t);
    }

	/**
         * @param aw
         * @param t
         */
        private void setValueFromAnn(AttrWrapper aw, TreeSet<Set4Value> t)
        {
        	//boolean nullable = aw.isAnnotationPresent(Set4Nullable.class);
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
    					AttrWrapper attrValue = mDeclaringHandler.getRegistry().get(key);
    					if (attrValue != null && attrValue.notFake())
    					{
    						//AttrWrapper attrValue = mDeclaringHandler.getRegistry().get(key);
    						
    						//bind as observer
    						attrValue.addObserver(aw);
    						
    						Object testValue = attrValue.getValue();
    						// test conditions
    						boolean test = false;
    						if (testValue != null)
    						{
    							if (when[i].eq().length() > 0)
    							{
    								test = testValue.equals(when[i].eq());
    							} else
    							if (when[i].ne().length() > 0)
    							{
    								test = ! testValue.equals(when[i].eq());
    							} else
    							if (when[i].notNull().length() > 0)
    							{
    								test = Boolean.valueOf(when[i].notNull());
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
    						AttrWrapper keyAW = mDeclaringHandler.getRegistry().get(key);
    						if (keyAW == null)
    						{
    							keyAW = new AttrWrapper();
    							mDeclaringHandler.getRegistry().put(key, keyAW);
    						}
    						keyAW.addObserver(aw);
    						//return; // do not return, set default if any
    					}
    				}
    				/**/
    				//throw new Set4JException("Unimplemented!");
    				
    			}
    		}

    		if (strVal != null)	//set only if we have some value, keep otherwise 
    		{
    			strVal = mDeclaringHandler.substVars(strVal, null, aw);
    			try 
    			{
    				aw.setValue( mDeclaringHandler.substVars(strVal, null, null) );
    			} 
    			catch (Set4JException e)
    			{
    				// well this is actually bad, not sure about all possibilities, one is coming from TestBasics test.
    				aw.setValue( strVal ); // or bind?
    			}
    		} else
    		{
    			// if this is register and there is value already then fire value-change event
    			Object curValue = getValue();
    			if (curValue != null)
    			{
    				// let observers known
    			    setChanged();
    			    notifyObservers();
    			}
    		}
        }

	/**
     * @return
     */
    private boolean notFake()
    {
	    return !mFake;
    }

    public boolean isFake()
    {
	    return mFake;
    }

	/**
     * 
     */
    public void forbidOverride()
    {
    	mFixed = true;
    }

    public void enableOverride()
    {
        mFixed = false;
    }



}
