package org.java.scoped;

import java.util.Map;

public class RequestAttributes {

	private static ScopedValue<Map<String, String>> REQUEST_ATTRIBUTES = ScopedValue.newInstance();
	
	public ScopedValue<Map<String, String>> getRequestAttributes(){
		return REQUEST_ATTRIBUTES;
	}
	
	public Map<String, String> getValues(){
		return REQUEST_ATTRIBUTES.get();
	}
	
}
