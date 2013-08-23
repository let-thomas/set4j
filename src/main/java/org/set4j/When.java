/**
 * 
 */
package org.set4j;

/**
 * @author Tomas Mikenda
 *
 */
public @interface When
{
	/** */
	String what();
	String eq() default "";
	String ne() default "";
	String notNull() default "";
	
}
