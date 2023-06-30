package com.xebia.http4s.metrics

import cats.data.Kleisli
import cats.effect.*
import cats.syntax.all.*
import com.xebia.http4s.leak.User
import io.circe.Codec
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.*
import org.typelevel.log4cats.Logger

import scala.util.Random
import scala.concurrent.duration.*

object ProductCatalogRoutes:

  private def percentage: Int = Random.nextInt(100) + 1

  private def time: Int =
    percentage match
      case p if p <= 50 => Random.between(50, 100) // 50% of calls between 50 and 100
      case p if p <= 95 => Random.between(200, 300) // 45% of calls between 200 and 300
      case _ => Random.between(500, 800) // Remaining 4% of calls between 500 and 800

  private case class Product(id: String, name: String, quantity: Long) derives Codec.AsObject

  private val p1 = Product("p1", "iPhone", 5)

  private val productList = List(
    p1,
    Product("p2", "Samsung Galaxy", 8),
    Product("p3", "MacBook Pro", 3),
    Product("p4", "Dell XPS", 7),
    Product("p5", "Sony PlayStation", 12),
    Product("p6", "Nintendo Switch", 10),
    Product("p7", "Bose Noise Cancelling Headphones", 4),
    Product("p8", "Fitbit Versa", 6),
    Product("p9", "Amazon Echo", 9),
    Product("p10", "Google Nest Hub", 2)
  )

  private def leak[F[_]: Async: Logger]: F[Unit] =
    Logger[F].info("Free memory: " + Runtime.getRuntime.freeMemory()) >>
      Async[F].delay(User.generateNewUser(Random.nextString(999)))

  def memoryLeakRoutes[F[_]: Async: Logger]: HttpRoutes[F] =
    Kleisli(routes.run(_).semiflatTap(_ => leak))

  def timedRoutes[F[_]: Async]: HttpRoutes[F] = Kleisli(routes.run(_).semiflatTap(_ => Async[F].sleep(time.millis)))

  private def routes[F[_]: Concurrent]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    given EntityEncoder[F, List[Product]] = jsonEncoderOf
    given EntityEncoder[F, Product] = jsonEncoderOf
    given EntityDecoder[F, Product] = jsonOf

    val routes: HttpRoutes[F] = HttpRoutes.of[F] {

      case GET -> Root / "products" =>
        percentage match
          case p if p <= 80 => Ok(productList)
          case p if p <= 95 => NotFound("")
          case _ => InternalServerError("")

      case GET -> Root / "products" / _ =>
        percentage match
          case p if p <= 80 => Ok(p1)
          case p if p <= 95 => NotFound("")
          case _ => InternalServerError("")

      case req @ POST -> Root / "products" =>
        req.as[Product].attempt >>
          (percentage match
            case p if p <= 70 => Accepted("")
            case p if p <= 80 => BadRequest("")
            case _ => InternalServerError("")
          )

      case req @ PUT -> Root / "products" / _ =>
        req.as[Product].attempt >>
          (percentage match
            case p if p <= 60 => Ok("")
            case p if p <= 70 => BadRequest("")
            case p if p <= 90 => NotFound("")
            case _ => InternalServerError("")
          )

      case DELETE -> Root / "products" / _ =>
        percentage match
          case p if p <= 50 => NoContent()
          case p if p <= 90 => BadRequest("")
          case _ => InternalServerError("")

      case GET -> Root / "products" / "search" =>
        percentage match
          case p if p <= 75 => Ok("")
          case p if p <= 85 => BadRequest("")
          case _ => InternalServerError("")
    }

    routes
  }
