package models

import anorm.SQL
import anorm.SqlParser.scalar
import parser.CourtRoomParser
import play.api.db.DBApi
import play.api.libs.json.{Json, OFormat}
import utils.{Helpers, ListResult}

import javax.inject.Inject

case class CourtRoom(
  id: Long,
  name: String,
  description: String,
  status: Int = 1,
  active: Int = 1,
  file_sound: String
)
class CourtRoomData @Inject()(
                                 DBApi: DBApi,
                               ){
  private val db = DBApi.database("antrian_pn")

  implicit val courtRoomFormat: OFormat[CourtRoom] = Json.format[CourtRoom]

  def list(page: Int = 0, active: String = "active"): ListResult[CourtRoom] = db.withConnection{ implicit c =>
    val startRow = Helpers.start(page)
    val limitation = (if (page > 0) s" LIMIT ${Helpers.limit} OFFSET ${startRow} " else "")

    val is_active: String = active match {
      case "active" => " WHERE active = 1 "
      case "inactive" => " WHERE active = 0 "
      case _ => " "
    }

    val query: String = "SELECT * FROM court_room "

    val count = "SELECT COUNT(*) FROM court_room "
    val total = SQL(count + is_active).as(scalar[Long].single)

    val list = SQL(query + is_active + limitation).as(CourtRoomParser.courtRoomParser.*)

    ListResult(list, page, Helpers.limit, total)
  }

  def get(id: Int): Option[CourtRoom] = db.withConnection { implicit c =>
    val query: String = s"SELECT * FROM court_room WHERE id = {id};"

    SQL(query).on("id" -> id).as(CourtRoomParser.courtRoomParser.singleOpt)
  }

  def insert(courtRoom: CourtRoom): (Option[Any], String) = db.withConnection{ implicit c =>
    val data: Map[String, String] = Map (
      "name" -> courtRoom.name,
      "description" -> courtRoom.description,
      "status" -> courtRoom.status.toString,
      "active" -> courtRoom.active.toString,
      "file_sound" -> courtRoom.file_sound,
    )
    Helpers.insertDB("court_room", data)
  }

  def update(courtRoom: CourtRoom):(Int, String) = db.withConnection {implicit c =>
    val data: Map[String, String] = Map (
      "name" -> courtRoom.name,
      "description" -> courtRoom.description,
      "status" -> courtRoom.status.toString,
      "active" -> courtRoom.active.toString,
      "file_sound" -> courtRoom.file_sound,
    )

    Helpers.updateDB("court_room", data, s"id = ${courtRoom.id}")
  }

  def delete(id: Long): (Int, String) = db.withConnection { implicit c =>

    Helpers.deleteDB("court_room", s"id = $id")
  }



}
