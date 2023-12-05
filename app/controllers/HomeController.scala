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

  def runningText(): Action[AnyContent] = Action {
    Ok(views.html.pages.masterdata.running_text())
  }
}
