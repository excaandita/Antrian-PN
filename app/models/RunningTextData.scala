package models

import anorm.SQL
import parser.RunningTextParser
import play.api.db.DBApi
import play.api.libs.json.{Json, OFormat}

import javax.inject.Inject

case class RunningText(
  id: Int,
  description: String,
  status: String
)
class RunningTextData @Inject()(
                                 DBApi: DBApi,
                               ){
  private val db = DBApi.database("antrian_pn")

  implicit val runningTextFormat: OFormat[RunningText] = Json.format[RunningText]

  def list(): List[RunningText] = db.withConnection{ implicit c =>
    val query: String = "SELECT * FROM running_text"

    SQL(query).as(RunningTextParser.runningTextParser.*)
  }

  def get(id: Int): Option[RunningText] = db.withConnection { implicit c =>
    println(id)
    val query: String = s"SELECT * FROM running_text WHERE id = {id};"
    SQL(query).on("id" -> id).as(RunningTextParser.runningTextParser.singleOpt)
  }



}
