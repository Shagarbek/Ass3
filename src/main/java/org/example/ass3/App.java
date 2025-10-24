package org.example.ass3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class App {

    public static void main(String[] args) throws IOException {
        System.out.println("=== MST Optimization Started ===");

        String inputDir = "input";
        String outputJson = "output/output.json";
        String outputCsv = "output/results.csv";

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
                    "%s,%s,%d,%d,%s,%d,%d,%.4f\n",
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
}
