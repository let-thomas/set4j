package org.set4j.vals;

import org.set4j.Set4Register;
import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public class RuntimeC
{
	int port = 123;
	
	@Set4Register
	public int getPort()
	{
		return port;
	}

}
