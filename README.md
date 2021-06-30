# pointy
[![javadoc](https://javadoc.io/badge2/cz.tvrzna/pointy/0.2.0/javadoc.svg)](https://javadoc.io/doc/cz.tvrzna/pointy/0.2.0)

Micro web framework with built-in HTTP server.

## What is pointy good for?
Making of micro-service, that should be server-less, often brings tons of dependencies, that are inadequately large and heavy. Pointy brings simple routing with built-in HTTP server.

## Installation
```xml
<dependency>
    <groupId>cz.tvrzna</groupId>
    <artifactId>pointy</artifactId>
    <version>0.2.0</version>
</dependency>
```

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