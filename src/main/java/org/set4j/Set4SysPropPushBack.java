package org.set4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines that value should be pushed back to system.properties. 
 * Optionally using specified property name.
 * @author Tomas Mikenda
 * @since 1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Set4SysPropPushBack
{
	/**
	 * Optional parameter specifying to which system property push the value;
	 * If not used uses given one, i.e. by its name or defined by {@link Set4SysProp}.
	 * If specified may differ from default or set by Set4SysProp one.
	 * @return
	 */
	String name() default "";
}
