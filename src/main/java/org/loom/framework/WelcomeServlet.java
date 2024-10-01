package org.loom.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WelcomeServlet extends HttpServlet {

	private List<Service> services; 
	
	
	public WelcomeServlet(List<Service> services) {
		super();
		this.services = services;
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		services.stream().forEach(Service::doStuff);
		
		PrintWriter writer = resp.getWriter();

		writer.println(String.format("""
				<html>
					<title>Welcome!</title>
					<body>
						<h1>Welcome %s %s</h1>
						<h1>Have a Great Day!</h1>
					</body>
				</html>
				""", RequestAttributes.getValueByName("userFName"),  RequestAttributes.getValueByName("userLName")));
	}
}
