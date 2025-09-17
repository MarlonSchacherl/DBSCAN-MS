package utils

import model.DataPoint
import org.scalatest.funsuite.AnyFunSuite

case class WriteResultToCSVTest() extends AnyFunSuite {
  val x: DataPoint = DataPoint(Array(1.0f, 2.0f), id = 0)
  x.globalCluster = 1

  val y: DataPoint = DataPoint(Array(3.0f, 4.0f), id = 1)
  y.globalCluster = 2

  val z: DataPoint = DataPoint(Array(5.0f, 6.0f), id = 3)
  z.globalCluster = 1

  val result = Array(x, y, z)
  WriteResultToCSV(result, "test.csv")
}
