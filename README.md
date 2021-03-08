# ktor-sample3

ktor-sample3 is your new project powered by [Ktor](http://ktor.io) framework.

<img src="https://repository-images.githubusercontent.com/40136600/f3f5fd00-c59e-11e9-8284-cb297d193133" alt="Ktor" width="100" style="max-width:20%;">

Company website: example.com Ktor Version: 1.5.2 Kotlin Version: 1.4.10
BuildSystem: [Gradle with Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

# Ktor Documentation

Ktor is a framework for quickly creating web applications in Kotlin with minimal effort.

* Ktor project's [Github](https://github.com/ktorio/ktor/blob/master/README.md)
* Getting started with [Gradle](http://ktor.io/quickstart/gradle.html)
* Getting started with [Maven](http://ktor.io/quickstart/maven.html)
* Getting started with [IDEA](http://ktor.io/quickstart/intellij-idea.html)

Selected Features:

* [Routing](#routing-documentation-jetbrainshttpswwwjetbrainscom)
* [Micrometer Metrics](#micrometer-metrics-documentation-jetbrainshttpswwwjetbrainscom)
* [WebSockets](#websockets-documentation-jetbrainshttpswwwjetbrainscom)

## Routing Documentation ([JetBrains](https://www.jetbrains.com))

Allows to define structured routes and associated handlers.

### Description

Routing is a feature that is installed into an Application to simplify and structure page request handling. This page
explains the routing feature. Extracting information about a request, and generating valid responses inside a route, is
described on the requests and responses pages.

```application.install(Routing) {
    get("/") {
        call.respondText("Hello, World!")
    }
    get("/bye") {
        call.respondText("Good bye, World!")
    }

```

`get`, `post`, `put`, `delete`, `head` and `options` functions are convenience shortcuts to a flexible and powerful
routing system. In particular, get is an alias to `route(HttpMethod.Get, path) { handle(body) }`, where body is a lambda
passed to the get function.

### Usage

## Routing Tree

Routing is organized in a tree with a recursive matching system that is capable of handling quite complex rules for
request processing. The Tree is built with nodes and selectors. The Node contains handlers and interceptors, and the
selector is attached to an arc which connects another node. If selector matches current routing evaluation context, the
algorithm goes down to the node associated with that selector.

Routing is built using a DSL in a nested manner:

```kotlin
route("a") { // matches first segment with the value "a"
  route("b") { // matches second segment with the value "b"
     get {…} // matches GET verb, and installs a handler
     post {…} // matches POST verb, and installs a handler
  }
}
```

```kotlin
method(HttpMethod.Get) { // matches GET verb
   route("a") { // matches first segment with the value "a"
      route("b") { // matches second segment with the value "b"
         handle { … } // installs handler
      }
   }
}
```kotlin
route resolution algorithms go through nodes recursively discarding subtrees where selector didn't match.

Builder functions:
* `route(path)` – adds path segments matcher(s), see below about paths
* `method(verb)` – adds HTTP method matcher.
* `param(name, value)` – adds matcher for a specific value of the query parameter
* `param(name)` – adds matcher that checks for the existence of a query parameter and captures its value
* `optionalParam(name)` – adds matcher that captures the value of a query parameter if it exists
* `header(name, value)` – adds matcher that for a specific value of HTTP header, see below about quality

## Path
Building routing tree by hand would be very inconvenient. Thus there is `route` function that covers most of the use cases in a simple way, using path.

`route` function (and respective HTTP verb aliases) receives a `path` as a parameter which is processed to build routing tree. First, it is split into path segments by the `/` delimiter. Each segment generates a nested routing node.

These two variants are equivalent:

```kotlin
route("/foo/bar") { … } // (1)

route("/foo") {
   route("bar") { … } // (2)
}
```

### Parameters

Path can also contain parameters that match specific path segment and capture its value into `parameters` properties of
an application call:

```kotlin
get("/user/{login}") {
   val login = call.parameters["login"]
}
```

When user agent requests `/user/john` using `GET` method, this route is matched and `parameters` property will
have `"login"` key with value `"john"`.

### Optional, Wildcard, Tailcard

Parameters and path segments can be optional or capture entire remainder of URI.

* `{param?}` –- optional path segment, if it exists it's captured in the parameter
* `*` –- wildcard, any segment will match, but shouldn't be missing
* `{...}` –- tailcard, matches all the rest of the URI, should be last. Can be empty.
* `{param...}` –- captured tailcard, matches all the rest of the URI and puts multiple values for each path segment
  into `parameters` using `param` as key. Use `call.parameters.getAll("param")` to get all values.

Examples:

```kotlin
get("/user/{login}/{fullname?}") { … }
get("/resources/{path...}") { … }
```

## Quality

It is not unlikely that several routes can match to the same HTTP request.

One example is matching on the `Accept` HTTP header which can have multiple values with specified priority (quality).

```kotlin
accept(ContentType.Text.Plain) { … }
accept(ContentType.Text.Html) { … }
```

The routing matching algorithm not only checks if a particular HTTP request matches a specific path in a routing tree,
but it also calculates the quality of the match and selects the routing node with the best quality. Given the routes
above, which match on the Accept header, and given the request header `Accept: text/plain; q=0.5, text/html` will
match `text/html` because the quality factor in the HTTP header indicates a lower quality fortext/plain (default is 1.0)
.

The Header `Accept: text/plain, text/*` will match `text/plain`. Wildcard matches are considered less specific than
direct matches. Therefore the routing matching algorithm will consider them to have a lower quality.

Another example is making short URLs to named entities, e.g. users, and still being able to prefer specific pages
like `"settings"`. An example would be

* `https://twitter.com/kotlin` -– displays user `"kotlin"`
* `https://twitter.com/settings` -- displays settings page

This can be implemented like this:

```kotlin
get("/{user}") { … }
get("/settings") { … }
```

The parameter is considered to have a lower quality than a constant string, so that even if `/settings` matches both,
the second route will be selected.

### Options

No options()

## Micrometer Metrics Documentation ([JetBrains](https://www.jetbrains.com))

Enables Micrometer metrics in your Ktor server application.

### Description

The [MicrometerMetrics](https://api.ktor.io/%ktor_version%/io.ktor.metrics.micrometer/-micrometer-metrics/index.html)
feature enables [Micrometer](https://micrometer.io/docs) metrics in your Ktor server application and allows you to
choose the required monitoring system, such as Prometheus, JMX, Elastic, and so on. By default, Ktor exposes metrics for
monitoring HTTP requests and a set of low-level metrics for [monitoring the JVM][micrometer_jvm_metrics]. You can
customize these metrics or create new ones.

### Usage

### Install MicrometerMetrics

<var name="feature_name" value="MicrometerMetrics"/>
<include src="lib.md" include-id="install_feature"/>

#### Exposed Metrics

Ktor exposes the following metrics for monitoring HTTP requests:

* `ktor.http.server.requests.active`: a [gauge](https://micrometer.io/docs/concepts#_gauges) that counts the amount of
  concurrent HTTP requests. This metric doesn't provide any tags.
* `ktor.http.server.requests`: a [timer](https://micrometer.io/docs/concepts#_timers) for measuring the time of each
  request. This metric provides a set of tags for monitoring request data, including `address` for a requested
  URL, `method` for an HTTP method, `route` for a Ktor route handling requests, and so on.

> The metric names may be [different](https://micrometer.io/docs/concepts#_naming_meters) depending on the configured monitoring system.

In addition to HTTP metrics, Ktor exposes a set of metrics for [monitoring the JVM](#jvm_metrics).

### Create a Registry

After installing `MicrometerMetrics`, you need to create
a [registry for your monitoring system](https://micrometer.io/docs/concepts#_registry) and assign it to
the [registry](https://api.ktor.io/%ktor_version%/io.ktor.metrics.micrometer/-micrometer-metrics/-configuration/registry.html)
property. In the example below, the `PrometheusMeterRegistry` is created outside the `install` block to have the
capability to reuse this registry in different [route handlers](Routing_in_Ktor.md):

```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }
}
```

### Prometheus: Expose a Scrape Endpoint

If you use Prometheus as a monitoring system, you need to expose an HTTP endpoint to the Prometheus scraper. In Ktor,
you can do this in the following way:

1. Create a dedicated [route](Routing_in_Ktor.md) that accepts GET requests by the required address (`/metrics` in the
   example below).
1. Use `call.respond` to send scraping data to Prometheus.

```kotlin
fun Application.module() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
        // ...
    }

    routing {
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
```

### Options

The `MicrometerMetrics` feature provides various configuration options that can be accessed
using [MicrometerMetrics.Configuration](https://api.ktor.io/%ktor_version%/io.ktor.metrics.micrometer/-micrometer-metrics/-configuration/index.html)
.

### Timers

To customize tags for each timer, you can use the `timers` function that is called for each request:

```kotlin
install(MicrometerMetrics) {
    // ...
    timers { call, exception ->
        tag("region", call.request.headers["regionId"])
    }
}
```

### Distribution Statistics

You configure [distribution statistics](https://micrometer.io/docs/concepts#_configuring_distribution_statistics) using
the `distributionStatisticConfig` property, for example:

```kotlin
install(MicrometerMetrics) {
    // ...
    distributionStatisticConfig = DistributionStatisticConfig.Builder()
                .percentilesHistogram(true)
                .maximumExpectedValue(Duration.ofSeconds(20).toNanos())
                .sla(
                    Duration.ofMillis(100).toNanos(),
                    Duration.ofMillis(500).toNanos()
                )
                .build()
}
```

### JVM and System Metrics

In addition to [HTTP metrics](#ktor_metrics), Ktor exposes a set of metrics
for [monitoring the JVM][micrometer_jvm_metrics]. You can customize a list of these metrics using the `meterBinders`
property, for example:

```kotlin
install(MicrometerMetrics) {
    // ...
    meterBinders = listOf(
        JvmMemoryMetrics(),
        JvmGcMetrics(),
        ProcessorMetrics()
    )
}
```

You can also assign an empty list to disable these metrics at all.()

## WebSockets Documentation ([JetBrains](https://www.jetbrains.com))

Adds WebSockets support for bidirectional communication with the client

### Description

This feature adds WebSockets support to Ktor. WebSockets are a mechanism to keep a bi-directional real-time ordered
connection between the server and the client. Each message from this channel is called Frame: a frame can be a text or
binary message, or a close or ping/pong message. Frames can be marked as incomplete or final.

### Usage

## Installation

In order to use the `WebSockets` functionality you first have to install it:

```kotlin
install(WebSockets)
```

You can adjust a few parameters when installing if required:

```kotlin
install(WebSockets) {
    pingPeriod = Duration.ofSeconds(60) // Disabled (null) by default
    timeout = Duration.ofSeconds(15)
    maxFrameSize = Long.MAX_VALUE // Disabled (max value). The connection will be closed if surpassed this length.
    masking = false
}
```

## Basic usage

Once installed, you can define the `webSocket` routes for the `routing` feature:

Instead of the short-lived normal route handlers, webSocket handlers are meant to be long-lived. And all the relevant
WebSocket methods are suspended so that the function will be suspended in a non-blocking way while receiving or sending
messages.

`webSocket` methods receive a callback with a `WebSocketSession` instance as the receiver. That interface defines
an `incoming` (`ReceiveChannel`) property and an `outgoing` (`SendChannel`) property, as well as a close method.

### Usage as an suspend actor

```kotlin
routing {
    webSocket("/") { // websocketSession
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }
    }
}
```

### Usage as a Channel

Since the `incoming` property is a `ReceiveChannel`, you can use it with its stream-like interface:

```kotlin
routing {
    webSocket("/") { // websocketSession
        for (frame in incoming.mapNotNull { it as? Frame.Text }) {
            val text = frame.readText()
            outgoing.send(Frame.Text("YOU SAID $text"))
            if (text.equals("bye", ignoreCase = true)) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
            }
        }
    }
}
```

### Options

* `pingPeriod` -- duration between pings or null to disable pings.
* `timeout` -- write/ping timeout after that a connection will be closed
* `maxFrameSize` -- maximum frame that could be received or sent
* `masking` -- whether masking need to be enabled (useful for security)()

# Reporting Issues / Support

Please use [our issue tracker](https://youtrack.jetbrains.com/issues/KTOR) for filing feature requests and bugs. If
you'd like to ask a question, we recommmend [StackOverflow](https://stackoverflow.com/questions/tagged/ktor) where
members of the team monitor frequently.

There is also community support on the [Kotlin Slack Ktor channel](https://app.slack.com/client/T09229ZC6/C0A974TJ9)

# Reporting Security Vulnerabilities

If you find a security vulnerability in Ktor, we kindly request that you reach out to the JetBrains security team via
our [responsible disclosure process](https://www.jetbrains.com/legal/terms/responsible-disclosure.html).

# Contributing

Please see [the contribution guide](CONTRIBUTING.md) and the [Code of conduct](CODE_OF_CONDUCT.md) before contributing.

TODO: contribution of features guide (link)