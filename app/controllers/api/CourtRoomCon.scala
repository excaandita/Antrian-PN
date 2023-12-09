package controllers.api

import models.{CourtRoom, CourtRoomData, QueueData}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import utils.{Helpers, ListResult, ResponseService => Res}

import javax.inject.Inject

class CourtRoomCon @Inject()(
                              cc: ControllerComponents,
                              courtRoomData: CourtRoomData,
                              queueData: QueueData
                              ) extends AbstractController(cc){

  import courtRoomData.courtRoomFormat

  def list(page: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val param: Map[String, String] = Map(
      "active" -> Helpers.getData(request, "active"),
      "order" -> Helpers.getData(request, "order")
    )
    val res: ListResult[CourtRoom] = courtRoomData.list(page, param)

    if (res.total > 0) {
      Res.successWithPage[CourtRoom](res, page, "Ruang Sidang")
    } else NoContent
  }

  def listKiosk(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val param: Map[String, String] = Map(
      "active" -> Helpers.getData(request, "active"),
    "order" -> Helpers.getData(request, "order")
    )
    val res: ListResult[CourtRoom] = courtRoomData.list(0, param)

    if (res.total > 0) {
      Res.successWithPage[CourtRoom](res, 0, "Ruang Sidang")
    } else NoContent
  }

  def get(id: Int): Action[AnyContent] = Action{
    val res: Option[CourtRoom] = courtRoomData.get(id)

    res match {
      case Some(res) => Res.success[CourtRoom]("Berhasil mendapatkan data Ruang sidang.", res)
      case None => Res.notFound("Data ruang sidang tidak ditemukan.")
    }
  }

  def add(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
    val name = (param \ "name").as[String]
    val description = (param \ "description").as[String]
    val status = (param \ "status").asOpt[Int].getOrElse(1)
    val active = (param \ "active").asOpt[Int].getOrElse(0)
    val file_sound = (param \ "file_sound").asOpt[String].getOrElse("default.mp3")

    val courtRoom = CourtRoom(-1, name, description, status, active, file_sound)

    val res = courtRoomData.insert(courtRoom)
    res match {
      case (Some(data), _) =>
        Res.created(
          "Data Ruang Sidang berhasil ditambahkan.",
          Json.obj("court_room_id" -> data.asInstanceOf[Long]))

      case (None, message) => {
          Res.badRequest(s"Data Ruang Sidang gagal ditambahkan.", message)
        }
    }
  }

  def put(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val param = request.body.asJson.get
//    param.as menerima body json sesuai seluruh case class running text
    val courtRoom = param.as[CourtRoom]
//    param.as jika dijabarkan akan menjadi:
//    val id = (param \ "id").as[Long]
//    val description = (param \ "description").as[String]
//    val status = (param \ "status").as[Boolean]
//    val runningText = RunningText(id, description, status)

    val res = courtRoomData.update(courtRoom)
    res match {
      case (1, "") =>
        Res.success[JsValue]("Data Ruang Sidang berhasil diperbaharui.",
          Json.obj(
            "court_room_id" -> courtRoom.id,
            "court_room_name" -> courtRoom.name,
            "court_room_desc" -> courtRoom.description,
            "court_room_active_monitor" -> courtRoom.active_monitor,
            "court_room_active_kiosk" -> courtRoom.active_kiosk,
            "court_room_file_sound" -> courtRoom.file_sound,
          ))
      case (-1, message) => {
          Res.badRequest(s"Data Ruang Sidang gagal diperbaharui.", message)
        }
    }
  }

  def remove(): Action[AnyContent] = Action { implicit request =>
    val param = request.body.asJson.get
    val id = (param \ "id").as[Long]

    val res: (Int, String) = courtRoomData.delete(id)
    res match {
      case (1, "") => Res.success[JsValue]("Data Ruang Sidang berhasil dihapus.", Json.obj("deleted" -> 1))
      case (0, err) => Res.badRequest("Data Ruang Sidang gagal dihapus.", err)
    }
  }


}
