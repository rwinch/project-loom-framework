package org.loom.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Representative of work callee client would typically perform, like reading
 * from a database table.
 */
public class DatabaseReadService implements Service {
	private static String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

	public DatabaseReadService() {
	}

	@Override
	public void doStuff() {
		try (Connection connection = DriverManager.getConnection(JDBC_URL);
				Statement statement = connection.createStatement();) {
			//***DON'T DO THIS IN A PRODUCTION SETTING, EXTREMELY VULNERABLE TO SQL INJECTION!!!!***
			String readQuery = String.format("select * from users WHERE ID = %s",
					RequestAttributes.getValueByName("currentId"));
			ResultSet resultSet = statement.executeQuery(readQuery);
			resultSet.next();
			String fname = resultSet.getString("fname");
			String lname = resultSet.getString("lname");
			RequestAttributes.updateValue("userFName", fname);
			RequestAttributes.updateValue("userLName", lname);

		} catch (Exception e) {
			e.printStackTrace();// Do something with the exception
		}
	}
}