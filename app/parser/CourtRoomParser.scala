package parser

import anorm.{Macro, RowParser}
import models.CourtRoom

object CourtRoomParser{
    val courtRoomParser: RowParser[CourtRoom] = Macro.namedParser[CourtRoom]
}


