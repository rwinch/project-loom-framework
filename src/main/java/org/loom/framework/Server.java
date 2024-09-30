package org.loom.framework;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandlers;
import com.sun.net.httpserver.HttpServer;

public class Server {
	private static final InetSocketAddress LOOPBACK_ADDR = new InetSocketAddress(InetAddress.getLoopbackAddress(),
			8080);

	public static void main(String[] args) throws Exception {

		HttpServer s = HttpServer.create(LOOPBACK_ADDR, 0);
		ScopedValueHandler handler = new ScopedValueHandler(
				List.of(new ClientProcess(), new UpstreamProcess(), new DownstreamProcess()),
				HttpHandlers.of(200, new Headers(), "Success"));
		s.createContext("/", handler);

		s.start();
	}
}