package parser

import anorm._
import anorm.SqlParser._
import anorm.{Column, MetaDataItem, RowParser, TypeDoesNotMatch}
import models.{Queue, QueueJoin, QueueSearch}

import java.time.LocalDateTime
import java.{sql => js, util => ju}

object QueueParser {
  implicit def dateToTimestamp: Column[js.Timestamp] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match {
        case ts: js.Timestamp => Right(ts)
        case d: ju.Date => Right(new js.Timestamp(d.getTime))
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to Timestamp for column " + qualified))
      }
    }
//  val queueParser: RowParser[Queue] = Macro.namedParser[Queue]
  val queueParser: RowParser[Queue] = {
    get[Long]("id") ~
      get[ju.Date]("date") ~
      get[Int]("queue_number") ~
      get[Long]("id_court_room") ~
      get[Option[LocalDateTime]]("call_time") ~
      get[LocalDateTime]("pick_up_time") ~
      get[Int]("status") map {
      case id ~ date ~ queue_number ~ id_court_room ~ call_time ~ pick_up_time ~ status =>

        val call_time_validation = if(call_time.isDefined) Some(js.Timestamp.valueOf(call_time.get)) else None

        Queue(id, date, queue_number, id_court_room, call_time_validation, js.Timestamp.valueOf(pick_up_time), status)
    }
  }

//  parser with join table id_court_room
  val queueJoinParser: RowParser[QueueJoin] = {
    get[Long]("id_queue") ~
      get[ju.Date]("date") ~
      get[Int]("queue_number") ~
      get[Long]("id_court_room") ~
      CourtRoomParser.courtRoomParser.? ~
      get[Option[LocalDateTime]]("call_time") ~
      get[LocalDateTime]("pick_up_time") ~
      get[Int]("status") map {
      case id_queue ~ date ~ queue_number ~ id_court_room ~ court_room ~ call_time ~ pick_up_time ~ status =>

        val call_time_validation = if(call_time.isDefined) Some(js.Timestamp.valueOf(call_time.get)) else None

        QueueJoin(id_queue, date, queue_number, id_court_room, court_room, call_time_validation, js.Timestamp.valueOf(pick_up_time), status)
    }
  }

  val queueSearchParser: RowParser[QueueSearch] = {
    get[Long]("id") ~
      get[Int]("queue_number") map {
      case id ~ queue_number =>
        QueueSearch(id, queue_number)
    }
  }
}
