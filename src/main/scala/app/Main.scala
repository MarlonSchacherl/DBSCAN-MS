package app

import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("DBSCAN-MS")
      .config("spark.local.dir", "S:\\temp")
      .master("local[14]") // * for all cores
      .config("spark.driver.memory", "4g").config("spark.driver.maxResultSize", "15g")
      .config("spark.executor.memory", "8g").getOrCreate()

//    DBSCANMS.run(spark)

    spark.stop()
  }
}
