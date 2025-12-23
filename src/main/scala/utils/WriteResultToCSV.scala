package utils

import model.DataPoint


import java.io.{FileWriter, BufferedWriter}

case object WriteResultToCSV {
  def apply(result: Array[DataPoint[Array[Float]]], filename: String): Unit = {
    val file = new FileWriter(filename)
    val writer = new BufferedWriter(file)
    val output = result.map(p => (p.data, p.globalCluster))
    try {
      output.foreach { case (coordinates, label) =>
        val coordsString = coordinates.mkString(",")
        writer.write(s"$coordsString,$label\n")
      }
    } finally {
      writer.close()
      file.close()
    }
  }
}
