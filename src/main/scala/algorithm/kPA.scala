package algorithm

import model.{DataPoint, MASK, Subspace}
import utils.{MapPointToVectorSpace, Metric}

import scala.util.control.Breaks.{break, breakable}

object kPA {
  /**
   * Computes the partitions for a given point using the kPA algorithm.
   * @param point The point to partition.
   * @param pivots The pivots used for mapping the point to the vector space.
   * @param subspaces The subspaces to partition the point into.
   * @return A list of tuples containing the point and the partition index.
   */
  def apply[A](point: DataPoint[A], pivots: Array[DataPoint[A]], subspaces: Array[Subspace[A]])(implicit m: Metric[A]): List[(Int, DataPoint[A])] = {
    execute(point, pivots, subspaces)
  }

  def execute[A](point: DataPoint[A], pivots: Array[DataPoint[A]], subspaces: Array[Subspace[A]])(implicit m: Metric[A]): List[(Int, DataPoint[A])] = {
    val newPoint: DataPoint[A] = if (point.vectorRep == null) point.withVectorRep(MapPointToVectorSpace(point, pivots)) else point

    var returnList = List[(Int, DataPoint[A])]()
    breakable {
      for (i <- subspaces.indices) {
        val subspace = subspaces(i)
        if (inside(newPoint, subspace.outer)) {
          val mask = (inside(newPoint, subspace.bbCoords), inside(newPoint, subspace.inner)) match {
            case (true, true) => MASK.SPACE_INNER
            case (true, false) => MASK.MARGIN_INNER
            case (false, _) => MASK.MARGIN_OUTER
          }
          val pointWithMask = newPoint.withMask(mask)
          pointWithMask.partition = i
          returnList = (i, pointWithMask) :: returnList
          if (mask == MASK.SPACE_INNER) break
        }
      }
    }

    returnList
  }


  /**
   * Checks if a point is inside a Subspace[A].
   * @param point The point to check.
   * @param subspaceCoords The bounding box of the Subspace[A].
   * @return True if the point is inside the Subspace[A], false otherwise.
   */
  final def inside[A](point: DataPoint[A], subspaceCoords: Array[(Float, Float)]): Boolean = {
    require(point.vectorRep.length == subspaceCoords.length, "Point and Subspace[A] must have the same dimension")
    val v = point.vectorRep
    var i = 0
    while (i < v.length) {
      val (low, high) = subspaceCoords(i)
      val x = v(i)
      if (!((low.isNaN || x >= low) && (high.isNaN || x < high))) return false
      i += 1
    }
    true
  }
}
