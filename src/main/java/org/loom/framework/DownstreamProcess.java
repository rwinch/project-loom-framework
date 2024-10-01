package org.loom.framework;

import java.util.Map;

public class DownstreamProcess implements Service {

	@Override
	public void doStuff() {		
		System.out.println("The request value in downstream process: " + RequestAttributes.getValueByName("name"));
	}

}
