package org.example.ass3;

import java.util.*;

public class PrimMST {

    public static MSTResult findMST(Graph graph, String graphId, String graphName) {
        long startTime = System.currentTimeMillis();
        long operationCount = 0;

        Set<String> visited = new HashSet<>();
        List<Edge> mstEdges = new ArrayList<>();
        double totalCost = 0.0;

        if (graph.vertices.isEmpty()) {
            return new MSTResult(graphId, graphName, 0, 0, 0, 0, 0);
        }

        String start = graph.vertices.get(0);
        visited.add(start);

        while (visited.size() < graph.vertices.size()) {
            Edge minEdge = null;
            double minWeight = Double.MAX_VALUE;

            for (Edge edge : graph.edges) {
                operationCount++;
                if (visited.contains(edge.from) && !visited.contains(edge.to) && edge.weight < minWeight) {
                    minWeight = edge.weight;
                    minEdge = edge;
                } else if (visited.contains(edge.to) && !visited.contains(edge.from) && edge.weight < minWeight) {
                    minWeight = edge.weight;
                    minEdge = new Edge(edge.to, edge.from, edge.weight);
                }
            }

            if (minEdge == null) break;

            visited.add(minEdge.to);
            mstEdges.add(minEdge);
            totalCost += minEdge.weight;
        }

        long endTime = System.currentTimeMillis();

        return new MSTResult(
                graphId,
                graphName,
                graph.getVertexCount(),
                mstEdges.size(),
                endTime - startTime,
                operationCount,
                totalCost
        );
    }
}
