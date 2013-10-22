package org.set4j.propfile.inheritance;

import org.set4j.Set4PropFile;
import org.set4j.impl.LocationType;
import org.set4j.propfile.MainS;

/**
 * @author Tomas Mikenda
 *
 */
@Set4PropFile(type= LocationType.resource, location="org/set4j/propfile/child", files={"default.prop", "${customer}.prop", "${customer}-${env}.prop"})
public interface MainChildWithPF extends MainS
{
	
}
