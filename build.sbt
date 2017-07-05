import java.nio.file._
import java.nio.file.attribute._

enablePlugins(ScalafmtPlugin)

lazy val baseSettings = Seq(
  organization := "com.bumnetworks",
  version := "0.0.1",
  scalaVersion := "2.12.2",
  crossScalaVersions := Seq(scalaVersion.value, "2.11.8", "2.11.11", "2.10.5"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-language:implicitConversions"
  ),
  scalafmtOnCompile in ThisBuild := true,
  scalafmtTestOnCompile in ThisBuild := true
)

lazy val deps = Seq(
  libraryDependencies ++= Seq(
    "org.specs2" %% "specs2-core" % "3.9.1" % "test",
    "org.specs2" %% "specs2-matcher-extra" % "3.9.1" % "test"))

lazy val publishSettings = Seq(
  publishTo := {
    val repo = file(".") / ".." / "repo"
    Some(
      Resolver.file("repo",
                    if (version.value.trim.endsWith("SNAPSHOT"))
                      repo / "snapshots"
                    else repo / "releases"))
  }
)

lazy val quid = project
  .in(file("."))
  .settings(baseSettings)
  .settings(deps)
  .settings(name := "quid", moduleName := "quid")
  .settings(publishSettings)
  .settings(
    initialCommands := """
    import quid._
    """
  )
