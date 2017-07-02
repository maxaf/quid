import java.nio.file._
import java.nio.file.attribute._

lazy val baseSettings = Seq(
  organization := "com.bumnetworks",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.2",
  initialCommands := """
    import quid._
  """,
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-language:implicitConversions"
  )) ++ scalariformSettings

lazy val deps = Seq(
 libraryDependencies ++= Seq(
   "org.specs2" %% "specs2-core" % "3.9.1" % "test",
   "org.specs2" %% "specs2-matcher-extra" % "3.9.1" % "test"))

lazy val publishSettings = Seq(
  publishTo := {
    val repo = file(".") / ".." / "repo"
    Some(Resolver.file("repo",
      if (version.value.trim.endsWith("SNAPSHOT")) repo / "snapshots"
      else repo / "releases"))
  }
)

lazy val core = project
  .in(file("."))
  .settings(baseSettings)
  .settings(deps)
  .settings(name := "quid", moduleName := "quid")
  .settings(publishSettings)

lazy val quid = project
  .aggregate(core)
  .settings(baseSettings)
