package org.set4j.vals;

import org.set4j.Set4Module;
import org.set4j.Set4PropFile;
import org.set4j.Set4Value;
import org.set4j.impl.LocationType;

/**
 * @author Tomas Mikenda
 *
 */
@Set4PropFile(type= LocationType.resource, location="org/set4j/vals", files={"${run}.prop"})
public interface Setting
{
	@Set4Value(value="http://${server.server}:${server.port}")
	String url();

	@Set4Module
	Server server();
	
	@Set4Module
	RuntimeC runtime();
	
	@Set4Value(value="run1")
	String run();
}
