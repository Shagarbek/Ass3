package org.example.ass3;

import java.util.List;

public class GraphData {
    public String name;
    public List<String> vertices;
    public List<EdgeData> edges;

    public static class EdgeData {
        public String from;
        public String to;
        public double weight;
    }
}
