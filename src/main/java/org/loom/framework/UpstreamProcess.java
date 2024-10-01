package org.loom.framework;

import java.time.LocalDateTime;

public class UpstreamProcess implements Service {

	@Override
	public void doStuff() {		
		System.out.println("Updating name value with local date");
		RequestAttributes.updateValue("name", RequestAttributes.getValueByName("name") + LocalDateTime.now().toString());
	}

}
