package org.set4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Register property and override default name used from system properties. 
 * This is actually very similar to Set4Register but with SysProp name definition.
 * Why is not as parameter in {@link Set4Value} or {@link Set4Register}?
 * Because there could be more Set4Value(s) and do not confuse you using Set4Value and Set4Register when sysprop registration override is needed.
 * @author Tomas Mikenda
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Set4SysProp
{
	String name();
	boolean parentPrefix() default true;
}
