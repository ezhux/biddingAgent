package lt.ezz

import lt.ezz.domain.{Banner, BidRequest, BidResponse, Campaign, CampaignNoStream, Device, Geo, Impression, Site, Targeting, TargetingNoStream, User}
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  import DefaultJsonProtocol._

  implicit val geoJsonFormat = jsonFormat4(Geo)

  implicit val impressionRequestJsonFormat = jsonFormat8(Impression)
  implicit val siteJsonFormat = jsonFormat2(Site)
  implicit val userJsonFormat = jsonFormat2(User)


  implicit val deviceJsonFormat = jsonFormat2(Device)
  implicit val bidRequestJsonFormat = jsonFormat5(BidRequest)

  implicit val bannerJsonFormat = jsonFormat4(Banner)
  implicit val bidResponseJsonFormat = jsonFormat5(BidResponse)

  implicit val targetingJsonFormat = jsonFormat2(TargetingNoStream)
  implicit val campaignJsonFormat = jsonFormat6(CampaignNoStream)

}
