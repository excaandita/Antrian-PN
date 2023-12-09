package controllers

import controllers.transactions.{Kiosk, KioskTrx}
import models.{RunningText, RunningTextData, QueueData, QueueJoin}
import play.api._
import play.api.mvc._
import utils.{Helpers, ListResult, ResponseService => Res}

import javax.inject._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               runningTextData: RunningTextData,
                               kioskTrx: KioskTrx,
                               queueData: QueueData
                               ) extends BaseController {
  import queueData.{queueFormat, queueJoinFormat}
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

  def kiosk(): Action[AnyContent] = Action {
    val court_room_list: List[Kiosk] = kioskTrx.list()

    Ok(views.html.boards.kiosk(court_room_list))
  }

  def display(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val running_text_list: List[RunningText] = runningTextData.list().items

    Ok(views.html.boards.display(running_text_list))
  }

  def cetak(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>

    val queue_number: String = Helpers.getData(request, "queue_number")
    val date_now: String = Helpers.getData(request, "date_now")
    val court_room_name: String = Helpers.getData(request, "court_room_name")
    val queue_left: String = Helpers.getData(request, "queue_left")

    Ok(views.html.printing.cetak_antrian(queue_number, date_now, court_room_name, queue_left))
  }

}
