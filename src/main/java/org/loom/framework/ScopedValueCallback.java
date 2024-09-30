package org.loom.framework;

import java.util.Map;

public class ScopedValueCallback  implements Callback{
	private static ScopedValue<Map<String,String>> requestAttributes = ScopedValue.newInstance();

	@Override
	public String getAttributeByName(String name) {
		// TODO Auto-generated method stub
		return requestAttributes.get().get(name);
	}

	@Override
	public void updateAttributeByName(String name, String value) {
		requestAttributes.get().put(name, value);
	}
}
