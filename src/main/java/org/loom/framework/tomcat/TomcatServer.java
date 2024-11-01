package org.loom.framework.tomcat;

import java.io.File;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.loom.framework.UserContextManager;
import org.loom.framework.UserContextManagerFilter;
import org.loom.framework.WelcomeServlet;

public class TomcatServer {

	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir("temp");
		tomcat.getConnector();

		String contextPath = "";
		String docBase = new File(".").getAbsolutePath();

		Context context = tomcat.addContext(contextPath, docBase);

		FilterDef userContextManagerFilterDef = new FilterDef();
		userContextManagerFilterDef.setFilter(new UserContextManagerFilter());
		userContextManagerFilterDef.setFilterName(UserContextManagerFilter.class.getSimpleName());

		FilterMap userContextFilterMap = new FilterMap();
		userContextFilterMap.addURLPattern("/*");
		userContextFilterMap.setFilterName(UserContextManagerFilter.class.getSimpleName());

		context.addFilterDef(userContextManagerFilterDef);
		context.addFilterMap(userContextFilterMap);

		String servletName = "ScopedValues";
		String urlPattern = "/";

		tomcat.addServlet(contextPath, servletName, new WelcomeServlet());
		context.addServletMappingDecoded(urlPattern, servletName);

		tomcat.start();
		tomcat.getServer().await();
	}
}
