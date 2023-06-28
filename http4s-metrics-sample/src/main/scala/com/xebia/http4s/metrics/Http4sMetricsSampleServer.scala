package com.xebia.http4s.metrics

import cats.effect.Async
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.metrics.prometheus.{Prometheus, PrometheusExportService}
import org.http4s.server.middleware.{Logger, Metrics}

object Http4sMetricsSampleServer:

  def run[F[_]: Async: Network]: F[Nothing] = {
    for
      prometheus <- PrometheusExportService.build[F]
      metrics <- Prometheus.metricsOps[F](prometheus.collectorRegistry, "server")
      httpApp = (
        Metrics(metrics, classifierF = _ => Some("product"))(ProductCatalogRoutes.timedRoutes[F]) <+> prometheus.routes
      ).orNotFound
      _ <-
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(httpApp)
          .build
    yield ()
  }.useForever
