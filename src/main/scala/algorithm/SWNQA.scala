package algorithm

import model.DataPoint
import org.apache.spark.rdd.RDD

import scala.util.Random
import utils.Distance.euclidean


case object SWNQA {
  def apply(objects: RDD[DataPoint], epsilon: Float, seed: Int = 42): RDD[(DataPoint, List[DataPoint])] = {
    objects.mapPartitions(iter => {
      val ps = iter.toArray.map((_, List[DataPoint]()))
      val rng = new Random(seed)
      val dimension = rng.nextInt(ps.head._1.dimensions)
      val points = ps.sortBy(point => point._1.vectorRep(dimension))

      for (l <- points.indices) {
        val lPoint = points(l)._1
        val searchRegion = lPoint.vectorRep.map(x => (x - epsilon, x + epsilon))

        var u = l
        var uPoint = points(u)._1
        while (u < points.length && uPoint.vectorRep(dimension) - lPoint.vectorRep(dimension) <= epsilon) {
          if (inSearchRegion(searchRegion, uPoint) && lPoint.distance(uPoint, euclidean) <= epsilon) {
            points(l) = (points(l)._1, points(u) +: points(l)._2)
            points(u) = (points(u)._1, points(l) +: points(u)._2)
          }

          u = u + 1
          if (u < points.length) {
            uPoint = points(u)._1
          }
        }
      }
      points.iterator
    })
  }

  def inSearchRegion(searchRegion: Array[(Float, Float)], point: DataPoint): Boolean = {
    for (i <- searchRegion.indices) {
      if (point.vectorRep(i) < searchRegion(i)._1 || point.vectorRep(i) > searchRegion(i)._2) {
        return false
      }
    }
    true
  }
}
