package controllers.transactions

import models.{CourtRoom, CourtRoomData, QueueData}
import play.api.db.DBApi
import utils.ListResult

import javax.inject.{Inject, Singleton}

case class Kiosk(
                      courtRoom: CourtRoom,
                      remaining_queue: Int,
                      total_queue: Int
                    )
@Singleton
class KioskTrx @Inject()(
                          DBApi: DBApi,
                          courtRoomData: CourtRoomData,
                          queueData: QueueData
                        ){
  def list(): List[Kiosk] = {

    val param: Map[String, String] = Map(
      "active_kiosk" -> "active",
      "order" -> "name"
    )
    val res: ListResult[CourtRoom] = courtRoomData.list(0, param)

    val kiosk: List[Kiosk] = {
      res.items.map{ v =>
        val remainingQueue: Int = queueData.remainingQueue(v.id)
        val totalQueue: Int = queueData.totalQueue(v.id)

        Kiosk(v.copy(), remainingQueue, totalQueue)
      }
    }

    kiosk
  }

}
