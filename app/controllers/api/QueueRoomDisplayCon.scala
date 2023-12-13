package controllers.api


import javax.inject.{Inject, Singleton}

import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Source}
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.mvc._
import play.api.libs.json._ 

case class NotifDisplayMessage(
              no_antri: Int, 
              id_court_room: String, 
              sound: String, 
              loket: String, 
              panggil: String
                )  

@Singleton
class QueueRoomDisplayCon @Inject() (
								cc: ControllerComponents)
                              (implicit val mat: Materializer)
  extends AbstractController(cc) {



  private[this] val (sink, source) =
    MergeHub.source[JsValue](perProducerBufferSize = 16)
      .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
      .run()

  implicit val notifFormat = Json.format[NotifDisplayMessage]

  
  def pushMessage = Action{ implicit request: Request[AnyContent] => 
    
    val param: JsValue = request.body.asJson.get
    // println(param);
    //val notifMessage: NotifMultiPoliMessage = param.as[NotifMultiPoliMessage]

    val message: JsValue = Json.obj(
                            "data" -> param
                          )

    Source.single(message).runWith(sink)
    Ok
  }

  def notifChannel = Action{ implicit request: Request[AnyContent] => 
    Ok.chunked(source via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }

  // End of Class
}
