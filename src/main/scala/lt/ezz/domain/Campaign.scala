package lt.ezz.domain

final case class Campaign(id: Int, userId: Int, country: String, targeting: Targeting, banners: List[Banner], bid: Double)
final case class Targeting(cities: List[String], targetedSiteIds: LazyList[Int])
final case class Banner(id: Int, src: String, width: Int, height: Int)

final case class BidRequest(id: String, imp: Option[List[Impression]], site: Site, user: Option[User], device: Option[Device])
final case class Impression(id: String, wmin: Option[Int], wmax: Option[Int], w: Option[Int], hmin: Option[Int], hmax: Option[Int], h: Option[Int], bidFloor: Option[Double])
final case class Site(id: Int, domain: String)
final case class User(id: String, geo: Option[Geo])
final case class Device(id: String, geo: Option[Geo])
final case class Geo(country: Option[String], city: Option[String], lat: Option[Double], lon: Option[Double])

final case class BidResponse(id: String, bidRequestId: String, price: Double, adid: Option[String], banner: Option[Banner])

final case class CampaignNoStream(id: Int, userId: Int, country: String, targeting: TargetingNoStream, banners: List[Banner], bid: Double)
final case class TargetingNoStream(cities: List[String], targetedSiteIds: List[Int])

object CampaignGenerator {

  import org.scalacheck.Gen
  import org.scalacheck.Gen.{oneOf}

  def genCampaign = for {
    id <- Gen.choose(0, 500)
    userId <- Gen.choose(0, 10000)
    country <- oneOf("Lithuania", "Latvia", "Estonia")
    targeting <- for {
      cities <- Gen.containerOfN[Array, String](3, Gen.oneOf("Kaunas", "Tartu", "Riga", "Vilnius", "Tallinn")).map(_.toList)
      targetedSiteIds <- Gen.containerOfN[Array, Int](100, Gen.chooseNum(0, 5000)).map(_.to(LazyList))
    } yield Targeting(cities, targetedSiteIds)
    banner <- for {
      id <- Gen.choose(5000, 6000)
      src <- Gen.oneOf("src1", "src2", "src3")
      width <- Gen.oneOf(180, 480, 760)
      height <- Gen.oneOf(20, 40, 60)
    } yield Banner(id, src, width, height)
    banners <- Gen.containerOfN[Array, Banner](3, banner).map(_.toList)
    bid <- Gen.choose(5.0, 500)
  } yield Campaign(id, userId, country, targeting, banners, bid)

}