inThisBuild(Seq(
  organization := "dev.aoiroaoino",
  name := "monoton",
  version := "0.1.0-SNAPSHOT"
))

lazy val v = new {
  val scala213 = "2.13.0"
  val scala212 = "2.12.9"
  // servers
  val netty = "4.1.38.Final"
  val akkaHttp = "10.1.9"
  // adapters
  val circe = "0.12.0-RC4"
  // other (common)
  val slf4jApi = "1.7.28"
}

lazy val commonSettings = Seq(
  scalaVersion := v.scala213,
  crossScalaVersions := Seq(v.scala213, v.scala212),
  scalacOptions ++= Seq(
    "-feature",
    "-unchecked",
    "-deprecation"
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .aggregate(core, serverNetty, serverAkkaHttp, adapterCirce)
  .dependsOn(core, serverNetty, serverAkkaHttp, adapterCirce)

lazy val core = project
  .settings(moduleName := "monoton-core")
  .settings(commonSettings)

lazy val serverNetty = (project in file("server-netty"))
  .settings(moduleName := "monoton-server-netty")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "io.netty" % "netty-all" % v.netty,
    "org.slf4j" % "slf4j-api" % v.slf4jApi
  ))
  .dependsOn(core)

lazy val serverAkkaHttp = (project in file("server-akka-http"))
  .settings(moduleName := "monoton-server-akka-http")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http-core" % v.akkaHttp,
    "org.slf4j" % "slf4j-api" % v.slf4jApi
  ))
  .dependsOn(core)

lazy val adapterCirce = (project in file("adapter-circe"))
  .settings(moduleName := "monoton-adapter-circe")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % v.circe,
    "io.circe" %% "circe-parser" % v.circe
  ))
  .dependsOn(core)

// TODO: move to other repository
lazy val example = project
  .settings(moduleName := "monoton-example")
  .settings(commonSettings)
  .settings(libraryDependencies += "io.circe" %% "circe-generic" % v.circe)
  .dependsOn(core, serverNetty, adapterCirce)
