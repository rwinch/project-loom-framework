package org.java.scoped;

import java.util.Map;

/**
 * Representative of enterprise developer implementing business meaningful
 * processing code.
 */
public class ClientProcess implements Process {
	RequestAttributes requestAttributes = new RequestAttributes();

	public void executeProcess() {
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, String> requestValues = requestAttributes.getValues();
		System.out.println("The request value: " + requestValues.get("name"));
	}
}
