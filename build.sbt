ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .enablePlugins(JmhPlugin)
  .settings(
    name := "boltdb-scala",
    libraryDependencies ++= List(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test
    )
  )
