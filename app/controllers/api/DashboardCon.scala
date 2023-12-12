package controllers.api

import models.{QueueData}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import utils.{Helpers, ListResult, ResponseService => Res}

import javax.inject.{Inject, Singleton}

@Singleton
class DashboardCon @Inject()(
                              cc: ControllerComponents,
                              queueData: QueueData,
                              ) extends AbstractController(cc){

  def get(date: String): Action[AnyContent] = Action{
    val res: (Int, Int, Int) = queueData.getDashboard(date)

    Res.success[JsValue]("Berhasil mendapatkan data.", Json.obj(
        "total" -> res._1,
        "remaining" -> res._2,
        "called" -> res._3,
        ))
    
  }
}
