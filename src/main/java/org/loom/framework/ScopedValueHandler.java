package org.loom.framework;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ScopedValueHandler implements HttpHandler {
	private List<Process> processes;
	private HttpHandler realHandler;

	public ScopedValueHandler(List<Process> processes, HttpHandler realHandler) {
		this.processes = processes;
		this.realHandler = realHandler;
	}

	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		RequestAttributes.getRequestAttributes();
		URI uri = exchange.getRequestURI();
		String query = uri.getQuery();
		Map<String, String> requestValues = Map.of("name", query);
		ScopedValue.<Map<String, String>>where(RequestAttributes.getRequestAttributes(), requestValues).run(() -> {
			Joiner<String, Void> joiner = Joiner.awaitAll();
			try (var scope = StructuredTaskScope.open(joiner)) {
				scope.fork(() -> {
					for (Process proc : processes) {
						proc.executeProcess();
					}
					
				});
				realHandler.handle(exchange);
				scope.join();
			} catch (Exception e){
				e.printStackTrace();
			}
		});

	}

}
