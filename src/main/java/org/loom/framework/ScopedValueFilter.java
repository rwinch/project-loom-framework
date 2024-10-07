package org.loom.framework;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Filter class that extras values off the request and places them in a
 * {@link ScopedValue} that can be referenced by other classes, including client
 * code.
 */
public class ScopedValueFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Map<String, String[]> requestParameters = request.getParameterMap();
		Map<String, String> requestValues = new HashMap<>();
		System.out.println("Request Parameters");

		for (Entry<String, String[]> entry : requestParameters.entrySet()) {
			requestValues.put(entry.getKey(), entry.getValue()[0]);
			System.out.println("Request parameter name: " + entry.getKey());
			System.out.println("Value: " + Arrays.toString(entry.getValue()));
		}

		// Binds the ScopedValue to the thread for use down the filter chain, value is
		// destroyed on leaving the run block.
		// See for more details: https://openjdk.org/jeps/487#Description
		ScopedValue.<Map<String, String>>where(RequestAttributes.getRequestAttributes(), requestValues).run(() -> {
			try {
				chain.doFilter(request, response);
			} catch (IOException | ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

}
