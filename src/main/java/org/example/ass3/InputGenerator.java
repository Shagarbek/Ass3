package org.example.ass3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class InputGenerator {

    public static void main(String[] args) throws IOException {
        generateGraphs("src/main/resources/input/input_small.json", 5, 5, 30);
        generateGraphs("src/main/resources/input/input_medium.json", 10, 30, 300);
        generateGraphs("src/main/resources/input/input_large.json", 10, 300, 1000);
        generateGraphs("src/main/resources/input/input_extra_large.json", 3, 1000, 2000);
        System.out.println("✅ All input JSON files generated successfully!");
    }

    private static void generateGraphs(String filename, int count, int minVertices, int maxVertices) throws IOException {
        Random rand = new Random();
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        List<Map<String, Object>> graphs = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            int verticesCount = rand.nextInt(maxVertices - minVertices + 1) + minVertices;
            List<String> vertices = new ArrayList<>();
            for (int v = 0; v < verticesCount; v++) {
                vertices.add("V" + v);
            }

            List<Map<String, Object>> edges = new ArrayList<>();
            for (int e = 0; e < verticesCount * 2; e++) { // плотность умеренная
                String from = vertices.get(rand.nextInt(verticesCount));
                String to = vertices.get(rand.nextInt(verticesCount));
                if (!from.equals(to)) {
                    double weight = 1 + rand.nextInt(1000);
                    edges.add(Map.of("from", from, "to", to, "weight", weight));
                }
            }

            graphs.add(Map.of(
                    "name", "Graph " + i,
                    "vertices", vertices,
                    "edges", edges
            ));
        }

        mapper.writeValue(new File(filename), graphs);
        System.out.println("Generated " + filename);
    }
}
