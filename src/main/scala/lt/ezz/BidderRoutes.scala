package lt.ezz

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import lt.ezz.BidRegistry.{ GetCampaign, PlaceBid }
import lt.ezz.domain.{ BidRequest, BidResponse, Campaign, CampaignNoStream, TargetingNoStream }

class BidderRoutes(bidRegistry: ActorRef[BidRegistry.Command])(
    implicit val system: ActorSystem[_]
) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("my-app.routes.ask-timeout")
  )

  def placeBid(bid: BidRequest): Future[BidResponse] =
    bidRegistry.ask(PlaceBid(bid, _))
  def getCampaign(id: Int): Future[Campaign] =
    bidRegistry.ask(GetCampaign(id, _))

  val bidRoutes: Route =
    concat(
      pathPrefix("campaign") {
        path(IntNumber) { id =>
          get {
            onSuccess(getCampaign(id)) { response =>
              complete(
                CampaignNoStream(
                  response.id,
                  response.userId,
                  response.country,
                  TargetingNoStream(response.targeting.cities, response.targeting.targetedSiteIds.toList),
                  response.banners,
                  response.bid
                )
              )
            }
          }
        }
      },
      pathPrefix("bid") {
        concat(
          pathEnd {
            concat(post {
              entity(as[BidRequest]) { bid =>
                onSuccess(placeBid(bid)) { bidResponse =>
                  bidResponse match {
                    case br if br.adid.isDefined =>
                      complete((StatusCodes.Created, bidResponse))
                    case _ => complete((StatusCodes.NoContent))
                  }

                }
              }
            })
          }
        )
      }
    )
}
