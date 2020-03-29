package lt.ezz

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import lt.ezz.domain.{ BidRequest, BidResponse, Campaign, CampaignGenerator }

import scala.util.Random

object BidRegistry {
  sealed trait Command
  final case class PlaceBid(bid: BidRequest, replyTo: ActorRef[BidResponse]) extends Command
  final case class GetCampaign(id: Int, replyTo: ActorRef[Campaign])         extends Command

  def apply(): Behavior[Command] = {
    // generating data for test
    registry(
      (0 to 50).toList.map(_ => CampaignGenerator.genCampaign.sample.get)
    )
  }

  private def registry(campaigns: List[Campaign]): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case PlaceBid(bid, replyTo) =>
          val matcher =
            context.spawn(MatcherActor(), "matcher" + Random.nextInt(1000))
          matcher ! MatcherActor.PerformMatch(bid, campaigns, replyTo)
          Behaviors.same
        case GetCampaign(id, replyTo) =>
          replyTo ! campaigns(id)
          Behaviors.same
      }
    }

}
