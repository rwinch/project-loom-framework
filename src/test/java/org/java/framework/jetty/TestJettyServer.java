package org.java.framework.jetty;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.loom.framework.jetty.JettyServer;

public class TestJettyServer {
	private static final String REQUEST_URL = "http://localhost:8080/?fname=%s&lname=%s";
	private static final String RESPONSE_BODY = """
			<html>
				<title>Welcome!</title>
				<body>
					<h1>Welcome %s %s</h1>
					<h1>Have a Great Day!</h1>
				</body>
			</html>""";
	private static final HttpClient CLIENT = HttpClient.newHttpClient();

	@Test
	public void testTomcatServer() {
		if (!serverIsActive()) {
			startServer();
		}
		CompletableFuture<Void> call1 = callClient("Chris", "Jones");
		CompletableFuture<Void> call2 = callClient("Patrick", "Mahomes");
		CompletableFuture<Void> call3 = callClient("Travis", "Kelce");
		CompletableFuture<Void> call4 = callClient("Taylor", "Swift");
		CompletableFuture.allOf(call1, call2, call3, call4).join();

	}

	private void startServer() {
		Thread.ofVirtual().start(() -> {
			try {
				JettyServer.main(new String[] {});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).run();
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean serverIsActive() {
		try {
			CLIENT.send(HttpRequest.newBuilder(URI.create("http://localhost:8080/")).build(),
					BodyHandlers.discarding());
		} catch (IOException | InterruptedException e) {
			return false;
		}
		return true;
	}

	private CompletableFuture<Void> callClient(String fName, String lName) {
		HttpRequest request = HttpRequest.newBuilder(URI.create(String.format(REQUEST_URL, fName, lName))).build();

		var future = CLIENT.sendAsync(request, BodyHandlers.ofString()).thenAccept(
				response -> assertEquals(String.format(RESPONSE_BODY, fName, lName).strip(), response.body().strip()));

		return future;

	}
}
