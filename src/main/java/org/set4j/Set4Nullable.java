package org.set4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Same like {@link Set4Register} but not required value i.e. can be null contrary to register which must be set.
 * @author Tomas Mikenda
 * see {@link Set4Register}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Set4Nullable
{

}
