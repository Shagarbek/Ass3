package org.example.ass3;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class App {

    public static void main(String[] args) throws IOException {
        System.out.println("=== MST Optimization Started ===");

        String inputDir = URLDecoder.decode(
                Objects.requireNonNull(App.class.getClassLoader().getResource("input")).getPath(),
                StandardCharsets.UTF_8
        );
        if (inputDir.startsWith("/")) {
            inputDir = inputDir.substring(1);
        }

        System.out.println("Input directory resolved to: " + inputDir);

        String outputJson = "output/output.json";
        String outputCsv = "output/results.csv";
        String benchmarkCsv = "output/benchmark.csv";


        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        List<MSTResult> results = new ArrayList<>();

        Files.createDirectories(Paths.get("output"));

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inputDir), "*.json")) {
            for (Path path : stream) {
                System.out.println("Processing file: " + path.getFileName());
                processFile(path.toFile(), results);
            }
        }

        mapper.writeValue(new File(outputJson), results);
        System.out.println("Results saved to: " + outputJson);

        saveResultsToCSV(results, outputCsv);
        System.out.println("CSV summary saved to: " + outputCsv);
        runBenchmark(inputDir, benchmarkCsv);
        System.out.println("Benchmark completed!");

        System.out.println("=== Done ===");
    }

    private static void processFile(File file, List<MSTResult> results) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        GraphData[] graphDataList = mapper.readValue(file, GraphData[].class);

        for (int i = 0; i < graphDataList.length; i++) {
            GraphData data = graphDataList[i];
            String graphId = file.getName().replace(".json", "") + "_G" + (i + 1);
            Graph graph = new Graph();

            for (GraphData.EdgeData edge : data.edges) {
                graph.addEdge(edge.from, edge.to, edge.weight);
            }

            MSTResult primResult = PrimMST.findMST(graph, graphId, data.name + " (Prim)");
            results.add(primResult);

            MSTResult kruskalResult = KruskalMST.findMST(graph, graphId, data.name + " (Kruskal)");
            results.add(kruskalResult);

            if (Math.abs(primResult.totalCost - kruskalResult.totalCost) > 1e-9) {
                System.out.println("Warning: MST costs differ for " + graphId);
            }
        }
    }

    private static void saveResultsToCSV(List<MSTResult> results, String outputCsv) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph ID,Graph Name,Vertices,Edges,Algorithm,Execution Time (ms),Operations,Total Cost\n");

        for (MSTResult r : results) {
            sb.append(String.format(
                    "%s,%s,%d,%d,%s,%d ms,%d,%.4f\n",
                    r.id,
                    r.name,
                    r.vertices,
                    r.edges,
                    r.name.contains("Prim") ? "Prim" : "Kruskal",
                    r.executeTime,
                    r.operationCount,
                    r.totalCost
            ));

        }

        Files.write(Paths.get(outputCsv), sb.toString().getBytes());
    }
    private static void runBenchmark(String inputDir, String benchmarkCsv) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Dataset,Graph Count,Algorithm,Avg Time (ms),Avg Operations,Total Cost\n");

        ObjectMapper mapper = new ObjectMapper();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inputDir), "*.json")) {
            for (Path path : stream) {
                GraphData[] graphDataList = mapper.readValue(path.toFile(), GraphData[].class);
                Map<String, List<Long>> timeMap = new HashMap<>();
                Map<String, List<Long>> opMap = new HashMap<>();
                Map<String, Double> costMap = new HashMap<>();

                for (GraphData data : graphDataList) {
                    Graph graph = new Graph();
                    for (GraphData.EdgeData edge : data.edges) {
                        graph.addEdge(edge.from, edge.to, edge.weight);
                    }

                    for (int i = 0; i < 5; i++) {
                        MSTResult prim = PrimMST.findMST(graph, data.name, data.name + " (Prim)");
                        MSTResult kruskal = KruskalMST.findMST(graph, data.name, data.name + " (Kruskal)");

                        timeMap.computeIfAbsent("Prim", k -> new ArrayList<>()).add(prim.executeTime);
                        timeMap.computeIfAbsent("Kruskal", k -> new ArrayList<>()).add(kruskal.executeTime);
                        opMap.computeIfAbsent("Prim", k -> new ArrayList<>()).add(prim.operationCount);
                        opMap.computeIfAbsent("Kruskal", k -> new ArrayList<>()).add(kruskal.operationCount);
                        costMap.put("Prim", prim.totalCost);
                        costMap.put("Kruskal", kruskal.totalCost);
                    }
                }

                for (String algo : List.of("Prim", "Kruskal")) {
                    double avgTime = timeMap.get(algo).stream().mapToLong(Long::longValue).average().orElse(0);
                    double avgOps = opMap.get(algo).stream().mapToLong(Long::longValue).average().orElse(0);
                    double cost = costMap.get(algo);

                    sb.append(String.format("%s,%d,%s,%.2f ms,%.0f,%.4f\n",
                            path.getFileName(),
                            graphDataList.length,
                            algo,
                            avgTime,
                            avgOps,
                            cost
                    ));

                }
            }
        }

        Files.write(Paths.get(benchmarkCsv), sb.toString().getBytes());
        System.out.println("Benchmark results saved to: " + benchmarkCsv);
    }
}
