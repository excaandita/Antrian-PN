package controllers.api

import models.{RunningText, RunningTextData}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import utils.{ListResult, ResponseService => Res}

import javax.inject.Inject

class RunningTextCon @Inject()(
                              cc: ControllerComponents,
                              runningTextData: RunningTextData
                              ) extends AbstractController(cc){

  import runningTextData.runningTextFormat
  def get(id: Int): Action[AnyContent] = Action{implicit request: Request[AnyContent] =>
    val res: Option[RunningText] = runningTextData.get(id)

    res match {
      case Some(res) => Res.success[RunningText]("Berhasil mendapatkan data running text.", res)
      case None => Res.notFound("Data running text tidak ditemukan.")
    }
  }

  def list(page: Int): Action[AnyContent] = Action {
    val res: ListResult[RunningText] = runningTextData.list()

    if (res.total > 0) {
      Res.successWithPage[RunningText](res, page, "Running Text")
    } else NoContent
  }

  def put(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val param = request.body.asJson.get
//    param.as menerima body json sesuai seluruh case class running text
    val runningText = param.as[RunningText]
//    param.as jika dijabarkan akan menjadi:
//    val id = (param \ "id").as[Long]
//    val description = (param \ "description").as[String]
//    val status = (param \ "status").as[Boolean]
//    val runningText = RunningText(id, description, status)

    val res = runningTextData.update(runningText)
    res match {
      case (1, "") =>
        Res.success[JsValue]("Data running text berhasil diperbaharui.",
          Json.obj(
            "running_text_id" -> runningText.id,
            "running_text_desc" -> runningText.description
          ))
      case (-1, message) => {
          Res.badRequest(s"Data running text gagal diperbaharui.", message)
        }
    }
  }

//  def delete(menuId: Long): Action[AnyContent] = jwtAuth { implicit request =>
//    val data: Map[String, String] = Map(
//      "is_delete" -> "yes"
//    )
//    val res: (Int, String) = menuData.deleteMenuById(data, menuId)
//    res match {
//      case (1, "") => Res.success[JsValue]("Data menu berhasil dihapus.", Json.obj("deleted" -> 1))
//      case (0, err) => Res.badRequest("Data menu gagal dihapus.", err)
//    }
//  }


}
