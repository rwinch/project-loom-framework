package org.loom.framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

/**
 * Representative of work callee client would typically perform, like
 * updating a database table.
 */
public class DatabaseUpdateService implements Service {
	private static String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	private int id = 0;
	public DatabaseUpdateService() {
		initDatabase();
	}

	private void initDatabase() {
		try (Connection connection = DriverManager.getConnection(JDBC_URL);) {
			Statement statement = connection.createStatement();

			statement.execute("Create table users (ID int primary key, fname varchar(50), lname varchar(50))");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method actually doing work. Here using a {@link StructuredTaskScope} to
	 * handle the update. Not a great use of StructuredTaskScope as only a single
	 * task is being executed, but demonstrates the ability to reference a
	 * {@link ScopedValue} when in a forked {@link Subtask} created by a StructuredTaskScope.
	 */
	@Override
	public void doStuff() {
		Joiner<String, Void> joiner = Joiner.awaitAllSuccessfulOrThrow();
		try (var scope = StructuredTaskScope.open(joiner);
				Connection connection = DriverManager.getConnection(JDBC_URL);) {
			scope.fork(() -> {
				int currentId = getCurrentId();
				//***DON'T DO THIS IN A PRODUCTION SETTING, EXTREMELY VULNERABLE TO SQL INJECTION!!!!***
				String updateQuery = String.format("Insert into users (ID, fname, lname) values (%d, '%s', '%s')",
						currentId, RequestAttributes.getValueByName("fname"), RequestAttributes.getValueByName("lname"));
				
				RequestAttributes.updateValue("currentId", Integer.toString(currentId));
				System.out.println("RequestID: " + SecurityAttributes.getValueByName("requestID"));
				try (Statement statement = connection.createStatement()) {
					int rowsUpdated = statement.executeUpdate(updateQuery);
					System.out.println("rows updated: " + rowsUpdated);
				} catch (Exception e) {
					e.printStackTrace();//Do something with the exception
				}
			});
			scope.join();
		} catch (Exception e) {
			e.printStackTrace();//Do something with the exception
		}

	}

	private int getCurrentId() {
		return id++;
	}

}
