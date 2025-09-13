package algorithm

import model.DataPoint
import org.apache.spark.rdd.RDD

import scala.util.Random
import utils.Distance.euclidean


case object SWNQA {
  def apply(objects: RDD[DataPoint], epsilon: Float, seed: Int = 42): RDD[(DataPoint, List[DataPoint])] = {
    val count = objects.count().toInt
    require(count > -1, "Number of objects must be smaller than Int.MaxValue")

    val rng = new Random(seed)
    val dimension = rng.nextInt(objects.first().dimensions)
    val sortedObjects = objects.map((_, List[DataPoint]())).sortBy(point => point._1.vectorRep(dimension))

    sortedObjects.mapPartitions(iter => {
      val points = iter.toArray

      for (l <- points.indices) {
        val lPoint = points(l)._1
        val searchRegion = lPoint.vectorRep.map(x => (x - epsilon, x + epsilon))

        var u = l
        val uPoint = points(u)._1
        while (u < points.length && uPoint.vectorRep(dimension) - lPoint.vectorRep(dimension) > epsilon) {
          if (inSearchRegion(searchRegion, uPoint) && lPoint.distance(uPoint, euclidean) <= epsilon) {
            points(u) +: points(l)._2
            points(l) +: points(u)._2
          }
          u = u + 1
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
