package org.example.ass3;
import java.util.ArrayList;
import java.util.List;

public class Graph {
    public List<String> vertices = new ArrayList<>();
    public List<Edge> edges = new ArrayList<>();

    public void addEdge(String from, String to, double weight) {
        edges.add(new Edge(from, to, weight));
        if (!vertices.contains(from)) vertices.add(from);
        if (!vertices.contains(to)) vertices.add(to);
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public List<Edge> getEdges() {
        return edges;
    }
}

