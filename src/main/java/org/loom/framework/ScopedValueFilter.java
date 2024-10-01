package org.loom.framework;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class ScopedValueFilter implements Filter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Map<String, String[]> requestParameters =  request.getParameterMap();
		Map<String,String> requestValues = new HashMap<>();
		System.out.println("Request Parameters");

		for(Entry<String, String[]> entry : requestParameters.entrySet()) {
			requestValues.put(entry.getKey(), entry.getValue()[0]);
			System.out.println("Request parameter name: "+ entry.getKey());
			System.out.println("Value: " + Arrays.toString(entry.getValue()));
		}
		
		ScopedValue.<Map<String, String>>where(RequestAttributes.getRequestAttributes(), requestValues).run(() -> {
			Joiner<String, Void> joiner = Joiner.awaitAllSuccessfulOrThrow();
			try (var scope = StructuredTaskScope.open(joiner)) {
				scope.fork(() -> {
					try {
						chain.doFilter(request, response);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ServletException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				scope.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
