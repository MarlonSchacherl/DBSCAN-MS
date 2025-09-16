package model

// TODO: Possible optimization; maybe use GraphX, or work with variables instead of values.
case class Graph[V](adjacencyList: Map[V, List[V]] = Map[V, List[V]]()) {
  def addVertex(v: V): Graph[V] = {
    if (adjacencyList.contains(v)) this
    else Graph(adjacencyList + (v -> List()))
  }

  def addEdge(v1: V, v2: V): Graph[V] = {
    val updatedAdjList = adjacencyList.map {
      case (vertex, neighbors) if vertex == v1 => (vertex, v2 :: neighbors)
      case (vertex, neighbors) if vertex == v2 => (vertex, v1 :: neighbors)
      case (vertex, neighbors) => (vertex, neighbors)
    }
    Graph(updatedAdjList)
  }

  def neighbors(v: V): Option[List[V]] = adjacencyList.get(v)
}
