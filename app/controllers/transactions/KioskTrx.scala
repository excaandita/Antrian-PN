package controllers.transactions

import models.{CourtRoom, CourtRoomData, QueueData}
import play.api.libs.json.{Json, OFormat}
import utils.ListResult

import javax.inject.{Inject, Singleton}

case class Kiosk(
                      courtRoom: CourtRoom,
                      remaining_queue: Int,
                      total_queue: Int
                    )
@Singleton
class KioskTrx @Inject()(
                          courtRoomData: CourtRoomData,
                          queueData: QueueData
                        ){
  import courtRoomData.courtRoomFormat
  implicit val kioskFormat: OFormat[Kiosk] = Json.format[Kiosk]
  def list(): Option[List[Kiosk]] = {

    val param: Map[String, String] = Map(
      "active_kiosk" -> "active",
      "order" -> "name"
    )
    val res: ListResult[CourtRoom] = courtRoomData.list(0, param)

    val kiosk: Option[List[Kiosk]] = {
      if(res.items.nonEmpty) {
        val list = res.items.map { v =>
          val remainingQueue: Int = queueData.remainingQueue(v.id)
          val totalQueue: Int = queueData.totalQueue(v.id)

          Kiosk(v.copy(), remainingQueue, totalQueue)
        }
        Some(list)
      }else{
        None
      }
    }

    kiosk
  }

}


