ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.4.2"

lazy val root = (project in file("."))
  .settings(
    name := "ShortcutApiScala"
  )

lazy val catsVersion = "2.12.0"
lazy val catsEffectVersion = "3.5.4"
lazy val http4sVersion = "0.23.27"
lazy val logbackVersion = "1.5.6"
lazy val http4sBlazeServerVersion = "0.23.16"
lazy val http4sBlazeClientVersion = "0.23.16"
lazy val circeGenericVersion = "0.14.7"
lazy val circeParserVersion = "0.14.9"
lazy val scalaTestVersion = "3.2.18"


libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sBlazeServerVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % circeGenericVersion,
  "io.circe" %% "circe-parser" % circeParserVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.http4s" %% "http4s-blaze-client" % http4sBlazeClientVersion % Test
)
