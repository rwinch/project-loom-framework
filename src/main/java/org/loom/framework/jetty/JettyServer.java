package org.loom.framework.jetty;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.loom.framework.DatabaseReadService;
import org.loom.framework.DatabaseUpdateService;
import org.loom.framework.ScopedValueFilter;
import org.loom.framework.SecurityScopedValueFilter;
import org.loom.framework.WelcomeServlet;

import jakarta.servlet.DispatcherType;

public class JettyServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server();
		ServletContextHandler servletContextHandler = new ServletContextHandler();
		ServletHolder welcomeServletHolder = new ServletHolder(
				new WelcomeServlet(List.of(new DatabaseUpdateService(), new DatabaseReadService())));
		servletContextHandler.addFilter(ScopedValueFilter.class, "/", EnumSet.of(DispatcherType.REQUEST));
		servletContextHandler.addFilter(SecurityScopedValueFilter.class, "/", EnumSet.of(DispatcherType.REQUEST));
		servletContextHandler.addServlet(welcomeServletHolder, "/");
		server.setHandler(servletContextHandler);
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });
		server.start();
		server.join();
	}

}
