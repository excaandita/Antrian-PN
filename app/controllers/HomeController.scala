package controllers

import models.{RunningText, RunningTextData}
import play.api._
import play.api.mvc._

import javax.inject._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               runningTextData: RunningTextData) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val running_text_list: List[RunningText] = runningTextData.list().items

    Ok(views.html.index(running_text_list))
  }


//  MASTERDATA
  def dashboard(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.pages.masterdata.dashboard())
  }

  def runningText():Action[AnyContent] = Action{ implicit request: Request[AnyContent]=>
    Ok(views.html.pages.masterdata.running_text())
  }

  def courtRoom(): Action[AnyContent] = Action { implicit request: Request[AnyContent]=>
    Ok(views.html.pages.masterdata.court_room())
  }

  def video(): Action[AnyContent] = Action { implicit request: Request[AnyContent]=>
    Ok(views.html.pages.masterdata.video())
  }

//  ANTRIAN
  def queue(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.pages.antrian())
  }
}