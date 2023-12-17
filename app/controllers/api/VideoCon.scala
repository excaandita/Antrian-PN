package controllers.api

import models.{Video, VideoData}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import utils.{Helpers, ListResult, ResponseService => Res}
import play.api.libs.Files.TemporaryFile

import javax.inject.{Inject, Singleton}
import java.nio.file.{Files, Path, Paths}

@Singleton
class VideoCon @Inject()(
                              cc: ControllerComponents,
                              videoData: VideoData
                              ) extends AbstractController(cc){

  import videoData.videoFormat

  def list(page: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val active: String = Helpers.getData(request, "active")
    val res: ListResult[Video] = videoData.list(page, active)

    if (res.total > 0) {
      Res.successWithPage[Video](res, page, "Video")
    } else NoContent
  }

  def get(id: Int): Action[AnyContent] = Action{
    val res: Option[Video] = videoData.get(id)

    res match {
      case Some(res) => Res.success[Video]("Berhasil mendapatkan data Video.", res)
      case None => Res.notFound("Data Video tidak ditemukan.")
    }
  }

  def add(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
    val name = (param \ "name").as[String]
    val path = (param \ "path").as[String]
    val status = (param \ "status").asOpt[Int].getOrElse(1)

    val video = Video(-1, name, path, status)

    val res = videoData.insert(video)
    res match {
      case (Some(data), _) =>
        Res.created(
          "Data Video berhasil ditambahkan.",
          Json.obj("video_id" -> data.asInstanceOf[Long]))

      case (None, message) => {
          Res.badRequest(s"Data Video gagal ditambahkan.", message)
        }
    }
  }

  def put(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val param = request.body.asJson.get
    val video = param.as[Video]

    val res = videoData.update(video)
    res match {
      case (1, "") =>
        Res.success[JsValue]("Data Video berhasil diperbaharui.",
          Json.obj(
            "video_id" -> video.id,
          ))
      case (-1, message) => {
          Res.badRequest(s"Data Video perbaharui.", message)
        }
    }
  }

  def remove(): Action[AnyContent] = Action { implicit request =>
    val param = request.body.asJson.get
    val id = (param \ "id").as[Long]

    val res: (Int, String) = videoData.delete(id)
    res match {
      case (1, "") => Res.success[JsValue]("Data video berhasil dihapus.", Json.obj("deleted" -> 1))
      case (0, err) => Res.badRequest("Data video gagal dihapus.", err)
    }
  }


}
