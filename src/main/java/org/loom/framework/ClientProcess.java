package org.loom.framework;

/**
 * Representative of enterprise developer implementing business meaningful
 * processing code.
 * 
 * The purpose of the 5 second delay is to demonstrate that even as other
 * requests come through, the value that is printed by sout is associated with
 * the original request and not the latest value.
 */
public class ClientProcess implements Process {

	public void executeProcess() {
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("The request value: " + RequestAttributes.getValueByName("name"));
	}
}
