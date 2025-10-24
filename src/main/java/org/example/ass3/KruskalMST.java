package org.example.ass3;

import java.util.*;

public class KruskalMST {

    static class DisjointSet {
        private final Map<String, String> parent = new HashMap<>();

        public void makeSet(List<String> vertices) {
            for (String v : vertices) parent.put(v, v);
        }

        public String find(String item) {
            if (parent.get(item).equals(item)) return item;
            String root = find(parent.get(item));
            parent.put(item, root); // path compression
            return root;
        }

        public void union(String set1, String set2) {
            parent.put(find(set1), find(set2));
        }
    }

    public static MSTResult findMST(Graph graph, String graphId, String graphName) {
        long startTime = System.currentTimeMillis();
        long operationCount = 0;

        List<Edge> mstEdges = new ArrayList<>();
        double totalCost = 0.0;

        // сортируем рёбра по весу
        List<Edge> sortedEdges = new ArrayList<>(graph.edges);
        sortedEdges.sort(Comparator.comparingDouble(e -> e.weight));

        DisjointSet ds = new DisjointSet();
        ds.makeSet(graph.vertices);

        for (Edge edge : sortedEdges) {
            operationCount++;
            String root1 = ds.find(edge.from);
            String root2 = ds.find(edge.to);

            if (!root1.equals(root2)) {
                mstEdges.add(edge);
                totalCost += edge.weight;
                ds.union(root1, root2);
            }

            if (mstEdges.size() == graph.vertices.size() - 1)
                break;
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
