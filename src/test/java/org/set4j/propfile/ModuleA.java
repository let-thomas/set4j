package org.set4j.propfile;

import org.set4j.Set4Module;
import org.set4j.Set4Register;
import org.set4j.Set4Value;
import org.set4j.Set4Values;
import org.set4j.When;

/**
 * @author Tomas Mikenda
 *
 */
public interface ModuleA
{
	@Set4Module
	ModuleSubA getSubA();

	@Set4Values({
		@Set4Value(value="true"),
		@Set4Value(value="false", when=@When(what="env", eq="prod"))
	})
	boolean isTestParam();
	
	@Set4Register
	boolean isEnabled();
	
	@Set4Register
	String viewName();
	
}
