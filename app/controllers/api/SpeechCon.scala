package controllers.api

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.Configuration 
import scala.concurrent.{ExecutionContext}

import java.io.{File}



@Singleton
class SpeechCon @Inject()(config: Configuration, 
                        cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {


    def getAudioFile(audioFile: String) = Action { implicit request: Request[AnyContent] => 
        val audioDir: String = config.get[String]("app.soundDir")
        val path: String = s"${audioDir}/${audioFile}"
        
        Ok.sendFile(new File(path))
    }



   
  // End of Class
}