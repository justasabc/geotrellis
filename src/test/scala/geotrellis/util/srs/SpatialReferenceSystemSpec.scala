package geotrellis.util.srs

import org.scalatest.FunSpec
import org.scalatest.matchers._

import geotrellis.feature.Feature.factory
import com.vividsolutions.jts.geom._
import scala.collection.JavaConversions._

class SpatialReferenceSystemSpec extends FunSpec with ShouldMatchers {
  case object TestSRS extends SpatialReferenceSystem {
    val name = "Test SRS"

    def transform(x:Double,y:Double,targetSRS:SpatialReferenceSystem) =
      (x * 10.0,y * -10.0)
  }

  describe("SpatialReferenceSystem.transform") {
    it("should transform a coordinate") {
      val coord = new Coordinate(3.0,5.0)
      val result = TestSRS.transform(coord,LatLng)
        (result.x,result.y) should be ((30.0,-50.0))
    }

    it("should transform a Point") {
      val point = factory.createPoint(new Coordinate(3.0,5.0))
      val result = TestSRS.transform(point,LatLng)
        (result.getX,result.getY) should be ((30.0,-50.0))
    }

    it("should transform a Polygon") {
      def c(x:Double,y:Double) = new Coordinate(x,y)
      val lr = factory.createLinearRing(Array(c(3.0,5.0),c(4.0,6.0),c(1.0,7.0),c(3.0,5.0)))
      val poly = factory.createPolygon(lr,Array())
      val result = TestSRS.transform(poly,LatLng)
      for((c1,c2) <- result.getExteriorRing.getCoordinateSequence
                           .toCoordinateArray
                           .zip(
                              Array(c(30.0,-50.0),c(40.0,-60.0),c(10.0,-70.0),c(30.0,-50.0))
                            )) {
        c1 should be (c2)
      }
    }
  }
}
