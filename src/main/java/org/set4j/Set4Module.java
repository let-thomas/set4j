package org.set4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks object as configuration submodule.<br/>
 * <b>Note</b>: submodules (its classes) cannot be inner classes unless already created before calling initialization!
 * <pre>
 * public class Main
 * {
 * 	&#64Set4Module
 * 	Node1 node1 = null;
 * 	&#64Set4Module
 * 	Node2 node2 = null;
 * 	NoNode somethingElse = null;
 * }
 * </pre>
 * @author Tomas Mikenda
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Set4Module
{

}
