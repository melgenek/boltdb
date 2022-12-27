ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .enablePlugins(JmhPlugin)
  .settings(
    name := "boltdb-scala",
    libraryDependencies ++= List(
      "com.softwaremill.magnolia1_3" %% "magnolia" % "1.2.6",
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test
    )
  )
