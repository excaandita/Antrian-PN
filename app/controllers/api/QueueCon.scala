package controllers.api

import models._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import utils.{Helpers, ListResult, ResponseService => Res}

import java.{sql => js, util => ju}
import javax.inject.{Inject, Singleton}

@Singleton
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

    val res: Option[Int] = queueData.lastQueue(idCourtRoom.toLong, date)

    res match {
      case Some(res) => Res.success[Int]("Berhasil mendapatkan data queue.", res)
      case None => Res.notFound("Data queue tidak ditemukan.")
    }
  }

  def searchQueue(): Action[AnyContent] = Action{ implicit request: Request[AnyContent] =>
    val idCourtRoom: String = Helpers.getData(request, "id_court_room")
//    val search: String = Helpers.getData(request, "search")

    val res: Option[QueueSearch] = queueData.lastCall(idCourtRoom.toLong)

    res match {
      case Some(res) => {
        val nextQueue: Option[Int] = queueData.searchQueueNumber(idCourtRoom.toLong, res.queue_number, "next")
        val prevQueue: Option[Int] = queueData.searchQueueNumber(idCourtRoom.toLong, res.queue_number, "prev")
        Res.success[JsValue]("Berhasil mendapatkan data queue.",
          Json.obj(
            "queue_number" -> res.queue_number,
            "id_queue_next" -> nextQueue,
            "id_queue_prev" -> prevQueue,
            "id_queue_now" -> res.id,
          )
        )
      }
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
      val lastQueue: Option[Int] = queueData.lastQueue(idCourtRoom, Helpers.date2String(now, "yyyy-MM-dd"))
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
      val remainingQueue: Int = queueData.remainingQueue(idCourtRoom)

      if(getQueue.isDefined){
        Res.created("Data queue berhasil ditambahkan",
          Json.obj(
            "queue" -> getQueue.get.queue_number,
            "date_now" -> getQueue.get.pick_up_time,
            "court_room" -> getQueue.get.court_room.get,
            "queue_left" -> remainingQueue
          )
        )
      }else{
        Res.badRequest("Data queue gagal ditambahkan", "Gagal mendapatkan data antrian terakhir")
      }
    }else{
      Res.badRequest("Data queue gagal ditambahkan", res._1)
    }
  }

  def callQueue(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
    val idQueue: Long = (param \ "id_queue").as[Long]

    val getQueue: Option[QueueJoin] = queueData.get(idQueue)
    if(getQueue.isDefined){
      val nowTimestamp: js.Timestamp = new js.Timestamp(System.currentTimeMillis())
      val remainingQueue: Int = queueData.remainingQueue(getQueue.get.id_court_room)
      val nextQueue = queueData.searchQueueNumber(getQueue.get.id_court_room, getQueue.get.queue_number, "next")
      val prevQueue = queueData.searchQueueNumber(getQueue.get.id_court_room, getQueue.get.queue_number, "prev")

      val queue: Queue = Queue(
        getQueue.get.id,
        getQueue.get.date,
        getQueue.get.queue_number,
        getQueue.get.id_court_room,
        Some(nowTimestamp),
        getQueue.get.pick_up_time,
        1
      )
      val res = queueData.update(queue)
      res match {
        case (1, "") =>
          Res.success[JsValue]("Data queue berhasil diperbaharui.",
            Json.obj(
              "queue" -> getQueue.get.queue_number,
              "court_room" -> getQueue.get.court_room,
              "remaining_queue" -> remainingQueue,
              "id_queue_next" -> nextQueue,
              "id_queue_now" -> getQueue.get.id,
              "id_queue_prev" -> prevQueue
            ))
        case (-1, message) => {
          Res.badRequest(s"Data queue gagal diperbaharui.", message)
        }
      }

    }else{
      Res.badRequest(s"Data queue gagal diperbaharui.", "Data queue tidak ditemukan")
    }



  }
}
