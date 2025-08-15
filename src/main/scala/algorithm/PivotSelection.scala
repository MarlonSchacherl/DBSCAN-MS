package algorithm

import model.{DataPoint, DataPointVector}
import utils.Distance.euclidean

import scala.util.Random


case object PivotSelection {
  // Hull Foci Algorithm to find pivot candidates
  // TODO: FIX RETURN VALUE TO Array[DataPoint]
  private[algorithm] def HF(dataset: Array[DataPointVector],
                            numberOfPivotCandidates: Int,
                            distanceFunction: (Array[Float], Array[Float]) => Float,
                            seed: Int): Array[DataPointVector] = {

    val rng = new Random(seed)
    val pivotCandidates = new Array[DataPointVector](numberOfPivotCandidates)
    val startingPoint = dataset(rng.nextInt(dataset.length))

    pivotCandidates(0) = findFarthestPoint(dataset, startingPoint, distanceFunction)
    pivotCandidates(1) = findFarthestPoint(dataset, pivotCandidates(0), distanceFunction)

    val edge = distanceFunction(pivotCandidates(0).coordinates, pivotCandidates(1).coordinates)

    // TODO: This is inefficient because we're computing distances to pivot candidates multiple times.
    for (i <- 2 until numberOfPivotCandidates) {
      var minimalError = Float.MaxValue
      var bestCandidate: DataPointVector = null

      for (point <- dataset if !pivotCandidates.contains(point)) {
        var error = 0.0f
        for (pivot <- pivotCandidates.take(i)) {
          error += Math.abs(edge - distanceFunction(point.coordinates, pivot.coordinates))
        }

        if (error < minimalError) {
          minimalError = error
          bestCandidate = point
        }
      }

      pivotCandidates(i) = bestCandidate
    }


    /*
    pivotCandidates.indices.drop(2).foreach(i => {
      dataset.filter(point => !pivotCandidates.contains(point)).map(point => {
        (point, pivotCandidates.take(i).map(candidate => {
          Math.abs(edge - distanceFunction(point.coordinates, candidate.coordinates))
        }).sum)
      }).minBy(_._2) match {
        case (bestCandidate, _) => pivotCandidates(i) = bestCandidate
      }
    })
    */

    pivotCandidates
  }

  private[algorithm] def findFarthestPoint(dataset: Array[DataPointVector],
                                           referencePoint: DataPointVector,
                                           distanceFunction: (Array[Float], Array[Float]) => Float): DataPointVector = {

    var maxDistance = Float.MinValue
    var farthestPoint: DataPointVector = null

    for (point <- dataset if point != referencePoint) {
        val distance = distanceFunction(referencePoint.coordinates, point.coordinates)
        if (distance > maxDistance) {
          maxDistance = distance
          farthestPoint = point
      }
    }

    require(farthestPoint != null, "No valid farthest point found")
    farthestPoint
  }

  /**
   * Selects pivots using the Hull Foci Algorithm (HFI).
   *
   * @param dataset The dataset from which to select pivots.
   * @param numberOfPivots The number of pivots to select.
   * @param distanceFunction The distance function to use for distance calculations.
   * @param seed Random seed for reproducibility.
   */
  def HFI(dataset: Array[DataPointVector],
          numberOfPivots: Int = 40,
          distanceFunction: (Array[Float], Array[Float]) => Float = euclidean,
          seed: Int = Random.nextInt()): Unit = {
    require(dataset.nonEmpty, "Dataset must not be empty")
    require(numberOfPivots >= 2, "Number of pivots must be at least 2")
    require(numberOfPivots <= dataset.length, "Number of pivots must not exceed dataset size")

    val candidates = HF(dataset, numberOfPivots, distanceFunction, seed)
    val objectPairs = null // TODO: Choose object pairs
    var pivots = List[DataPoint]()

    for (i <- 0 until numberOfPivots) {
      var maxPrecision = Float.MinValue
      var bestCandidate: DataPointVector = null
      var bestCandidateIndex = -1

      for (j <- candidates.indices) {
        if (candidates(j) != null) {
          pivots = candidates(j) :: pivots //TODO: ERROR! candidates(j) is DataPointVector, not DataPoint

          val newPrecision = newPivotSetPrecision(objectPairs, pivots)
          if (newPrecision > maxPrecision) {
            maxPrecision = newPrecision
            bestCandidate = candidates(j)
            bestCandidateIndex = j
          }

          pivots = pivots.tail
        }
      }

      if (bestCandidate != null) {
        pivots = bestCandidate :: pivots //TODO: ERROR! SOME ASSIGNMENT IS WRONG, CANDIDATE IS DataPointVector, NOT DataPoint
        candidates(bestCandidateIndex) = null
      } else {
        throw new RuntimeException("No valid pivot candidate found")
      }
    }


  }

  /**
   * Computes the precision of the pivot set with the new pivot candidate.
   *
   * (This is precision(P) in "Efficient Metric Indexing for Similarity Search")
   * @param objectPairs An array of pairs of data points.
   * @param pivots The pivots used for mapping the data points to the vector space. The last pivot is the new pivot candidate.
   * @return The average precision of the pivot selection.
   */
  private[algorithm] def newPivotSetPrecision(objectPairs: Array[(DataPoint, DataPoint)], pivots: List[DataPoint]): Float = {
    objectPairs.map { case (a, b) =>
      L_infNorm(mapPointToVectorSpace(a, pivots), mapPointToVectorSpace(b, pivots)) / a.distance(b, euclidean)
    }.sum / objectPairs.length
  }

  /**
   * Computes the L-infinity norm (Chebyshev distance) between two data points.
   *
   * (This is D() in "Efficient Metric Indexing for Similarity Search")
   * @param a First data point.
   * @param b Second data point.
   * @return The L-infinity norm between the two data points.
   */
  private def L_infNorm(a: DataPointVector, b: DataPointVector): Float = {
    require(a.coordinates.length == b.coordinates.length, "Data points must have the same dimension")
//    a.data.zip(b.data).map { case (x, y) => Math.abs(x - y) }.max

    var maxDiff = Float.MinValue
    for (i <- a.coordinates.indices) {
      val diff = Math.abs(a.coordinates(i) - b.coordinates(i))
      if (diff > maxDiff) {
        maxDiff = diff
      }
    }
    maxDiff
  }

  /**
   * Maps a data point to a vector space defined by the given pivots.
   *
   * (This is ϕ(o) in "Efficient Metric Indexing for Similarity Search")
   * @param point The data point to map.
   * @param pivots The pivots defining the vector space.
   * @return A DataPointVector representing the coordinates in the vector space.
   */
  private def mapPointToVectorSpace(point: DataPoint, pivots: List[DataPoint]): DataPointVector = {
    val coordinates = pivots.map(pivot => pivot.distance(point, euclidean)).toArray
    DataPointVector(coordinates, point.id, point.label, point.visited)
  }
}
