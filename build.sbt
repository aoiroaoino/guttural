import microsites._

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
  val circe = "0.12.1"
  // other (common)
  val slf4jApi = "1.7.28"
}

lazy val commonSettings = Seq(
  scalaVersion := v.scala213,
  crossScalaVersions := Seq(v.scala213, v.scala212),
  scalacOptions ++= Seq(
    "-feature",
    "-unchecked",
    "-deprecation",
    "-language:implicitConversions,higherKinds"
  )
)

lazy val guiceSettings = Seq(
  libraryDependencies ++= Seq(
    "javax.inject" % "javax.inject" % "1",
    "com.google.inject" % "guice" % "4.2.2"
  )
)

lazy val root = (project in file("."))
  .settings(moduleName := "monoton")
  .settings(commonSettings)
  .aggregate(core, serverNetty, serverAkkaHttp, codecCirce, codecPlayJson)
  .dependsOn(core, serverNetty, serverAkkaHttp, codecCirce, codecPlayJson)

lazy val core = project
  .settings(moduleName := "monoton-core")
  .settings(commonSettings)

lazy val plugin = project
  .settings(moduleName := "monoton-plugin")
  .settings(Seq( // plugin settings
    sbtPlugin := true,
    sbtVersion := "1.2.8",
    scalaVersion := v.scala212
  ))

lazy val serverNetty = (project in file("server-netty"))
  .settings(moduleName := "monoton-server-netty")
  .settings(commonSettings)
  .settings(guiceSettings)
  .settings(libraryDependencies ++= Seq(
    "io.netty" % "netty-all" % v.netty,
    "org.slf4j" % "slf4j-api" % v.slf4jApi
  ))
  .dependsOn(core)

lazy val serverAkkaHttp = (project in file("server-akka-http"))
  .settings(moduleName := "monoton-server-akka-http")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"   % "10.1.9",
    "com.typesafe.akka" %% "akka-stream" % "2.5.23",
    "org.slf4j" % "slf4j-api" % v.slf4jApi
  ))
  .dependsOn(core)

lazy val clientScalajHttp = (project in file("client-scalaj-http"))
  .settings(moduleName := "monoton-client-scalaj-http")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "org.scalaj" %% "scalaj-http" % "2.4.2"
  ))
  .dependsOn(core)

lazy val codecCirce = (project in file("codec-circe"))
  .settings(moduleName := "monoton-codec-circe")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % v.circe,
    "io.circe" %% "circe-parser" % v.circe
  ))
  .dependsOn(core)

lazy val codecPlayJson = (project in file("codec-play-json"))
  .settings(moduleName := "monoton-codec-play-json")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-json" % "2.8.0-M5"
  ))
  .dependsOn(core)

lazy val docs = project
  .settings(moduleName := "monoton-docs")
  .settings(Seq(
    micrositeName := "Monoton",
    micrositeDescription := "Simple and Monotonous Web Framework for Scala",
//    micrositeUrl := "https://github.com/aoiroaoino/monoton",
    micrositeDocumentationUrl := "/docs",
    micrositeAuthor := "aoiroaoino",
    micrositeHomepage := "https://github.com/aoiroaoino/monoton",
    // Twitter
    micrositeTwitter := "@aoiroaoino",
    micrositeTwitterCreator := "@aoiroaoino",
    // GitHub
    micrositeGithubOwner := "aoiroaoino",
    micrositeGithubRepo := "monoton",
    // other
    micrositeHighlightTheme := "atom-one-light",
    micrositeExtraMdFiles := Map(
      file("LICENSE") -> ExtraMdFileConfig(
        "license.md",
        "home",
        Map("title" -> "License", "section" -> "license", "position" -> "7")
      ),
    )
  ))
  .enablePlugins(MicrositesPlugin)

// TODO: move to other repository
lazy val example = project
  .settings(moduleName := "monoton-example")
  .settings(commonSettings)
  .settings(libraryDependencies += "io.circe" %% "circe-generic" % v.circe)
  .dependsOn(core, serverNetty, serverAkkaHttp, codecCirce, codecPlayJson)

lazy val it = project
  .settings(moduleName := "monoton-it")
  .settings(commonSettings)
  .dependsOn(core, serverNetty, clientScalajHttp)
  // integration test settings
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "it,test"
  )
