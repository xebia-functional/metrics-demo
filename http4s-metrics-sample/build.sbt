val Http4sVersion = "0.23.20"
val Http4sPrometheusMetricsV = "0.24.3"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.4.8"
val MunitCatsEffectVersion = "1.0.7"
val CirceV = "0.14.5"

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "com.xebia"
ThisBuild / scalaVersion := "3.3.0"

ThisBuild / assemblyMergeStrategy := {
  case PathList(ps @ _*) if ps.lastOption.contains("module-info.class") =>
    MergeStrategy.discard
  case x if x.endsWith(".properties") => MergeStrategy.filterDistinctLines
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(
    organization := "com.xebia",
    name := "http4s-metrics-sample",
    assembly / mainClass := Some("com.xebia.http4s.metrics.Main"),
    Docker / packageName := "http4s-metrics-sample",
    dockerBaseImage := "openjdk:11-jre-slim-buster",
    dockerExposedPorts ++= Seq(8080),
    dockerUpdateLatest := true,
    dockerAlias := DockerAlias(
      registryHost = Some("ghcr.io"),
      username = Some((ThisBuild / organization).value),
      name = (Docker / packageName).value,
      tag = Some("latest")
    ),
    Universal / javaOptions ++= Seq(
      // -J params will be added as jvm parameters
      "-J-Xmx128m",
      "-J-Xms128m",
      "-J-XshowSettings:vm",
      "-J-Xlog:gc+heap+exit"
    ),
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.3.0",
    libraryDependencies ++= Seq(
      "org.http4s"          %% "http4s-ember-server"       % Http4sVersion,
      "org.http4s"          %% "http4s-circe"              % Http4sVersion,
      "org.http4s"          %% "http4s-dsl"                % Http4sVersion,
      "org.http4s"          %% "http4s-prometheus-metrics" % Http4sPrometheusMetricsV,
      "io.circe"            %% "circe-generic"             % CirceV,
      "org.scalameta"       %% "munit"                     % MunitVersion           % Test,
      "org.typelevel"       %% "munit-cats-effect-3"       % MunitCatsEffectVersion % Test,
      "ch.qos.logback"       % "logback-classic"           % LogbackVersion,
      "org.fusesource.jansi" % "jansi"                     % "2.4.0"
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
