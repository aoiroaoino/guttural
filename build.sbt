inThisBuild(Seq(
  organization := "dev.aoiroaoino",
  name := "monoton",
  version := "0.1.0-SNAPSHOT",
  scalafmtOnCompile := true
))

lazy val v = new {
  val scala213 = "2.13.0"
  val scala212 = "2.12.9"
}

lazy val commonSettings = Seq(
  scalaVersion := v.scala213,
  crossScalaVersions := Seq(v.scala213, v.scala212),
  scalacOptions ++= Seq(
    "-feature",
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .aggregate(core, serverNetty)
  .dependsOn(core, serverNetty)

lazy val core = project
  .settings(moduleName := "monoton-core")
  .settings(commonSettings)

lazy val serverNetty = (project in file("server-netty"))
  .settings(moduleName := "monoton-server-netty")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "io.netty" % "netty-all" % "4.1.38.Final",
    "org.slf4j" % "slf4j-api" % "1.7.28"
  ))
  .dependsOn(core)

// TODO: move to other repository
lazy val example = project
  .settings(moduleName := "monoton-example")
  .settings(commonSettings)
  .dependsOn(core, serverNetty)
