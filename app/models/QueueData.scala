package models
import anorm.SqlParser.scalar
import anorm.{SQL, _}
import parser.QueueParser
import play.api.db.DBApi
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, OFormat, _}
import utils.{Helpers, ListResult}

import java.{sql => js, util => ju}
import javax.inject.Inject

case class Queue(
                  id: Long,
                  date: ju.Date,
                  queue_number: Int,
                  id_court_room: Long,
                  call_time: Option[js.Timestamp],
                  pick_up_time: js.Timestamp,
                  status: Int = 0
                    )
case class QueueJoin(
                  id: Long,
                  date: ju.Date,
                  queue_number: Int,
                  id_court_room: Long,
                  court_room: Option[CourtRoom],
                  call_time: Option[js.Timestamp],
                  pick_up_time: js.Timestamp,
                  status: Int = 0
                    )

class QueueData @Inject()(
                                 DBApi: DBApi,
                                 courtRoomData: CourtRoomData
                               ) {
  private val db = DBApi.database("antrian_pn")



  def timestampToLong(t: js.Timestamp): Long = t.getTime
  def longToTimestamp(dt: Long): js.Timestamp = new js.Timestamp(dt)

  implicit val timestampFormat: Format[js.Timestamp] = new Format[js.Timestamp] {
    def writes(t: js.Timestamp): JsValue = Json.toJson(timestampToLong(t))
    def reads(json: JsValue): JsResult[js.Timestamp] = Json.fromJson[Long](json).map(longToTimestamp)
  }

  implicit val queueFormat: OFormat[Queue] = Json.format[Queue]

  import courtRoomData.courtRoomFormat
  implicit val queueJoinFormat: OFormat[QueueJoin] = Json.format[QueueJoin]

  def list(page: Int = 0, active: String = "active"): ListResult[Queue] = db.withConnection{ implicit c =>
    val startRow = Helpers.start(page)
    val limitation = (if (page > 0) s" LIMIT ${Helpers.limit} OFFSET ${startRow} " else "")

    val is_active: String = active match {
      case "active" => " WHERE status = 1 "
      case "inactive" => " WHERE status = 0 "
      case _ => " "
    }

    val query: String = "SELECT * FROM queue "

    val count = "SELECT COUNT(*) FROM queue "
    val total = SQL(count + is_active).as(scalar[Long].single)

    val list = SQL(query + is_active + limitation).as(QueueParser.queueParser.*)

    ListResult(list, page, Helpers.limit, total)
  }

  def get(id: Long): Option[QueueJoin] = db.withConnection { implicit c =>
    val query: String =
      """
        |SELECT * FROM queue q
        |JOIN court_room cr
        |ON (q.id_court_room = cr.id)
        |WHERE q.id = {id} """.stripMargin

    SQL(query).on("id" -> id).as(QueueParser.queueJoinParser.singleOpt)
  }

  def getQueueLeft(idCourtRoom: Long, date: String): Int = db.withConnection { implicit c =>
    val query: String =
      """
        |SELECT COUNT(status) FROM queue q
        |WHERE q.id_court_room = {idCourtRoom} AND date = {date} AND status = 'no' """.stripMargin

    SQL(query).on("idCourtRoom" -> idCourtRoom, "date" -> date).as(scalar[Int].single)
  }

  def getQueue(idCourtRoom: Long, date: String): Option[Int] = db.withConnection{implicit c =>
    val query: String =
      """SELECT queue_number FROM queue
        |WHERE id_court_room = {idCourtRoom} AND date = {dateNow}
        |ORDER BY id DESC LIMIT 1
        |""".stripMargin

    SQL(query).on("idCourtRoom" -> idCourtRoom, "dateNow" -> date).as(scalar[Int].singleOpt)
  }

  def insert(queue: Queue): (Option[Any], String) = db.withConnection{ implicit c =>
    val data: Map[String, String] = Map (
      "date" -> Helpers.date2String(queue.date, "yyyy-MM-dd"),
      "queue_number" -> queue.queue_number.toString,
      "id_court_room" -> queue.id_court_room.toString,
      "call_time" -> queue.pick_up_time.toString,
      "pick_up_time" -> queue.pick_up_time.toString,
      "status" -> queue.status.toString
    )

    Helpers.insertDB("queue", data)
  }

  def update(queue: Queue):(Int, String) = db.withConnection {implicit c =>
    val data: Map[String, String] = Map (
      "date" -> Helpers.date2String(queue.date, "yyyy-MM-dd"),
      "queue_number" -> queue.queue_number.toString,
      "id_court_room" -> queue.id_court_room.toString,
      "call_time" -> Helpers.date2String(queue.call_time.get, "yyyy-MM-dd HH:mm:ss"),
      "pick_up_time" -> Helpers.date2String(queue.pick_up_time, "yyyy-MM-dd HH:mm:ss"),
      "status" -> queue.status.toString
    )

    Helpers.updateDB ("queue", data, s"id = ${queue.id}")
  }

  def delete(id: Long): (Int, String) = db.withConnection { implicit c =>

    Helpers.deleteDB("queue", s"id = $id")
  }
}

