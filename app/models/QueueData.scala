package models
import anorm.SqlParser.scalar
import anorm.{SQL, _}
import parser.QueueParser
import parser.QueueParser.queueSearchParser
import play.api.db.DBApi
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, OFormat, _}
import utils.{Helpers, ListResult}

import java.util.Date
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

case class QueueSearch(
                      id: Long,
                      queue_number: Int
                      )

class QueueData @Inject()(
                                 DBApi: DBApi,
                                 courtRoomData: CourtRoomData
                               ) {
  private val db = DBApi.database("antrian_pn")
  private val today = Helpers.date2String(new Date(), "yyyy-MM-dd")



  def timestampToLong(t: js.Timestamp): Long = t.getTime
  def longToTimestamp(dt: Long): js.Timestamp = new js.Timestamp(dt)

  implicit val timestampFormat: Format[js.Timestamp] = new Format[js.Timestamp] {
    def writes(t: js.Timestamp): JsValue = Json.toJson(timestampToLong(t))
    def reads(json: JsValue): JsResult[js.Timestamp] = Json.fromJson[Long](json).map(longToTimestamp)
  }

  implicit val queueFormat: OFormat[Queue] = Json.format[Queue]
  implicit val queueSearchFormat: OFormat[QueueSearch] = Json.format[QueueSearch]

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
        |SELECT q.id AS id_queue, q.date, q.queue_number, q.id_court_room, q.call_time, q.pick_up_time,
        |q.status, cr.* FROM queue q
        |JOIN court_room cr
        |ON (q.id_court_room = cr.id)
        |WHERE q.id = {id} """.stripMargin

    SQL(query).on("id" -> id).as(QueueParser.queueJoinParser.singleOpt)
  }

  def remainingQueue(idCourtRoom: Long, date: String = today): Int = db.withConnection { implicit c =>
    val query: String =
      """
        |SELECT COUNT(status) FROM queue q
        |WHERE q.id_court_room = {idCourtRoom} AND date = {date} AND status = 'no' """.stripMargin

    SQL(query).on("idCourtRoom" -> idCourtRoom, "date" -> date).as(scalar[Int].single)
  }

  def totalQueue(idCourtRoom: Long, date: String = today): Int = db.withConnection { implicit c =>
    val query: String =
      """
        |SELECT COUNT(id) FROM queue q
        |WHERE q.id_court_room = {idCourtRoom} AND date = {date} """.stripMargin

    SQL(query).on("idCourtRoom" -> idCourtRoom, "date" -> date).as(scalar[Int].single)
  }

  def lastQueue(idCourtRoom: Long, date: String = today): Option[Int] = db.withConnection{implicit c =>
    val query: String =
      """SELECT queue_number FROM queue
        |WHERE id_court_room = {idCourtRoom} AND date = {dateNow}
        |ORDER BY id DESC LIMIT 1
        |""".stripMargin

    SQL(query).on("idCourtRoom" -> idCourtRoom, "dateNow" -> date).as(scalar[Int].singleOpt)
  }

  def lastCall(idCourtRoom: Long, date: String = today): Option[QueueSearch] = db.withConnection{implicit c =>
    val query: String =
      """SELECT id, queue_number FROM queue
        |WHERE id_court_room = {idCourtRoom} AND date = {dateNow}
        |ORDER BY call_time DESC LIMIT 1
        |""".stripMargin

    SQL(query).on("idCourtRoom" -> idCourtRoom, "dateNow" -> date).as(queueSearchParser.singleOpt)
  }

  def searchQueueNumber(idCourtRoom: Long, queue_number: Int, search: String, date: String = today): Option[Int] = db.withConnection{implicit c =>

    var q = ""
    if(queue_number > -1){
      search match{
        case "next" => q += s" AND queue_number = ${queue_number + 1}"
        case "prev" => q += s" AND queue_number = ${queue_number - 1}"
        case _ => q += s" AND queue_number = ${queue_number}"
      }
    }


    val query: String =
      """SELECT id FROM queue
        |WHERE id_court_room = {idCourtRoom} AND date = {dateNow}
        |""".stripMargin

    SQL(query + q).on("idCourtRoom" -> idCourtRoom, "dateNow" -> date).as(scalar[Int].singleOpt)
  }

  def insert(queue: Queue): (Option[Any], String) = db.withConnection{ implicit c =>
    val data: Map[String, String] = Map (
      "date" -> Helpers.date2String(queue.date, "yyyy-MM-dd"),
      "queue_number" -> queue.queue_number.toString,
      "id_court_room" -> queue.id_court_room.toString,
      "call_time" -> (if(queue.call_time.isDefined) queue.call_time.toString else null),
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
      "call_time" -> queue.call_time.orNull.toString,
      "pick_up_time" -> queue.pick_up_time.toString,
      "status" -> queue.status.toString
    )

    Helpers.updateDB ("queue", data, s"id = ${queue.id}")
  }

  def delete(id: Long): (Int, String) = db.withConnection { implicit c =>

    Helpers.deleteDB("queue", s"id = $id")
  }
}

