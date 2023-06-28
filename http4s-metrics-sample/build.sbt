val Http4sVersion = "0.23.20"
val Http4sPrometheusMetricsV = "0.24.3"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.4.8"
val MunitCatsEffectVersion = "1.0.7"
val CirceV = "0.14.5"

lazy val root = (project in file("."))
  .settings(
    organization := "com.xebia",
    name := "http4s-metrics-sample",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.3.0",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-prometheus-metrics" % Http4sPrometheusMetricsV,
      "io.circe" %% "circe-generic" % CirceV,
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
