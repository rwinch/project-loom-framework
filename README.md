# Using Scoped Value in a Framework

This project is used to demonstrate using Scoped Values within the context of a framework. Particualrly a web framework, though hopefully it should provide a clear guide for non-web framework contexts. 

## Scoped Values

[Scoped Values](https://openjdk.org/jeps/8338456) are preview feature (as of JDK 24) are to share immuratable data within a thread and child threads. Scoped Values are intended to address some of hte shortcomings with ThreadLocal, but are not a "drop-in" replacement. As their name suggests, Scoped Values is available within a specific scope, whereas a ThreadLocal exists for the lifetime of a thread. 

Scoped Values are especially meant to be used with [Virtual Threads](https://openjdk.org/jeps/444) and Strucutred Concurrency. The former because it's possible there could be many thousands of a virtual threads existing concurrently within a JVM, and using Scoped Values where possible could reduce memory overhead, over using ThreadLocal. For the latter, Structured Concurrency, Structured Concurrency provides runtime guarantees on lifetime of threads. 

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

Here, imaging the commented sleep code is not there, it seems the child virtual thread *should* have access to the Scoped Value Name. Running the above code as-is would result in a `java.util.NoSuchElementException`.

Uncommenting the `Thread.sleep()`, reveals the reason for this behavior. The parent thread where the Scoped Value was available would have already left that scope by the time the child thread would had attempted to reference `NAME`. Outside of Structured Concurrency, which requires a `.join()`, to be called, there are no runtime guarantees to ensure that a Scoped Value would sitll be in scope. 