# pointy
Micro web framework with built-in HTTP server.

## What is pointy good for?
Making of micro-service, that should be server-less, often brings tons of dependencies, that are inadequately large and heavy. Pointy brings simple routing with built-in HTTP server.

## Example
The most simple web server, that listens on http://0.0.0.0:8080/hello-world

```java
package test.project;

import cz.tvrzna.pointy.router.PointyEndpoint;
import cz.tvrzna.pointy.server.PointyServer;

public class PointyExample
{
	public static void main(String[] args)
	{
		PointyServer server = new PointyServer("0.0.0.0", 8080, new PointyEndpoint()
		{
			@Override
			public void onInit()
			{
				ANY("/hello-world", (httpContext) -> {
					httpContext.send("Hello World!");
				});
			}
		});
		server.start();
	}
}

```