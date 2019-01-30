ThisBuild / organization := "lt.tabo"
ThisBuild / scalaVersion := "2.12.8"

lazy val root = project.in(file("."))
  .settings(
    name := "phantom-type-safe-map",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )

scalaSource in Compile := baseDirectory.value / "src"
scalaSource in Test := baseDirectory.value / "test-src"
