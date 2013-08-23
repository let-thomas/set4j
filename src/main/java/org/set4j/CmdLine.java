/**
 * 
 */
package org.set4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author mikentom
 * @date 17.4.2006
 */
public class CmdLine extends Initializer
{
	private static String mPrgName = "prgName";


	public static void help(Object o)
	{
		help(mPrgName, o);
	}
	
	public static void help(String prgName, Object o)
	{
		//if (o != null) 	help(prgName, getFields(o.getClass())); //o.getClass().getFields()/* getDeclaredFields()*/);
		if (o == null)
		{
			throw new Error("Object is not set!");
		}
	
	/** Prints command line help using annotations. */
	//public static void help(String prgName, Field[] fields)
	//{
		Field aFieldArr[] = getFields(o.getClass()); 
		System.out.println("Usage: ");
		StringBuffer aCmdLine = new StringBuffer(16);
		StringBuffer aExplain = new StringBuffer(128);
		
		aCmdLine.append(prgName);
		aCmdLine.append(" ");
		for (Field aField : aFieldArr)
		{
			if (aField.isAnnotationPresent(Set4Args.class))
			{
				Set4Args aAnn = aField.getAnnotation(Set4Args.class);
				if (aAnn.hideCmd() && aAnn.hideHelp()) continue;
				int aCmdRBPos = -1;	// rollback position
				int aHlpRBPos = -1;	// rollback position
				
				if (aAnn.hideCmd())  aCmdRBPos = aCmdLine.length();
				if (aAnn.hideHelp()) aHlpRBPos = aExplain.length();
				
				if (! aAnn.required())
				{
					aCmdLine.append('[');
				}
				if (aAnn.order() > 0)
				{
					aCmdLine.append(aAnn.option());
					aExplain.append(aAnn.option());
				} else
				{
					aCmdLine.append('-').append(aAnn.option());
					aExplain.append(" -").append(aAnn.option());
				}
				
				if (aAnn.optAttr().length() > 0)
				{
					aCmdLine.append(' ').append(aAnn.optAttr());
					aExplain.append(' ').append(aAnn.optAttr());
				}
				if (! aAnn.required())
				{
					aCmdLine.append(']');
				}
				
				if (aCmdRBPos > 0) aCmdLine.setLength(aCmdRBPos);
				else aCmdLine.append(' ');
				
				if (aHlpRBPos > 0) aExplain.setLength(aHlpRBPos);
				else aExplain.append('\t').append(aAnn.help());				
				
				if (aAnn.defaultValueFieldName() != null && aAnn.defaultValueFieldName().length() > 0)
				{
					// todo !!! Fetch value !!!
					aExplain.append("; default is ");
					// find the field by the name
					for (Field valField : aFieldArr)
					{
						if (valField.getName().equals(aAnn.defaultValueFieldName()))
						{
							valField.setAccessible(true);
							try
							{
								aExplain.append(valField.get(o));
							} catch (Exception e)
							{
								e.printStackTrace();
								aExplain.append("--- error ---");
							}
							break;
						}
					}
				}
				
				// probably needs repetition of cond above
				aExplain.append("\n");
			}
		}
		System.out.println(aCmdLine.toString());
		System.out.println(aExplain.toString());
	}
	
