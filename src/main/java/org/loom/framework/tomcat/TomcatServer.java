package org.loom.framework.tomcat;

import java.io.File;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.loom.framework.DatabaseReadService;
import org.loom.framework.DatabaseUpdateService;
import org.loom.framework.ScopedValueFilter;
import org.loom.framework.SecurityScopedValueFilter;
import org.loom.framework.WelcomeServlet;

public class TomcatServer {

	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir("temp");
		tomcat.getConnector();

		String contextPath = "";
		String docBase = new File(".").getAbsolutePath();

		Context context = tomcat.addContext(contextPath, docBase);

		FilterDef scopedValueFilter = new FilterDef();
		scopedValueFilter.setFilter(new ScopedValueFilter());
		scopedValueFilter.setFilterName(ScopedValueFilter.class.getSimpleName());
		
		FilterDef securityFilter = new FilterDef();
		securityFilter.setFilter(new SecurityScopedValueFilter());
		securityFilter.setFilterName(SecurityScopedValueFilter.class.getSimpleName());
				
		FilterMap scopeValueFilterMap = new FilterMap();
		scopeValueFilterMap.addURLPattern("/*");
		scopeValueFilterMap.setFilterName(ScopedValueFilter.class.getSimpleName());
		
		FilterMap securityFilterMap = new FilterMap();
		securityFilterMap.addURLPattern("/*");
		securityFilterMap.setFilterName(SecurityScopedValueFilter.class.getSimpleName());
		
		context.addFilterDef(scopedValueFilter);
		context.addFilterMap(scopeValueFilterMap);
		context.addFilterDef(securityFilter);
		context.addFilterMap(securityFilterMap);
				
		String servletName = "ScopedValues";
		String urlPattern = "/";

		tomcat.addServlet(contextPath, servletName, new WelcomeServlet(List.of(new DatabaseUpdateService(), new DatabaseReadService())));
		context.addServletMappingDecoded(urlPattern, servletName);

		tomcat.start();
		tomcat.getServer().await();
	}
}
