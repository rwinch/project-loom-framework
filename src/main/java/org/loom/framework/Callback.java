package org.loom.framework;

public interface Callback {

	String getAttributeByName(String name);
	
	void updateAttributeByName(String name, String value);
}
