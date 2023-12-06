package models

import anorm.SQL
import anorm.SqlParser.scalar
import parser.RunningTextParser
import play.api.db.DBApi
import play.api.libs.json.{Json, OFormat}
import utils.{Helpers, ListResult}

import javax.inject.Inject

case class RunningText(
  id: Long,
  description: String,
  status: Int = 1
)
class RunningTextData @Inject()(
                                 DBApi: DBApi,
                               ){
  private val db = DBApi.database("antrian_pn")

  implicit val runningTextFormat: OFormat[RunningText] = Json.format[RunningText]

  def list(page: Int = 0, active: String = "active"): ListResult[RunningText] = db.withConnection{ implicit c =>
    val startRow = Helpers.start(page)
    val limitation = (if (page > 0) s" LIMIT ${Helpers.limit} OFFSET ${startRow} " else "")

    val is_active: String = active match {
      case "active" => " WHERE status = 1 "
      case "inactive" => " WHERE status = 0 "
      case _ => " "
    }

    val query: String = "SELECT * FROM running_text "

    val count = "SELECT COUNT(*) FROM running_text "
    val total = SQL(count + is_active).as(scalar[Long].single)

    val list = SQL(query + is_active + limitation).as(RunningTextParser.runningTextParser.*)

    ListResult(list, page, Helpers.limit, total)
  }

  def get(id: Int): Option[RunningText] = db.withConnection { implicit c =>
    val query: String = s"SELECT * FROM running_text WHERE id = {id};"

    SQL(query).on("id" -> id).as(RunningTextParser.runningTextParser.singleOpt)
  }

  def insert(runningText: RunningText): (Option[Any], String) = db.withConnection{ implicit c =>
    val data: Map[String, String] = Map (
      "description" -> runningText.description,
    )
    Helpers.insertDB("running_text", data)
  }

  def update(runningText: RunningText):(Int, String) = db.withConnection {implicit c =>
    val data: Map[String, String] = Map (
      "description" -> runningText.description,
      "status" -> runningText.status.toString
    )

    Helpers.updateDB ("running_text", data, s"id = ${runningText.id}")
  }

  def delete(id: Long): (Int, String) = db.withConnection { implicit c =>

    Helpers.deleteDB("running_text", s"id = $id")
  }



}
