package app

import algorithm.DBSCAN_MS
import org.apache.spark.sql.SparkSession
import testutils.GetResultLabels.printClusters

import scala.util.{Failure, Success, Try}

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 10 && args.length != 7) {
      Console.err.println(
        s"""
           |Invalid number of arguments.
           |Usage: <filepath:String> <epsilon:Float> <minPts:Int> <numberOfPivots:Int> <numberOfPartitions:Int> <samplingDensity:Float> <seed:Int> [<dataHasHeader:Boolean> <dataHasRightLabel:Boolean> <collectResults:Boolean>]
           |
           |Example:
           |  spark-submit --class app.Main target/dbscanms-assembly.jar data/input.csv 0.5 5 10 8 0.01 42 true false true
           |Or, if all boolean arguments are false, they can be omitted:
           |  spark-submit --class app.Main target/dbscanms-assembly.jar data/input.csv 0.5 5 10 8 0.01 42
         """.stripMargin)
      System.exit(1)
    }

    // Parse arguments
    val maybeArgs = if (args.length == 10) {
      for {
        filepath          <- Try(args(0))
        epsilon           <- Try(args(1).toFloat)
        minPts            <- Try(args(2).toInt)
        numberOfPivots    <- Try(args(3).toInt)
        numberOfPartitions<- Try(args(4).toInt)
        samplingDensity   <- Try(args(5).toFloat)
        seed              <- Try(args(6).toInt)
        dataHasHeader     <- Try(args(7).toBoolean)
        dataHasRightLabel <- Try(args(8).toBoolean)
        collectResults    <- Try(args(9).toBoolean)
      } yield (filepath, epsilon, minPts, numberOfPivots, numberOfPartitions, samplingDensity, seed, dataHasHeader, dataHasRightLabel, collectResults)
    } else {
      for {
        filepath          <- Try(args(0))
        epsilon           <- Try(args(1).toFloat)
        minPts            <- Try(args(2).toInt)
        numberOfPivots    <- Try(args(3).toInt)
        numberOfPartitions<- Try(args(4).toInt)
        samplingDensity   <- Try(args(5).toFloat)
        seed              <- Try(args(6).toInt)
      } yield (filepath, epsilon, minPts, numberOfPivots, numberOfPartitions, samplingDensity, seed, false, false, false)
    }

    maybeArgs match {
      case Success((filepath, epsilon, minPts, numberOfPivots, numberOfPartitions, samplingDensity, seed, dataHasHeader, dataHasRightLabel, collectResults)) =>
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
             |Data Has Header:      $dataHasHeader
             |Data Has RightLabel:  $dataHasRightLabel
             |Collect Results:      $collectResults
           """.stripMargin)

        val spark = SparkSession.builder().appName("DBSCAN-MS").getOrCreate()
        Console.out.println("spark.local.dir = " + spark.sparkContext.getConf.get("spark.local.dir", "unset"))
        Console.out.println("java.io.tmpdir  = " + System.getProperty("java.io.tmpdir"))

        if (collectResults) {
          try {
            val start = System.nanoTime()

            val data = DBSCAN_MS.runFromFile(spark,
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
          finally {
            spark.stop()
          }
        }
        else {
          try {
            DBSCAN_MS.runWithoutCollect(spark,
              filepath,
              epsilon,
              minPts,
              numberOfPivots,
              numberOfPartitions,
              samplingDensity,
              seed,
              dataHasHeader,
              dataHasRightLabel)
          }
          finally {
            spark.stop()
          }
        }

      case Failure(ex) =>
        Console.err.println(s"Error parsing arguments: ${ex.getMessage}")
        System.exit(1)
    }
  }
}
