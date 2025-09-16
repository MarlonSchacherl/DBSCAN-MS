package algorithm

import model.{DataPoint, Graph, LABEL}

case object CCGMA {
  def apply(mergingCandidates: Array[DataPoint]): Graph[DataPoint] = {
    val localResults: Map[Long, Array[DataPoint]] = mergingCandidates.groupBy(_.id)

    var graph = Graph[DataPoint]()
    for ((id, localClusters) <- localResults) {
      // TODO: Needs to be optimized, see also Graph class.
      if (localClusters.length >= 2) {
        for (i <- localClusters.indices) {
          if (localClusters(i).label == LABEL.CORE) {
            graph = graph.addVertex(localClusters(i))
            for (j <- i + 1 until localClusters.length) {
              graph = graph.addVertex(localClusters(j))
              graph = graph.addEdge(localClusters(i), localClusters(j))
            }
          } else {
            for (j <- i + 1 until localClusters.length) {
              if (localClusters(j).label == LABEL.CORE) {
                graph = graph.addVertex(localClusters(i))
                graph = graph.addVertex(localClusters(j))
                graph = graph.addEdge(localClusters(i), localClusters(j))
              }
            }
          }
        }
      }
    }
    graph
  }
}
