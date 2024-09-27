package org.java.scoped;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ScopedValueHandler implements HttpHandler {
	RequestAttributes requestAttributes = new RequestAttributes();
	List<Process> processes;

	public ScopedValueHandler(List<Process> processes) {
		this.processes = processes;
	}

//	@Override
//	public void handle(HttpExchange exchange) throws IOException {
//		Joiner<Object, Void> joiner = Joiner.awaitAll();
//
//		try (var scope = StructuredTaskScope.open(joiner)) {
//			scope.fork(() -> handleRequest(exchange));
//			scope.join();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		
//		
//	}
//	private void handleRequest(HttpExchange exchange) {
//		URI uri = exchange.getRequestURI();
//		String query = uri.getQuery();
//	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		requestAttributes.getRequestAttributes();
		URI uri = exchange.getRequestURI();
		String query = uri.getQuery();
		Map<String, String> requestValues = Map.of("name", query);
		ScopedValue.<Map<String, String>>where(requestAttributes.getRequestAttributes(), requestValues).run(() -> {
			for (Process proc : processes) {
				proc.executeProcess();
			}
		});

	}

}
