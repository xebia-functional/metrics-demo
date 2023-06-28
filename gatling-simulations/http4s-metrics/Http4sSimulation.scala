import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class Http4sSimulation extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8080")

  val nbUsers = Integer.getInteger("users", 10)
  val duringMinutes = java.lang.Long.getLong("minutes", 1)

  val scn = scenario("Http4s Scenario")
    .during(duringMinutes.minutes) {
      randomSwitch(
        50d -> exec(http("Get All Products")
          .get("/products")),
        20d -> exec(http("Get Product by ID")
          .get("/products/123")),
        10d -> exec(http("Add New Product")
          .post("/products")
          .body(StringBody("""{"name": "New Product", "quantity": 10}""")).asJson),
        10d -> exec(http("Update Product")
          .put("/products/123")
          .body(StringBody("""{"name": "Updated Product", "quantity": 5}""")).asJson),
        5d -> exec(http("Delete Product")
          .delete("/products/123")),
        5d -> exec(http("Search Products")
          .get("/products/search?keyword=test"))
      )
    }

  setUp(scn.inject(atOnceUsers(nbUsers)).protocols(httpConf))
}
