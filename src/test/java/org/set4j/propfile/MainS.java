package org.set4j.propfile;

import org.set4j.Set4Module;
import org.set4j.Set4PropFile;
import org.set4j.Set4Register;
import org.set4j.Set4Value;
import org.set4j.Set4Values;
import org.set4j.When;
import org.set4j.impl.LocationType;

/**
 * @author Tomas Mikenda
 *
 */
@Set4PropFile(type=LocationType.resource, location="org/set4j/propfile", files={"default.prop", "${customer}.prop", "${customer}-${env}.prop"})
public interface MainS
{
	@Set4Register
	Customer getCustomer(); 

	@Set4Register
	String getEnv();
	
	@Set4Module
	ModuleA getA();
	
	@Set4Values({
	   @Set4Value(value="myown", when=@When(what="env", eq="localhost")),
	   @Set4Value(value="test.localhost", when=@When(what="env", eq="test")),
	   @Set4Value(value="print.test.com", when=@When(what="env", eq="integration")),
	   @Set4Value(value="print.dot.com", when=@When(what="env", eq="prod"))
	})
	String getPrintingServer();
}
