package algorithm

import org.scalatest.funsuite.AnyFunSuite

class DBSCAN_MSTest extends AnyFunSuite{
  test("Test") {
    DBSCAN_MS.run("data/synth_data_points10000x3D.csv", epsilon = 0.03f, minPts = 3, numberOfPivots = 9, numberOfPartitions = 10, samplingDensity = 0.2f)
  }

  test("2D Clustering Test with synthetic data from Sci-Kit Learn") {
    DBSCAN_MS.run("data/dbscan_dataset_2400x2D.csv", epsilon = 0.03f, minPts = 3, numberOfPivots = 9, numberOfPartitions = 10, samplingDensity = 0.2f)
  }

}
