package algorithm

import org.scalatest.funsuite.AnyFunSuite

class DBSCAN_MSTest extends AnyFunSuite{
  test("Basic 3D test") {
    DBSCAN_MS.run("data/synth_data_points10000x3D.csv", numberOfPartitions = 10, epsilon = 0.03f, numberOfPivots = 9, samplingDensity = 0.2f)
  }


}
