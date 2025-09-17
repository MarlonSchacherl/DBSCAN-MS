package algorithm

import imp.MutualInformation.normalizedMutualInfoScore
import org.scalatest.funsuite.AnyFunSuite

import scala.io.Source
import scala.util.Using

class DBSCAN_MSTest extends AnyFunSuite{
  test("Test") {
    DBSCAN_MS.run("data/synth_data_points10000x3D.csv", epsilon = 0.03f, minPts = 3, numberOfPivots = 9, numberOfPartitions = 10, samplingDensity = 0.2f)
  }

  test("2D Clustering Test with synthetic data from Sci-Kit Learn") {
    val result = DBSCAN_MS.run("data/dbscan_dataset_100x2D.csv",
      epsilon = 2f,
      minPts = 5,
      numberOfPivots = 9,
      numberOfPartitions = 10,
      samplingDensity = 0.5f,
      dataHasHeader = true,
      dataHasRightLabel = true)

    val distinctResult = result.map(p => (p.id, p.globalCluster)).distinct
    val distinctClusters = distinctResult.filterNot(_._2 == -1)
    val newLabelsMapping = distinctClusters.map(_._2).distinct.zipWithIndex.toMap
    val remappedClusters = distinctClusters.map(p => {
      val newLabel = newLabelsMapping.get(p._2)
      newLabel match {
        case Some(newLabel) => newLabel
        case _ => throw new Exception("WTF")
      }
    })
    val finalResult = remappedClusters.concat(distinctResult.filter(_._2 == -1).map(_ => -1)).sorted

    println(s"Clusters: ${distinctClusters.map(_._2).distinct.zipWithIndex.mkString("Array(", ", ", ")")}")
    println(s"Reduced Length: ${distinctResult.length}")
    println(s"Remapped Length: ${remappedClusters.length}")
    //    println(s"Number of Points: ${numberOfPoints.length}")
    //    val numberOfPoints = result.map(_.id).distinct

    val checkingLabels = getRightmostColumn("data/dbscan_dataset_100x2D.csv").toArray.map(_.toFloat.toInt).sorted
    println(s"Normalized Mutual Information Score: ${normalizedMutualInfoScore(labelsTrue = checkingLabels, labelsPred = finalResult)}")

    val y = newLabelsMapping.toArray
    val x = remappedClusters.foldLeft(0)((acc, label) => {
      if (label != acc) {
        acc + 1
      } else {
        acc
      }
    })
  }




  def getRightmostColumn(filePath: String): Seq[String] = {
    Using(Source.fromFile(filePath)) { source =>
      source.getLines().drop(1).map(_.split(',').last).toSeq
    }.getOrElse(Seq.empty)
  }
}
