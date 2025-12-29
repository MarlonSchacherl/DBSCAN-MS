package app

import algorithm.LineParsers.{CsvFloatVectorParser, TextStringParser}
import algorithm.{DBSCAN_MS, DataFormats}
import org.apache.spark.sql.SparkSession
import testutils.GetResultLabels.printClusters
import utils.Metrics.{EuclideanArrayFloat, LevenshteinString}

import scala.util.{Failure, Success, Try}

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 11 && args.length != 9) {
      Console.err.println(
        s"""
           |Invalid number of arguments.
           |Usage: <filepath:String> <epsilon:Float> <minPts:Int> <numberOfPivots:Int> <numberOfPartitions:Int> <samplingDensity:Float> <seed:Int> <Format:String> [<metricsPath:String>] [<dataHasHeader:Boolean> <dataHasRightLabel:Boolean> <collectResults:Boolean>]
           |
           |Example:
           |  spark-submit --class app.Main target/dbscanms-assembly.jar data/input.csv 0.5 5 10 8 0.01 42 vector true false true
           |Or, if all boolean arguments are false, they can be omitted, but metrics path must be specified:
           |  spark-submit --class app.Main target/dbscanms-assembly.jar data/input.csv 0.5 5 10 8 0.01 42 txt metrics
         """.stripMargin)
      System.exit(1)
    }

    // Parse arguments
    val maybeArgs = if (args.length == 11) {
      for {
        filepath <- Try(args(0))
        epsilon <- Try(args(1).toFloat)
        minPts <- Try(args(2).toInt)
        numberOfPivots <- Try(args(3).toInt)
        numberOfPartitions <- Try(args(4).toInt)
        samplingDensity <- Try(args(5).toFloat)
        seed <- Try(args(6).toInt)
        format <- Try(args(7))
        dataHasHeader <- Try(args(8).toBoolean)
        dataHasRightLabel <- Try(args(9).toBoolean)
        collectResults <- Try(args(10).toBoolean)
      } yield (filepath, epsilon, minPts, numberOfPivots, numberOfPartitions, samplingDensity, seed, format, null, dataHasHeader, dataHasRightLabel, collectResults)
    } else if (args.length == 9) {
      for {
        filepath <- Try(args(0))
        epsilon <- Try(args(1).toFloat)
        minPts <- Try(args(2).toInt)
        numberOfPivots <- Try(args(3).toInt)
        numberOfPartitions <- Try(args(4).toInt)
        samplingDensity <- Try(args(5).toFloat)
        seed <- Try(args(6).toInt)
        format <- Try(args(7))
        metricsPath <- Try(args(8))
      } yield (filepath, epsilon, minPts, numberOfPivots, numberOfPartitions, samplingDensity, seed, format, metricsPath, false, false, false)
    } else {
      throw new IllegalArgumentException("Invalid number of arguments")
    }

    maybeArgs match {
      case Success((filepath, epsilon, minPts, numberOfPivots, numberOfPartitions, samplingDensity, seed, format, metricsPath, dataHasHeader, dataHasRightLabel, collectResults)) =>
        Console.out.println(
          s"""
             |--- DBSCAN-MS Configuration ---
             |Filepath:             $filepath
             |Epsilon:              $epsilon
             |MinPts:               $minPts
             |Number of Pivots:     $numberOfPivots
             |Number of Partitions: $numberOfPartitions
             |Sampling Density:     $samplingDensity
             |Seed:                 $seed
             |Format:               $format
             |Metrics Path:         $metricsPath
             |Data Has Header:      $dataHasHeader
             |Data Has RightLabel:  $dataHasRightLabel
             |Collect Results:      $collectResults
           """.stripMargin)

        val spark = SparkSession.builder().appName("DBSCAN-MS").getOrCreate()
        Console.out.println("spark.local.dir = " + spark.sparkContext.getConf.get("spark.local.dir", "unset"))
        Console.out.println("java.io.tmpdir  = " + System.getProperty("java.io.tmpdir"))

        try {
          if (format == DataFormats.Vector) {
            implicit val m: utils.Metric[Array[Float]] = EuclideanArrayFloat
            implicit val p: algorithm.LineParser[Array[Float]] = CsvFloatVectorParser
            if (collectResults) {
              val data = DBSCAN_MS.runFromFile[Array[Float]](spark,
                filepath,
                epsilon,
                minPts,
                numberOfPivots,
                numberOfPartitions,
                samplingDensity,
                seed,
                dataHasHeader,
                dataHasRightLabel)

              printClusters(data)
            }
            else {
              DBSCAN_MS.runWithoutCollect[Array[Float]](spark,
                filepath,
                epsilon,
                minPts,
                numberOfPivots,
                numberOfPartitions,
                samplingDensity, seed,
                metricsPath,
                dataHasRightLabel,
                dataHasHeader)
            }
          }
          else if (format == DataFormats.TxtString) {
            implicit val m: utils.Metric[String] = LevenshteinString
            implicit val p: algorithm.LineParser[String] = TextStringParser
            if (collectResults) {
              val data = DBSCAN_MS.runFromFile[String](spark,
                filepath,
                epsilon,
                minPts,
                numberOfPivots,
                numberOfPartitions,
                samplingDensity,
                seed,
                dataHasHeader,
                dataHasRightLabel)

              printClusters(data)
            }
            else {
              DBSCAN_MS.runWithoutCollect[String](spark,
                filepath,
                epsilon,
                minPts,
                numberOfPivots,
                numberOfPartitions,
                samplingDensity, seed,
                metricsPath,
                dataHasRightLabel,
                dataHasHeader)
            }
          }
          else {
            throw new IllegalArgumentException(s"Invalid format: $format")
          }
        }
        finally {
          spark.stop()
          System.gc()
          Thread.sleep(200)
        }

      case Failure(ex) =>
        Console.err.println(s"Error parsing arguments: ${ex.getMessage}")
        System.exit(1)
    }
  }
}
