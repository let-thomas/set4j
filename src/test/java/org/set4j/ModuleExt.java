package org.set4j;

/**
 * @author Tomas Mikenda
 *
 */
public interface ModuleExt extends ModuleBase
{
	@Set4Value(value="ext value")
	String val();
	
	@Set4Value(value="new value")
	String newVal();
}
