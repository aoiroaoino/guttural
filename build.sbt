import microsites._
import Dependencies._

inThisBuild(Seq(
  organization := "dev.aoiroaoino",
  name := "guttural",
  version := "0.1.0-SNAPSHOT"
))

lazy val v = new {
  val scala213 = "2.13.3"
  val scala212 = "2.12.11"
  // servers
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

lazy val guttural = (project in file("."))
  .settings(moduleName := "guttural")
  .settings(commonSettings: _*)
  .aggregate(core, serverAkkaHttp, codecCirce, codecPlayJson)
  .dependsOn(core, serverAkkaHttp, codecCirce, codecPlayJson)

lazy val core = project
  .settings(moduleName := "guttural-core")
  .settings(commonSettings)

lazy val plugin = project
  .settings(moduleName := "guttural-plugin")
  .settings( // plugin settings
    sbtPlugin := true,
    sbtVersion := "1.2.8",
    scalaVersion := v.scala212
  )

lazy val serverAkkaHttp = (project in file("server-akka-http"))
  .settings(moduleName := "guttural-server-akka-http")
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"   % "10.1.9",
    "com.typesafe.akka" %% "akka-stream" % "2.5.23",
    airframe,
    "org.slf4j" % "slf4j-api" % v.slf4jApi
  ))
  .dependsOn(core)

lazy val clientScalajHttp = (project in file("client-scalaj-http"))
  .settings(moduleName := "guttural-client-scalaj-http")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "org.scalaj" %% "scalaj-http" % "2.4.2"
  ))
  .dependsOn(core)

lazy val codecCirce = (project in file("codec-circe"))
  .settings(moduleName := "guttural-codec-circe")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % v.circe,
    "io.circe" %% "circe-parser" % v.circe
  ))
  .dependsOn(core)

lazy val codecPlayJson = (project in file("codec-play-json"))
  .settings(moduleName := "guttural-codec-play-json")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-json" % "2.8.0-M5"
  ))
  .dependsOn(core)

lazy val docs = project
  .settings(moduleName := "guttural-docs")
  .settings(Seq(
    micrositeName := "Monoton",
    micrositeDescription := "Simple and Monotonous Web Framework for Scala",
//    micrositeUrl := "https://github.com/aoiroaoino/guttural",
    micrositeDocumentationUrl := "/docs",
    micrositeAuthor := "aoiroaoino",
    micrositeHomepage := "https://github.com/aoiroaoino/guttural",
    // Twitter
    micrositeTwitter := "@aoiroaoino",
    micrositeTwitterCreator := "@aoiroaoino",
    // GitHub
    micrositeGithubOwner := "aoiroaoino",
    micrositeGithubRepo := "guttural",
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
  .settings(moduleName := "guttural-example")
  .settings(commonSettings)
  .settings(libraryDependencies += "io.circe" %% "circe-generic" % v.circe)
  .dependsOn(core, serverAkkaHttp, codecCirce, codecPlayJson)

lazy val example2 = project
  .settings(moduleName := "guttural-example2")
  .settings(commonSettings)
  .settings(libraryDependencies += "io.circe" %% "circe-generic" % v.circe)
  .settings(libraryDependencies += airframe)
  .dependsOn(core, serverAkkaHttp, codecCirce, codecPlayJson)

lazy val it = project
  .settings(moduleName := "guttural-it")
  .settings(commonSettings)
  .dependsOn(core, clientScalajHttp)
  // integration test settings
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "it,test"
  )
