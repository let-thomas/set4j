/**
 * 
 */
package org.set4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command line parameter option.
 * @author Tomas Mikenda
 * @date 14.4.2006
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Set4Args
{
	/** Command line option. Each option has '-' prefix which is automatically added.
	 * <br>I.E. if yours option have to be <code>-c</code> then set <code>option="c"</code>. 
	 * <br>If {@link order} is used then option is used as name description of switch.
	 */
	String option();
	/** Option attribute, ie. name description of parameter, or type. */
	String optAttr() default "";
	String help();
	boolean required() default false;
	/** Position of the parameter on the command line. First parameter has number 1; */
	int order() default -1;
	/** Will not be presented if short help is printed. */
	boolean hideCmd() default false;
	/** Will not be presented if long help is printed. */
	boolean hideHelp() default false;
	/** Field name with default value. Used e.g. in reconstruction of cmdLine. Field has to be public. */
	String defaultValueFieldName() default "";
}

