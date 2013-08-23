package org.set4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation only register given attribute as one for later use, typically in {@link When} conditions and must be set. Exception is thrown if null. 
 * @author Tomas Mikenda
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Set4Register
{
	//String sysProp() default "";
}
