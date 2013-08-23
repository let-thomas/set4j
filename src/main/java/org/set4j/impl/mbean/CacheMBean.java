package org.set4j.impl.mbean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

/**
 * @author Tomas Mikenda
 *
 */
public class CacheMBean implements javax.management.DynamicMBean
{
	// internal variables describing the MBean
	private static final String thisClassName = CacheMBean.class.getName();
	
	private String mDescription = "Simple implementation of a dynamic MBean.";
	private Object mCacheObject = null;
	private Method mMethod = null;
	private String mStatus = "initial state";
	// internal variable
	private int mResets = 0;
	
	// class constructor
	public CacheMBean(Object singleton, String reloadMethodName, String desc) {
		mDescription = desc;
		mCacheObject = singleton;
		try
        {
	        mMethod = singleton.getClass().getMethod(reloadMethodName, new Class[] {});
	        mStatus = "init OK";
        } catch (Exception e)
        {
        	mStatus = "Error: " + e;
	        //e.printStackTrace();
        }
		
	    buildDynamicMBeanInfo();
	}


	// internal variables for describing MBean elements
	private MBeanAttributeInfo[] dAttributes = new MBeanAttributeInfo[2];
	private MBeanConstructorInfo[] mConstructors = null; //new MBeanConstructorInfo[1];
	private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
	private MBeanInfo dMBeanInfo = null;

	// internal method
	private void buildDynamicMBeanInfo() {

	    dAttributes[0] = new MBeanAttributeInfo(
	        "State",                 // name
	        "java.lang.String",      // type
	        "State: state string.",  // description
	        true,                    // readable
	        true,
	        false);                   // writable
	    dAttributes[1] = new MBeanAttributeInfo(
	        "resets",
	        "java.lang.Integer",
	        "Resets: count of resets.",
	        true,
	        false,
	        false);

	    MBeanParameterInfo[] params = null;
	    dOperations[0] = new MBeanOperationInfo(
	        "reload",                     // name
	        "Resets State and mResets attributes to their initial values",
	                                     // description
	        params,                      // parameter types
	        "void",                      // return type
	        MBeanOperationInfo.ACTION);  // impact

	    dMBeanInfo = new MBeanInfo(thisClassName,
	                               mDescription,
	                               dAttributes,
	                               mConstructors,
	                               dOperations,
	                               new MBeanNotificationInfo[0]);
	}

	// exposed method implementing the DynamicMBean.getMBeanInfo interface
	public MBeanInfo getMBeanInfo() {

	    // return the information we want to expose for management:
	    // the dMBeanInfo private field has been built at instantiation time,
	    return(dMBeanInfo);
	}	

