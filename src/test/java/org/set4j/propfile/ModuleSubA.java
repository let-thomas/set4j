package org.set4j.propfile;

import org.set4j.Set4Register;
import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public interface ModuleSubA
{
	@Set4Value(value="100")
	int getUpdateFreq();
}
