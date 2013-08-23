package org.set4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks class as Set4 class. This is not mandatory but useful in case of setting additional "tuning" options.<br/>
 * <b>fieldPrefix</b> specifies fixed prefix of class attributes, example
 * <pre>
 * &#64Set4Class(fieldPrefix="m")
 * class MyClass {
 * 	&#64Set4Property(value = "some value")
 * 	public String mField;
 * }
 * </pre>
 * Method names on the other hand <b>must</b> use usual convention, i.e. <b>get</b>Name() and <b>set</b>Name().
 * @author Tomas Mikenda
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Set4Class
{
	/**
	 * @return
	 */
	String fieldPrefix() default "";
	
	/**
	 * @return
	 */
	//String propertyFile() default ""; // TODO more files with cond/eval
	
	boolean loadFromPropFile() default false;
	
	/**
	 * Name of this variable in js scripts.
	 * Must be set when using js.
	 * @return
	 */
	String jsName() default "."; //using because when empty then fail in engine init (even when js not used) 
}
