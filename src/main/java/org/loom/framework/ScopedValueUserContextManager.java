package org.loom.framework;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.ServletException;

public class ScopedValueUserContextManager implements UserContextManager {

	private static ScopedValue<AtomicReference<String>> USER_CONTEXT = ScopedValue.newInstance();

	@Override
	public void setUser(String user) {
		USER_CONTEXT.get().set(user);
	}

	@Override
	public String getUser() {
		return USER_CONTEXT.get().get();
	}

	@Override
	public void clearUser() {
		setUser(null);
	}

	public <V> V withContext(Callable<V> callable) throws Exception {
		return ScopedValue.where(USER_CONTEXT, new AtomicReference(null)).call(callable::call);
	}
}
