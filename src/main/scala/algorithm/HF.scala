package algorithm

import model.DataPoint

import scala.util.Random

object HF {
  /** Selects pivot candidates using the Hull Foci (HF) algorithm.
   *
   * @param dataset The sampled dataset from which to select pivot candidates.
   * @param numberOfPivotCandidates The number of pivot candidates to select.
   * @param distanceFunction The distance function to use for distance calculations.
   * @param seed Random seed for reproducibility.
   * @return An array of selected pivot candidates.
   */
  def apply(dataset: Array[DataPoint],
                            numberOfPivotCandidates: Int,
                            distanceFunction: (Array[Float], Array[Float]) => Float,
                            seed: Int): Array[DataPoint] = {

    val rng = new Random(seed)
    val pivotCandidates = new Array[DataPoint](numberOfPivotCandidates)
    val startingPoint = dataset(rng.nextInt(dataset.length))

    pivotCandidates(0) = findFarthestPoint(dataset, startingPoint, distanceFunction)
    pivotCandidates(1) = findFarthestPoint(dataset, pivotCandidates(0), distanceFunction)

    val edge = pivotCandidates(0).distance(pivotCandidates(1), distanceFunction)

    // TODO: This is inefficient because we're computing distances to pivot candidates multiple times.
    for (i <- 2 until numberOfPivotCandidates) {
      var minimalError = Float.MaxValue
      var bestCandidate: DataPoint = null

      for (point <- dataset if !pivotCandidates.contains(point)) {
        var error = 0.0f
        for (pivot <- pivotCandidates.take(i)) {
          error += Math.abs(edge - point.distance(pivot, distanceFunction))
        }

        if (error < minimalError) {
          minimalError = error
          bestCandidate = point
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
   * @param distanceFunction The distance function to use for distance calculations.
   * @return The farthest point from the reference point in the dataset.
   */
  private[algorithm] def findFarthestPoint(dataset: Array[DataPoint],
                                           referencePoint: DataPoint,
                                           distanceFunction: (Array[Float], Array[Float]) => Float): DataPoint = {

    var maxDistance = Float.MinValue
    var farthestPoint: DataPoint = null

    for (point <- dataset if point != referencePoint) {
      val distance = referencePoint.distance(point, distanceFunction)
      if (distance > maxDistance) {
        maxDistance = distance
        farthestPoint = point
      }
    }

    require(farthestPoint != null, "No valid farthest point found")
    farthestPoint
  }

}
