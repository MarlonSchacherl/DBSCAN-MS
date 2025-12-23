package algorithm

import model.DataPoint
import utils.Metric

import scala.util.Random

object HF {
  /** Selects pivot candidates using the Hull Foci (HF) algorithm.
   *
   * @param dataset The sampled dataset from which to select pivot candidates.
   * @param numberOfPivotCandidates The number of pivot candidates to select.
   * @param seed Random seed for reproducibility.
   * @return An array of selected pivot candidates.
   */
  def apply[A](dataset: Array[DataPoint[A]],
            numberOfPivotCandidates: Int = 40,
            seed: Int)(implicit m: Metric[A]): Array[DataPoint[A]] = {
    execute(dataset, numberOfPivotCandidates, seed)
  }

  def execute[A](dataset: Array[DataPoint[A]],
                            numberOfPivotCandidates: Int,
                            seed: Int)(implicit m: Metric[A]): Array[DataPoint[A]] = {
    require(dataset.length > numberOfPivotCandidates, "Number of pivot candidates must be smaller than the dataset size!")

    val rng = new Random(seed)
    val pivotCandidates = new Array[DataPoint[A]](numberOfPivotCandidates)
    val startingPoint = dataset(rng.nextInt(dataset.length))

    pivotCandidates(0) = findFarthestPoint(dataset, startingPoint)
    pivotCandidates(1) = findFarthestPoint(dataset, pivotCandidates(0))

    val edge = pivotCandidates(0).distance(pivotCandidates(1))

    val errors = new Array[Float](dataset.length)
    for (i <- dataset.indices if !pivotCandidates.contains(dataset(i))) {
      errors(i) = Math.abs(edge - dataset(i).distance(pivotCandidates(0)))
    }

    for (i <- 2 until numberOfPivotCandidates) {
      var minimalError = Float.MaxValue
      var bestCandidate: DataPoint[A] = null
      for (j <- dataset.indices if !pivotCandidates.contains(dataset(j))) {
        val error = errors(j) + Math.abs(edge - dataset(j).distance(pivotCandidates(i - 1)))
        errors(j) = error
        if (error < minimalError) {
          minimalError = error
          bestCandidate = dataset(j)
        }
      }
      pivotCandidates(i) = bestCandidate
    }
    pivotCandidates
  }

  /** Finds the point in the dataset that is farthest from the reference point.
   *
   * @param dataset The dataset to search.
   * @param referencePoint The reference point.
   * @return The farthest point from the reference point in the dataset.
   */
  private[algorithm] def findFarthestPoint[A](dataset: Array[DataPoint[A]], referencePoint: DataPoint[A])
                                             (implicit m: Metric[A]): DataPoint[A] = {

    var maxDistance = Float.MinValue
    var farthestPoint: DataPoint[A] = null

    for (point <- dataset if point != referencePoint) {
      val distance = referencePoint.distance(point)
      if (distance > maxDistance) {
        maxDistance = distance
        farthestPoint = point
      }
    }

    require(farthestPoint != null, "No valid farthest point found")
    farthestPoint
  }

}
