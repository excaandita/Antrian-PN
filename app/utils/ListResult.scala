package utils

case class ListResult[A](items: List[A], page: Int, limit: Int, total: Long)
