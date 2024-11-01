package org.loom.framework;

import java.util.concurrent.Callable;

public interface UserContextManager {

	void setUser(String user);

	String getUser();

	void clearUser();

	default <V> V withContext(Callable<V> callable) throws Exception {
		return callable.call();
	}

	UserContextManager INSTANCE = new ScopedValueUserContextManager();

}
