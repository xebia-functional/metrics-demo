package com.xebia.http4s.metrics

import cats.effect.{Async, Resource}
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.metrics.prometheus.{Prometheus, PrometheusExportService}
import org.http4s.server.middleware.Metrics
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Http4sMetricsSampleServer:

  def run[F[_]: Async: Network]: F[Nothing] = {
    for
      prometheus <- PrometheusExportService.build[F]
      metrics <- Prometheus.metricsOps[F](prometheus.collectorRegistry, "server")
      given Logger[F] = Slf4jLogger.getLogger[F]
      routes = ProductCatalogRoutes.timedRoutes[F]
      httpApp = (
        Metrics(metrics, classifierF = _ => Some("product"))(routes) <+> prometheus.routes
      ).orNotFound
      _ <-
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(httpApp)
          .build
    yield ()
  }.useForever
