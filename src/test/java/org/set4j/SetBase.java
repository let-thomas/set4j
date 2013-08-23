package org.set4j;

/**
 * @author Tomas Mikenda
 *
 */
public interface SetBase
{
	@Set4Value(value="value from base")
    String getBaseValue();

	@Set4Module
	ModuleBase module();
}
