package org.loom.framework;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Filter class that extracts "security" values off the request and places them in a
 * {@link ScopedValue} that can be referenced by other classes, including client
 * code. Primarily to demonstrate that multiple ScopedValues can be added a referenced. 
 */
public class SecurityScopedValueFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String requestID = ((HttpServletRequest) request).getRequestId();
		Map<String, String> securityValues = new HashMap<>();
		securityValues.put("requestID", requestID);

		// Binds an instance of the ScopedValue to the thread for use down the filter
		// chain, instance is destroyed on leaving the run block.
		// See for more details: https://openjdk.org/jeps/487#Description
		ScopedValue.<Map<String, String>>where(SecurityAttributes.getSecurityAttributes(), securityValues).run(() -> {
			try {
				chain.doFilter(request, response);
			} catch (IOException | ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

}