	/** Load parameters from args[]. */
	//public static void loadParams(String[] args, CmdLineCaller objClass, Field[] fields)
	//public static void loadParams(String[] args, Field[] fields)
	public static void loadParams(String[] args, Object obj)
		throws RuntimeException
	{
		Field aFieldArr[] = getFields(obj.getClass());// getDeclaredFields(); 
		// aFieldArr = getClass().getFields();
		HashMap<String,Field> aOpts = new HashMap<String, Field>();
		
		// count num of required params
		int aReqCount = 0;
		for (Field aField : aFieldArr)
		{
			if (aField.isAnnotationPresent(Set4Args.class))
			{
				Set4Args aAnn = aField.getAnnotation(Set4Args.class);
				aReqCount += aAnn.required()? 1 : 0;
				if (aAnn.option() == null || aAnn.option().length() == 0 || aAnn.order() > 0)
				{
					if (aAnn.order() > 0) aOpts.put("&" + String.valueOf( aAnn.order()), aField);
				} else
				{
					aOpts.put(aAnn.option(), aField);
				}
			}
		}
		
		
		int argi = 0;
		int argOrderPos = 1;
		int aReqMatch = 0;
		boolean isSwitch = false;
		while (argi < args.length)
		{
			isSwitch = false;
			String arg = args[argi++];
			Object aValue = null;
			if (arg.charAt(0) == '-')
			{
				arg = arg.substring(1);
				isSwitch = true;
			}
			Field aField = aOpts.get(arg);
			if (aField == null && ! isSwitch)
			{
				// is there parameter with the current position?
				aField = aOpts.get("&" + String.valueOf( argOrderPos));
				if (aField != null) 
				{
					argOrderPos++;
					aValue = arg;
				}
			}
			if (aField == null)
			{
				System.out.println("Unknown parameter.");
				help(getPrgName(), obj);
				//System.exit(1);
				throw new ExceptionCmdLine("Unknown parameter", arg); // TODO element name
			}
			if (isSwitch) 
			{
				if (aField.getAnnotation(Set4Args.class).optAttr().equals("") )
				{
					aValue = null;
					arg = null;
				} else
				{
					aValue = args[argi++];
				}
			}
			if (aField.getAnnotation(Set4Args.class).required())
			{
				aReqMatch++;
			}
			
			
			try
			{
				// TODO use convertString2Type below !!!
				// set (and convert)
				//aValue = arg;
				Class<?> aFldCls = aField.getType();
				// is it boolean?
				if (String.class == aFldCls)
				{
					// not to do anything special
				} else
				if (Boolean.class.isAssignableFrom(aFldCls) || boolean.class.isAssignableFrom(aFldCls))
				{
					// is it only switch?
					if (arg == null)
					{
						aValue = new Boolean(true);
					} else
					{
						aValue = new Boolean((String)aValue);
					}
				} else
				if (Long.class.isAssignableFrom(aFldCls) || long.class.isAssignableFrom(aFldCls))
				{
					aValue = new Long((String)aValue);
				} else
				if (Integer.class.isAssignableFrom(aFldCls) || int.class.isAssignableFrom(aFldCls))
				{
					aValue = new Integer((String)aValue);
				} else
				{
					System.err.println("should set to type: " + aFldCls.getName());
				}
				
				// assign
				//objClass.storeCmdParam(aField, aValue);
				aField.setAccessible(true);
				aField.set(obj, aValue); // cause java.lang.IllegalAccessException
			} catch (Exception e)
			{
				e.printStackTrace();
				help(mPrgName, obj);
				System.exit(3);
			}
		}
		
		if (aReqMatch < aReqCount)
		{
			// print help
			help(mPrgName, obj);
			// finish
			System.exit(1);
		}
		/*
		for (int fldIdx = 0; fldIdx < aFieldArr.length; fldIdx++)
		{
			for (Field aField : aFieldArr)
			{
				if (aField.isAnnotationPresent(RawPosition.class))
				{
					RawPosition aRP = aField.getAnnotation(RawPosition.class);
					//aRP.
					if (aRP.pos() == fldIdx)
					{
						try
						{
							String aVal = aStrRaw.substring(startPos, startPos + aRP.len());
							aField.set(aRet, aRP.trim() ? aVal.trim() : aVal);
						} catch (IllegalArgumentException e)
						{
							e.printStackTrace();
						} catch (IllegalAccessException e)
						{
							e.printStackTrace();
						}
						startPos += aRP.len();
						break;
					}
				}/*
				if (aAnn instanceof RawPosition 
					&& ((RawPosition)aAnn).pos() == fldIdx)
				{
					
				}* /
			}
		}
		*/
		
	}

	/**
	 * Returns the mPrgName.
	 * @return Returns the mPrgName.
	 */
	public static String getPrgName()
	{
		return mPrgName;
	}

	/**
	 * Sets the mPrgName.
	 * @param prgName The mPrgName to set.
	 */
	public static void setPrgName(String prgName)
	{
		mPrgName = prgName;
	}
	public static Object convertString2Type(Field field, String strValue)
	{
		Object aValue = null;
		Class aFldCls = field.getType();
		if (String.class == aFldCls)
		{
			aValue = strValue;
		} else
		if (boolean.class.isAssignableFrom(aFldCls)) // is it boolean?
		{
			aValue = new Boolean(strValue);
		} else
		if (Long.class.isAssignableFrom(aFldCls))
		{
			aValue = new Long(strValue);
		} else
		if (Integer.class.isAssignableFrom(aFldCls))
		{
			aValue = new Integer(strValue);
		} else
		{
			throw new Error("Unhandled case! " + aFldCls.toString());
		}
		return aValue;
	}

	/* *
	 * Returns the mTraceSuperClass.
	 * @return Returns the mTraceSuperClass.
	 * /
	public static boolean isTraceSuperClass()
	{
		return mTraceSuperClass;
	}

	/* *
	 * Sets the mTraceSuperClass.
	 * @param traceSuperClass The mTraceSuperClass to set.
	 * /
	public static void setTraceSuperClass(boolean traceSuperClass)
	{
		mTraceSuperClass = traceSuperClass;
	}
	/**/

}
