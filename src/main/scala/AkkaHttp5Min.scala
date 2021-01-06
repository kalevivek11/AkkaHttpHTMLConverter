import java.net.URLEncoder

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import scala.concurrent.duration._

object AkkaHttp5Min {
  implicit val system = ActorSystem() //akka actors
  implicit val materializer = ActorMaterializer() //akka streams

  import system.dispatcher // threadpoll

  val source =
    """
      |object SimpleApp {
      |  val aField = 2
      |
      |  def aMethod(x: Int) = x + 1
      |
      |  def main(args: Array[String]) = {
      |    println(aMethod(aField))
      |  }
      |}
""".stripMargin


  def sendRequest(source: String) = {
    val responseFuture = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = "http://markup.su/api/highlighter",
        entity = HttpEntity(
          ContentTypes.`application/x-www-form-urlencoded`,
          s"source=${URLEncoder.encode(source.trim, "UTF-8")}&language=Scala&theme=Sunburst"
        )
      )
    )
    responseFuture.flatMap(_.entity.toStrict(2 seconds)).map(_.data.utf8String).foreach(println)

  }

  def main(args: Array[String]): Unit = {
    sendRequest(source)
  }
}
