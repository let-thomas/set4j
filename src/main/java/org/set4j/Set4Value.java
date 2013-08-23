package org.set4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Tomas Mikenda
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Set4Value
{
	/**
	 * Either value directly or in ${xxx} evaluation. where xxx is some property name.<br/>
	 * value="xxx" and value=${'xxx'} are same .
	 * By default value is set to null. But if not value is found exception is thrown (with no value was found text). 
	 * If wish to enable null value set also {@link Set4Nullable} annotation.
	 * @return
	 */
	public static final String NULL = "Null-NULL-null";
	String value(); // default NULL;
	When[] when() default @When(what="");
	//boolean sysProp() default true;
	//String sysPropName() default ""; not like this, could be ambiguous
}
