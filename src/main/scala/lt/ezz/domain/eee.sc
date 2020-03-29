


import spray.json._
import lt.ezz.domain._
import lt.ezz.JsonFormats._

val br = BidRequest("aaa", None, Site(4888, "domain"), Some(User("userId", Some(Geo(Some("Latvia"), Some("Riga"), None, None)))), None)

br.toJson

val a = List(true, false, false)
a.reduce(_ && _)
