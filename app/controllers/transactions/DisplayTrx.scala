package controllers.transactions

import models.{CourtRoom, CourtRoomData}
import play.api.db.DBApi
import utils.ListResult

import javax.inject.{Inject, Singleton}

case class DisplayCourt(
                      courtRoom: CourtRoom
                    )
@Singleton
class DisplayTrx @Inject()(
                          DBApi: DBApi,
                          courtRoomData: CourtRoomData,
                        ){
  def list(): List[DisplayCourt] = {

    val param: Map[String, String] = Map(
      "active_kiosk" -> "",
      "active_monitor" -> "active",
      "order" -> "name"
    )
    val res: ListResult[CourtRoom] = courtRoomData.list(0, param)

    val displayCourt: List[DisplayCourt] = {
      res.items.map{ v =>
        DisplayCourt(v.copy())
      }
    }

    displayCourt
  }

}
