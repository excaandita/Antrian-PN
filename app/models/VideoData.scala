package models

import anorm.SQL
import anorm.SqlParser.scalar
import parser.VideoParser
import play.api.db.DBApi
import play.api.libs.json.{Json, OFormat}
import utils.{Helpers, ListResult}

import javax.inject.Inject

case class Video(
  id: Long,
  name: String,
  path: String,
  status: Int = 1
)
class VideoData @Inject()(
                                 DBApi: DBApi,
                               ){
  private val db = DBApi.database("antrian_pn")

  implicit val videoFormat: OFormat[Video] = Json.format[Video]

  def list(page: Int = 0, active: String = "active"): ListResult[Video] = db.withConnection{ implicit c =>
    val startRow = Helpers.start(page)
    val limitation = (if (page > 0) s" LIMIT ${Helpers.limit} OFFSET ${startRow} " else "")

    val is_active: String = active match {
      case "active" => " WHERE status = 1 "
      case "inactive" => " WHERE status = 0 "
      case _ => " "
    }

    val query: String = "SELECT * FROM video "

    val count = "SELECT COUNT(*) FROM video "
    val total = SQL(count + is_active).as(scalar[Long].single)

    val list = SQL(query + is_active + limitation).as(VideoParser.videoParser.*)

    ListResult(list, page, Helpers.limit, total)
  }

  def get(id: Int): Option[Video] = db.withConnection { implicit c =>
    val query: String = s"SELECT * FROM video WHERE id = {id};"

    SQL(query).on("id" -> id).as(VideoParser.videoParser.singleOpt)
  }

  def insert(video: Video): (Option[Any], String) = db.withConnection{ implicit c =>
    val data: Map[String, String] = Map (
      "name" -> video.name,
      "path" -> video.path,
      "status" -> video.status.toString,
    )
    Helpers.insertDB("video", data)
  }

  def update(video: Video):(Int, String) = db.withConnection {implicit c =>
    val data: Map[String, String] = Map (
      "name" -> video.name,
      "path" -> video.path,
      "status" -> video.status.toString
    )

    Helpers.updateDB("video", data, s"id = ${video.id}")
  }

  def delete(id: Long): (Int, String) = db.withConnection { implicit c =>

    Helpers.deleteDB("video", s"id = $id")
  }

}
