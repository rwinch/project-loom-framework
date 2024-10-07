package org.loom.framework;

import java.util.Map;

/**
 * Helper class that holds "security" values taken from a HTTP Requests and allows them to be accessed by other layers of the application. 
 * 
 * Meant to be somewhat similar to: org.springframework.web.context.request.RequestContextHolder
 */
public class SecurityAttributes {

	private static ScopedValue<Map<String, String>> SECURITY_ATTRIBUTES = ScopedValue.newInstance();
	
	public static ScopedValue<Map<String, String>> getSecurityAttributes(){
		return SECURITY_ATTRIBUTES;
	}
	
	public static String getValueByName(String valueName) {
		return SECURITY_ATTRIBUTES.get().get(valueName);
	}
	
	public static String updateValue(String valueName, String newValue) {
		return SECURITY_ATTRIBUTES.get().put(valueName, newValue);
	}
}
