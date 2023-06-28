package com.xebia.http4s.metrics

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp.Simple:
  val run = Http4sMetricsSampleServer.run[IO]
