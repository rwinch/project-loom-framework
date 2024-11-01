package org.loom.framework;

public class ThreadLocalUserContextManager implements UserContextManager {
	private static final ThreadLocal<String> userContext = new ThreadLocal();

	@Override
	public void setUser(String user) {
		userContext.set(user);
	}

	@Override
	public String getUser() {
		return userContext.get();
	}

	@Override
	public void clearUser() {
		userContext.remove();
	}
}
