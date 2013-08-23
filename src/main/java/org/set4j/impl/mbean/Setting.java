package org.set4j.impl.mbean;

import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public interface Setting
{
	@Set4Value(value = "321")
	int getInt();
	@Set4Value(value = "retezec")
	String getString();
}
