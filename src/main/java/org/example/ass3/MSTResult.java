package org.example.ass3;

public class MSTResult {
    public String id;
    public String name;
    public int vertices;
    public int edges;
    public long executeTime;   // миллисекунды
    public long operationCount;
    public double totalCost;

    public MSTResult(String id, String name, int vertices, int edges, long executeTime, long operationCount, double totalCost) {
        this.id = id;
        this.name = name;
        this.vertices = vertices;
        this.edges = edges;
        this.executeTime = executeTime;
        this.operationCount = operationCount;
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "MSTResult{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", vertices=" + vertices +
                ", edges=" + edges +
                ", executeTime=" + executeTime +
                ", operationCount=" + operationCount +
                ", totalCost=" + totalCost +
                '}';
    }
}
