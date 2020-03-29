package lt.ezz

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }
import lt.ezz.domain.{ Banner, BidRequest, BidResponse, Campaign, Impression }

import scala.util.Random

object MatcherActor {

  sealed trait MatcherCommand
  final case class PerformMatch(bid: BidRequest, campaigns: List[Campaign], replyTo: ActorRef[BidResponse])
      extends MatcherCommand

  def apply(): Behavior[MatcherCommand] = Behaviors.receive { (context, message) =>
    message match {
      case PerformMatch(bid, campaigns, replyTo) => {
        context.log.info("Performing match")
        val response = (bid, campaigns) match {
          case (b, c) if c.map(co => checkSite(b, co) && checkLocation(b, co)).reduce(_ || _) =>
            BidResponse(Random.nextInt(1000).toString, b.id, 0.0, Some("1"), None)
          case _ =>
            BidResponse("", "", 0.0, None, None)
        }
        replyTo ! response
        context.log.info("Responded, terminating actor")
        Behaviors.stopped

      }
    }
  }

  private def checkSite(b: BidRequest, c: Campaign) = {
    c.targeting.targetedSiteIds.toList.contains(b.site.id)
  }

  private def checkLocation(b: BidRequest, c: Campaign) = {
    c.targeting.targetedSiteIds.toList.contains(b.site.id) &&
    c.targeting.cities
      .contains(b.user.flatMap(_.geo.flatMap(_.city)).getOrElse("")) &&
    c.country == b.user.flatMap(_.geo.flatMap(_.country)).getOrElse("")
  }

  private def checkBannerSize(i: Impression, b: Banner) = {
    b.height == List(i.h, i.hmin, i.hmax).flatten.head &&
    b.width == List(i.w, i.wmin, i.wmax).flatten.head
  }

  private def checkPrice(b: BidRequest, c: Campaign) = ???

}
