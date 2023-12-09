package controllers.api

import models.{Queue, QueueData}
import play.api.mvc._
import utils.{Helpers, ListResult, ResponseService => Res}

import java.{sql => js, util => ju}
import javax.inject.Inject

class QueueCon @Inject()(cc: ControllerComponents,
                         queueData: QueueData
                        ) extends AbstractController(cc){
  import queueData.queueFormat

  def list(page: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val active: String = Helpers.getData(request, "active")
    val res: ListResult[Queue] = queueData.list(page, active)

    if (res.total > 0) {
      Res.successWithPage[Queue](res, page, "Queue")
    } else NoContent
  }
  def get(id: Int): Action[AnyContent] = Action{
    val res: Option[Queue] = queueData.get(id)

    res match {
      case Some(res) => Res.success[Queue]("Berhasil mendapatkan data queue.", res)
      case None => Res.notFound("Data queue tidak ditemukan.")
    }
  }

  def getQueue(): Action[AnyContent] = Action{ implicit request: Request[AnyContent] =>
    val idCourtRoom: String = Helpers.getData(request, "id_court_room")
    val date: String = Helpers.getData(request, "date")

    val res: Option[Int] = queueData.getQueue(idCourtRoom.toLong, date)

    res match {
      case Some(res) => Res.success[Int]("Berhasil mendapatkan data queue.", res)
      case None => Res.notFound("Data queue tidak ditemukan.")
    }
  }

  def add(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
    val idCourtRoom: Long = (param \ "id_court_room").as[Long]
    val date: String = (param \ "date").as[String]

//    GET LAST queue by id court room
    val queueNumber: Int = {
      val lastQueue: Option[Int] = queueData.getQueue(idCourtRoom, date)
      lastQueue match{
        case Some(queueNum) => queueNum + 1
        case None => 1
      }
    }

    val now: ju.Date = new ju.Date()
    val nowTimestamp: js.Timestamp = new js.Timestamp(System.currentTimeMillis())

    val queue = Queue(-1, now, queueNumber, idCourtRoom, None, nowTimestamp, 0)

//  Insert data queue
    val res = {
      queueData.insert(queue) match {
        case (Some(data), _) => data.asInstanceOf[Int]
        case (None, message) =>
          println(message)
          -1
      }
    }

//    res greater than 0 means the insert is success
    if(res  > 0){
      queueData.get(res)
    }
    Ok("")


  }
}
