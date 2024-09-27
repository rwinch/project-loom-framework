package org.loom.framework;

import java.util.Map;

/**
 * Helper class that holds values taken from a HTTP Requests and allows them to be accessed by other layers of the application. 
 * 
 * Meant to be somewhat similar to: org.springframework.web.context.request.RequestContextHolder
 */
public class RequestAttributes {

	private static ScopedValue<Map<String, String>> REQUEST_ATTRIBUTES = ScopedValue.newInstance();
	
	public static ScopedValue<Map<String, String>> getRequestAttributes(){
		return REQUEST_ATTRIBUTES;
	}
	
	public static Map<String, String> getValues(){
		return REQUEST_ATTRIBUTES.get();
	}
	
}
