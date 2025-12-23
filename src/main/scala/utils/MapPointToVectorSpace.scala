package utils

import model.DataPoint

object MapPointToVectorSpace {
  /**
   * Maps a data point to a vector space defined by the given pivots.
   *
   * (This is ϕ(o) in "Efficient Metric Indexing for Similarity Search")
   * @param point The data point to map.
   * @param pivots The pivots defining the vector space.
   * @return The coordinates of the data point in the vector space.
   */
  def apply[A](point: DataPoint[A], pivots: Array[DataPoint[A]])(implicit m: Metric[A]): Array[Float] = {
    pivots.map(pivot => pivot.distance(point))
  }

  def apply[A](point: DataPoint[A], pivots: List[DataPoint[A]])(implicit m: Metric[A]): List[Float] = {
    pivots.map(pivot => pivot.distance(point))
  }

  def apply[A](point: DataPoint[A], pivots: Array[DataPoint[A]], pointer: Int)(implicit m: Metric[A]): Array[Float] = {
    var i = 0
    val n = pointer
    val result = new Array[Float](pivots.length)
    while (i <= n) {
      result(i) = pivots(i).distance(point)
      i += 1
    }
    result
  }

}
