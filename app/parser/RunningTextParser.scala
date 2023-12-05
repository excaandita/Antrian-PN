package parser

import anorm.{Macro, RowParser}
import models.RunningText

object RunningTextParser{
    val runningTextParser: RowParser[RunningText] = Macro.namedParser[RunningText]
}


