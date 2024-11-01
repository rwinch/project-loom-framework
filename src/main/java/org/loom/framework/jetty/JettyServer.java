package org.loom.framework.jetty;

import java.util.EnumSet;

import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.loom.framework.UserContextManagerFilter;
import org.loom.framework.WelcomeServlet;

public class JettyServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server();
		ServletContextHandler servletContextHandler = new ServletContextHandler();
		ServletHolder welcomeServletHolder = new ServletHolder(
				new WelcomeServlet());
		servletContextHandler.addFilter(UserContextManagerFilter.class, "/", EnumSet.of(DispatcherType.REQUEST));
		servletContextHandler.addServlet(welcomeServletHolder, "/");
		server.setHandler(servletContextHandler);
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });
		server.start();
		server.join();
	}

}
