package parser

import anorm.{Macro, RowParser}
import models.Video

object VideoParser{
    val videoParser: RowParser[Video] = Macro.namedParser[Video]
}


