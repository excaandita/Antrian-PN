package controllers.api

import models.{RunningText, RunningTextData}
import play.api.libs.json.Json
import play.api.mvc._
import utils.{ResponseService => Res}

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

  def list(): Action[AnyContent] = Action {
    val res: List[RunningText] = runningTextData.list()

    Ok(Json.obj("data" -> res))
  }


}
