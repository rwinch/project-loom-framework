package org.loom.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

/**
 * Representative of work a end stream client would typically perform, like
 * reading from a database table.
 */
public class DatabaseReadService implements Service {
	private static String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

	public DatabaseReadService() {
	}

	/**
	 * Method actually doing work. Here using a {@link StructuredTaskScope} to
	 * handle the update. Not a great use of StructuredTaskScope as only a single
	 * task is being executed, but demonstrates the ability to reference a
	 * ScopedValue when in a child thread created by StructuredTaskScope.
	 */
	@Override
	public void doStuff() {
		Joiner<String, Void> joiner = Joiner.awaitAllSuccessfulOrThrow();
		try (var scope = StructuredTaskScope.open(joiner);
				Connection connection = DriverManager.getConnection(JDBC_URL);) {
			scope.fork(() -> {
				String readQuery = String.format("select * from users WHERE ID = %s",  RequestAttributes.getValueByName("currentId"));
				try (Statement statement = connection.createStatement()) {
					ResultSet resultSet = statement.executeQuery(readQuery);
					resultSet.next();
					String fname = resultSet.getString("fname");
					String lname = resultSet.getString("lname");
					RequestAttributes.updateValue("userFName", fname);
					RequestAttributes.updateValue("userLName", lname);
				} catch (Exception e) {
					e.printStackTrace();// Do something with the exception
				}
			});
			scope.join();
		} catch (Exception e) {
			e.printStackTrace();// Do something with the exception
		}
	}
}