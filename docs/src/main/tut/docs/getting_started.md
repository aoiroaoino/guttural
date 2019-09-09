---
layout: docs
title: Getting Started
position: 1
---

# Getting Started

## Using SBT Plugin

plugins.sbt

```sbt
addSbtPlugin("dev.aoiroaoino" % "monoton-plugin" % "[versioin]")
```

build.sbt

```sbt
enablePlugins(MonotonPlugin)
```

## A la carte 

### Base (Required)

```sbt
libraryDependencies += "dev.aoiroaoino" %% "monoton-core" % "[version]"
```

### Choose Server Implementation (Required)

```sbt
// Netty
libraryDependencies += "dev.aoiroaoino" %% "monoton-server-netty" % "[version]"

// Akka HTTP
libraryDependencies += "dev.aoiroaoino" %% "monoton-server-akka-http" % "[version]"
```

### Choose Request/Response body codec (Optional)

```sbt
// Circe
libraryDependencies += "dev.aoiroaoino" %% "monoton-codec-circe" % "[version]"

// Play JSON
libraryDependencies += "dev.aoiroaoino" %% "monoton-codec-play-json" % "[version]"
```