	/* (non-Javadoc)
	 * @see org.set4j.impl.mbean.DynamicMBean#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String attribute_name) throws AttributeNotFoundException, MBeanException, ReflectionException
	{
		// Check attribute_name to avoid NullPointerException later on
	    if (attribute_name == null) {
	        throw new RuntimeOperationsException(
	            new IllegalArgumentException("Attribute name cannot be null"), 
	            "Cannot invoke a getter of " + thisClassName +
	                " with null attribute name");
	    }

	    // Call the corresponding getter for a recognized attribute_name
	    if (attribute_name.equals("State")) {
	        return getState();
	    } 
	    if (attribute_name.equals("resets")) {
	        return getResets();
	    }

	    // If attribute_name has not been recognized
	    throw(new AttributeNotFoundException(
	        "Cannot find " + attribute_name + " attribute in " + thisClassName));
	}
	
	// internal methods for getting attributes
	public String getState() {
	    return mStatus;
	}

	public Integer getResets() {
	    return new Integer(mResets);
	}

	/* (non-Javadoc)
	 * @see org.set4j.impl.mbean.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException,
	        MBeanException, ReflectionException
	{
		// Check attribute to avoid NullPointerException later on
	    if (attribute == null) {
	        throw new RuntimeOperationsException(
	            new IllegalArgumentException("Attribute cannot be null"),
	            "Cannot invoke a setter of " + thisClassName +
	                " with null attribute");
	    }
	    // Note: Attribute class constructor ensures the name not null
	    String name = attribute.getName();
	    Object value = attribute.getValue();

	    // Call the corresponding setter for a recognized attribute name
	    if (name.equals("State")) {
	        // if null value, try and see if the setter returns any exception
	        if (value == null) {
	            try {
	                setState( null );
	            } catch (Exception e) {
	                throw(new InvalidAttributeValueException(
	                    "Cannot set attribute "+ name +" to null")); 
	            }
	        }
	        // if non null value, make sure it is assignable to the attribute
	        else {
	            try {
	                if ((Class.forName("java.lang.String")).isAssignableFrom(
	                        value.getClass())) {
	                    setState((String) value);
	                }
	                else {
	                    throw(new InvalidAttributeValueException(
	                        "Cannot set attribute "+ name +
	                            " to a " + value.getClass().getName() +
	                            " object, String expected"));
	                }
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    // optional: recognize an attempt to set a read-only attribute
	    else if (name.equals("resets")) {
	        throw(new AttributeNotFoundException(
	            "Cannot set attribute "+ name +
	                " because it is read-only"));
	    }

	    // unrecognized attribute name
	    else {
	        throw(new AttributeNotFoundException(
	            "Attribute " + name + " not found in " +
	                this.getClass().getName()));
	    }
	}

	// internal method for setting attribute
	public void setState(String s) {
	    mStatus = s;
	    //nbChanges++;
	}

	/* (non-Javadoc)
	 * @see org.set4j.impl.mbean.DynamicMBean#getAttributes(java.lang.String[])
	 */
	@Override
	public AttributeList getAttributes(String[] attributeNames)
	{
	    // Check attributeNames to avoid NullPointerException later on
	    if (attributeNames == null) {
	        throw new RuntimeOperationsException(
	            new IllegalArgumentException(
	                "attributeNames[] cannot be null"),
	            "Cannot invoke a getter of " + thisClassName);
	    }
	    AttributeList resultList = new AttributeList();

	    // if attributeNames is empty, return an empty result list
	    if (attributeNames.length == 0)
	            return resultList;
	        
	    // build the result attribute list
	    for (int i=0 ; i<attributeNames.length ; i++){
	        try {        
	            Object value = getAttribute((String) attributeNames[i]);     
	            resultList.add(new Attribute(attributeNames[i],value));
	        } catch (Exception e) {
	            // print debug info but continue processing list
	            e.printStackTrace();
	        }
	    }
	    return(resultList);
	}

	/* (non-Javadoc)
	 * @see org.set4j.impl.mbean.DynamicMBean#setAttributes(javax.management.AttributeList)
	 */
	@Override
	public AttributeList setAttributes(AttributeList attributes)
	{
		// Check attributesto avoid NullPointerException later on
	    if (attributes == null) {
	        throw new RuntimeOperationsException(
	            new IllegalArgumentException(
	                "AttributeList attributes cannot be null"),
	            "Cannot invoke a setter of " + thisClassName);
	    }
	    AttributeList resultList = new AttributeList();

	    // if attributeNames is empty, nothing more to do
	    if (attributes.isEmpty())
	        return resultList;

	    // try to set each attribute and add to result list if successful
	    for (Iterator i = attributes.iterator(); i.hasNext();) {
	        Attribute attr = (Attribute) i.next();
	        try {
	            setAttribute(attr);
	            String name = attr.getName();
	            Object value = getAttribute(name); 
	            resultList.add(new Attribute(name,value));
	        } catch(Exception e) {
	            // print debug info but keep processing list
	            e.printStackTrace();
	        }
	    }
	    return(resultList);
	}

	/* (non-Javadoc)
	 * @see org.set4j.impl.mbean.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	@Override
	public Object invoke(String operationName, Object[] params, String[] signature) throws MBeanException,
	        ReflectionException
	{
	    // Check operationName to avoid NullPointerException later on
	    if (operationName == null) {
	        throw new RuntimeOperationsException(
	            new IllegalArgumentException(
	                "Operation name cannot be null"),
	            "Cannot invoke a null operation in " + thisClassName);
	    }

	    // Call the corresponding operation for a recognized name
	    if (operationName.equals("reload")){
	        // this code is specific to the internal "reset" method:
	        reload();     // no parameters to check
	        return null; // and no return value
	    } else { 
	        // unrecognized operation name:
	        throw new ReflectionException(
	            new NoSuchMethodException(operationName), 
	            "Cannot find the operation " + operationName +
	                " in " + thisClassName);
	    }
	}


	// internal method for implementing the reset operation
	public void reload() {
	    mResets++;
	    try
        {
	        mMethod.invoke(mCacheObject, (Object[])null);
	        mStatus = "reload: OK";
        } catch (Exception e)
        {
	        //e.printStackTrace();
        	mStatus = "Invocation Error: " + e;
        }
	}

}
