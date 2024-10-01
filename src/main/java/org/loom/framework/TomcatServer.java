package org.loom.framework;

import java.io.File;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

public class TomcatServer {

	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir("temp");
		tomcat.getConnector();

		String contextPath = "";
		String docBase = new File(".").getAbsolutePath();

		Context context = tomcat.addContext(contextPath, docBase);

		FilterDef filterDef = new FilterDef();
		filterDef.setFilter(new ScopedValueFilter());
		filterDef.setFilterName("ScopedValueFilter");
		filterDef.setFilterClass(ScopedValueFilter.class.getSimpleName());
		filterDef.setDisplayName("Scoped Value Filter");
		
		FilterMap filterMap = new FilterMap();
		filterMap.addURLPattern("/*");
		filterMap.setFilterName(ScopedValueFilter.class.getSimpleName());
		context.addFilterDef(filterDef);
		context.addFilterMap(filterMap);
				
		String servletName = "ScopedValues";
		String urlPattern = "/";

		tomcat.addServlet(contextPath, servletName, new WelcomeServlet(List.of(new DatabaseUpdateService(), new DatabaseReadService())));
		context.addServletMappingDecoded(urlPattern, servletName);

		tomcat.start();
		tomcat.getServer().await();
	}
}
