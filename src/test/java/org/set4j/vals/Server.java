package org.set4j.vals;

import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public interface Server
{
	@Set4Value(value="localhost")
	String server();
	
	@Set4Value(value="${runtime.port}")
	int port();
	

}
