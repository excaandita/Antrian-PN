package controllers.api

import models.{CourtRoomData, Queue, QueueData, QueueJoin}
import play.api.libs.json.Json
import play.api.mvc._
import utils.{Helpers, ListResult, ResponseService => Res}

import java.{sql => js, util => ju}
import javax.inject.Inject

class QueueCon @Inject()(cc: ControllerComponents,
                         queueData: QueueData,
                         courtRoomData: CourtRoomData
                        ) extends AbstractController(cc){
  import courtRoomData.courtRoomFormat
  import queueData.{queueFormat, queueJoinFormat}

  def list(page: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val active: String = Helpers.getData(request, "active")
    val res: ListResult[Queue] = queueData.list(page, active)

    if (res.total > 0) {
      Res.successWithPage[Queue](res, page, "Queue")
    } else NoContent
  }
  def get(id: Int): Action[AnyContent] = Action{
    val res: Option[QueueJoin] = queueData.get(id)

    res match {
      case Some(res) => Res.success[QueueJoin]("Berhasil mendapatkan data queue.", res)
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
//    val date: String = (param \ "date").asOpt[String].getOrElse()

    val now: ju.Date = new ju.Date()
    val nowTimestamp: js.Timestamp = new js.Timestamp(System.currentTimeMillis())
//    GET LAST queue by id court room
    val queueNumber: Int = {
      val lastQueue: Option[Int] = queueData.getQueue(idCourtRoom, Helpers.date2String(now, "yyyy-MM-dd"))
      lastQueue match{
        case Some(queueNum) => queueNum + 1
        case None => 1
      }
    }

    val queue = Queue(-1, now, queueNumber, idCourtRoom, None, nowTimestamp, 0)

//  Insert data queue
    val res = {
      queueData.insert(queue) match {
        case (Some(data), _) => ("", data.asInstanceOf[Long])
        case (None, message) =>
          println(message)
          (message, -1.asInstanceOf[Long])
      }
    }

//    res greater than 0 means the insert is success
    if(res._2 > 0){
      val getQueue: Option[QueueJoin] = queueData.get(res._2)

//      count queue left
      val queueLeft: Int = queueData.getQueueLeft(idCourtRoom, Helpers.date2String(now, "yyyy-MM-dd"))

      if(getQueue.isDefined){
        Res.created("Data queue berhasil ditambahkan",
          Json.obj(
            "queue" -> getQueue.get.queue_number,
            "date_now" -> getQueue.get.pick_up_time,
            "court_room" -> getQueue.get.court_room.get,
            "queue_left" -> queueLeft
          )
        )
      }else{
        Res.badRequest("Data queue gagal ditambahkan", "Gagal mendapatkan data antrian terakhir")
      }
    }else{
      Res.badRequest("Data queue gagal ditambahkan", res._1)
    }
  }
}
