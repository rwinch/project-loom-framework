package org.loom.framework;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;


public class UserContextManagerFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		UserContextManager contextManager = UserContextManager.INSTANCE;
		String user = request.getParameter("user");
		try {
			contextManager.withContext(() -> {
				contextManager.setUser(user);
				chain.doFilter(request, response);
				return null;
			});
		}
		catch (RuntimeException | IOException | ServletException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
		finally {
			contextManager.clearUser();
		}
	}

}
