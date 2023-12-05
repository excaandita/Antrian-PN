package utils

import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, Created, NotFound, Ok}

object ResponseService {

  def ok(message: String): Result = {
    Ok(Json.obj("message" -> message))
  }

  def success[T](message: String, data: T)(implicit writes: Writes[T]): Result = {
    Ok(
      Json.obj(
        "message" -> message,
        "data" -> Json.toJson[T](data)
      )
    )
  }

  def successWithPage[T](data: ListResult[T], page: Int, msgTitle: String)(implicit writes: Writes[T]): Result = {
    Ok(
      Json.obj(
        "message" -> s"Menampilkan ${data.items.length} dari ${data.total} data $msgTitle.",
        "data" -> data.items,
        "page" -> page,
        "limit" -> data.limit,
        "total" -> data.total)
    )
  }

  def created(message: String, data: JsValue): Result = {
    Created(
      Json.obj(
        "message" -> message,
        "data" -> Json.toJson(data)
      )
    )
  }

  def badRequest(message: String, errMessage: String): Result = {
    BadRequest(
      Json.obj(
        "message" -> message,
        "error" -> errMessage
      )
    )
  }

  def notFound(message: String): Result = {
    NotFound(Json.obj("message" -> message))
  }

}
