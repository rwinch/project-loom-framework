# Using Project Loom in a Framework

This project is demonstrating how to use [Project Loom](https://openjdk.org/projects/loom/) features; Virtual Threads, Structured Concurrency, and Scoped Values, within a framework context. Though this project primarily focuses on Scoped Values, as they are the most relevant in the framework context. 

For simple examples of using Structured Concurrency and Scoped Values, be sure to check out the [Loominated Java repo](https://github.com/wkorando/loominated-java). 

## Requirements

The project requires the JDK 24 Loom EA build available here:  https://jdk.java.net/loom/

You will also need to have maven installed to build the project. 

### Build and Running the Project

To build the project:

```
mvn clean package
```

To run the project as a Tomcat WebApp:

```
java --enable-preview -cp target/loom-framework-0.0.1-SNAPSHOT-jar-with-dependencies.jar org.loom.framework.tomcat.TomcatServer
```

To run the project as a Jetty WebApp:

```
java --enable-preview -cp target/loom-framework-0.0.1-SNAPSHOT-jar-with-dependencies.jar org.loom.framework.jetty.JettyServer
```

This startups a simple web server running on port `http://localhost:8080/?fname=[first-name]&lname=[last-name]`

Currently project is just pulling off the query value and sys outing it with a short delay. 

## About the Project

The project is a simple web application, that can be run as either a Tomcat or Jetty application.  

* [org.loom.framework.RequestAttribute](src/main/java/org/loom/framework/RequestAttributes.java) - Helper class that allows for easy referencing of `ScopedValue` instance.
   * [org.loom.framework.RequestAttribute](src/main/java/org/loom/framework/FilterAttributes.java) - Like `RequestAttributes` but for "security" values. Primarily to demonstrate multiple `ScopedValues` can be used and referenced. 
* [org.loom.framework.ScopedValueFilter](src/main/java/org/loom/framework/ScopedValueFilter.java) - `Filter` class that retrieves values off a request, in this case the query values of `fname` and `lanme` and adds them to the `ScopedValue` that is held by `RequestAttributes`.
   * [org.loom.framework.ScopedValueFilter](src/main/java/org/loom/framework/SecurtiyScopedValueFilter.java) - Essentially the same as `RequestAttributes`, but adds "security" values (in this case simply request id) to a ScopedValue. 
* [org.loom.framework.Service](src/main/java/org/loom/framework/Service.java) - Representative of arbitrary client callee code that is performing business meaningful work. Used to demonstrate `ScopedValues` can be referenced by callee code. 
   * [org.loom.framework.Service](src/main/java/org/loom/framework/DatabaseUpdatedService.java) - Implementation of `Service` that writes to a database (in-memory H2). It (unnecessarily) uses a `StructuredTaskScope` to perform this work. This is to demonstrate that the child threads that are spawned when `StructuredTaskScope` forks a `Subtask` have access to `ScopedValue` (this is further covered under [Scoped Values](#scoped-values))
* [org.loom.framework.WelcomeServlet](src/main/java/org/loom/framework/WelcomeServlet.java) - Simple Servlet implementation that prints out the results of the request. 


## Providing Feedback

The goal of this project is to encourage feedback on Project Loom features. To that end, feedback should be directed to the Project Loom devlist found here: https://mail.openjdk.org/mailman/listinfo/loom-dev

For feedback about this project itself, create and issue or submit a PR. 

## Virtual Threads

The central feature of Project Loom, virtual threads separate the concept of threads into two distinct parts. The Platform Thread, which is functionally similar to legacy Threads in, which have a one-to-one relationship to OS threads. And Virtual Threads, which exist in memory and run on top of platform threads. For a high-level overview of virtual threads, see this video: [https://www.youtube.com/watch?v=bOnIYy3Y5OA](https://www.youtube.com/watch?v=bOnIYy3Y5OA). For a more in-depth explanation on virtual threads be sure to read the [JEP 444](https://openjdk.org/jeps/444). 

## Structured Concurrency

Structured Concurrency, currently in preview as of JDK 24, is designed to allow developers to break a unit of work into multiple tasks that can be executed simultaneously. Structured concurrency introduces a new programming model to Java greatly simplifying the writing (and reading) of concurrent blocks of code, as well as error handling and debugging. Both of which will be covered in this project. 

## Scoped Values

[Scoped Values](https://openjdk.org/jeps/8338456) are preview feature (as of JDK 24) are to share immutable data within a thread and child threads. Scoped Values are intended to address some of the shortcomings with ThreadLocal, but are not a "drop-in" replacement. As their name suggests, a scoped value is available within a specific scope, whereas a thread local exists for the lifetime of a thread. 

Scoped Values are especially meant to be used with [Virtual Threads](https://openjdk.org/jeps/444) and Structured Concurrency. The former because it's possible there could be many thousands of a virtual threads existing concurrently within a JVM, and using Scoped Values where possible could reduce memory overhead, over using ThreadLocal. For the latter, Structured Concurrency, Structured Concurrency provides runtime guarantees on lifetime of threads. 

The issue with attempting to use Scoped Values in children threads outside the context of Strucutred Concurrency can be demonstrated with the below example: 

```java
public class ScopeValueExample {
	private static final ScopedValue<String> NAME = ScopedValue.newInstance();
	public static void main(String[] args) throws InterruptedException {
		ScopedValue.where(NAME, "duke").run(() -> {
			// This works
			System.out.println(Thread.currentThread().getName() + ": " + NAME.get());
			try {
				Thread.ofVirtual().start(() -> {
//					try {
//						Thread.sleep(1000L);
//
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
						// This fails
						System.out.println(Thread.currentThread().getName() + ": " + NAME.get());
					}).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Thread.ofPlatform().start(() -> {
				// This fails
				System.out.println(Thread.currentThread().getName() + ": " + NAME.get());
			});
		});
		System.out.println("Outside ScopedValue scope");

		Thread.currentThread().join();
		
	}
}
```

Here, imagine the commented sleep code is not there, it seems the child virtual thread *should* have access to the `ScopedValue` `NAME`. Running the above code as-is would result in a `java.util.NoSuchElementException`.

Uncommenting the `Thread.sleep()`, reveals the reason for this behavior. The parent thread where the `ScopedValue` was available would have already left that scope by the time the child thread would had attempted to reference `NAME`. Outside of Structured Concurrency, which requires a `.join()`, to be called, there are no runtime guarantees to ensure that a `ScopedValue` would still be in scope. 

### ScopedValue vs ThreadLocal

Scoped Values and Thread Locals exist in the same domain space; indirect passing of data to callees. However Scoped Values are not a "drop in" replacement for Thread Locals. As the names imply, Scoped Values exist within a specific scope of an application, while a ThreadLocal is attached to a specific thread.  

## Notice

This is a project meant to provide trivial examples of using Project Loom within the context of a framework. It has not be exhaustively reviewed for potential vulnerabilities. The code of this project wasn't written with the intent that it should be copy and pasted into production code and pushed to a live service, without it first going through security review to ensure code meets an organization's security and data privacy requirements. 