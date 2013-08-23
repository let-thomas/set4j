package org.set4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.set4j.impl.LocationType;


/**
 * @author Tomas Mikenda
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Set4PropFile
{
	LocationType type();
	String location();
	String[] files();
}
