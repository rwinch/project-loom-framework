package org.loom.framework;

import java.util.Map;

public class DownstreamProcess implements Process {

	@Override
	public void executeProcess() {		
		System.out.println("The request value in downstream process: " + RequestAttributes.getValueByName("name"));
	}

}
