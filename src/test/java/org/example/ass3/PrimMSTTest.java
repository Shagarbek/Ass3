package org.example.ass3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrimMSTTest {

    @Test
    void testSimpleGraphMST() {
        Graph graph = new Graph();
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 3);
        graph.addEdge("B", "C", 1);
        graph.addEdge("B", "D", 2);
        graph.addEdge("C", "D", 4);
        graph.addEdge("D", "E", 2);
        graph.addEdge("E", "F", 6);

        MSTResult result = PrimMST.findMST(graph, "G1", "Prim");

        assertEquals(5, result.edges);
        assertTrue(result.totalCost > 0);
        assertTrue(result.executeTime >= 0);
    }
}
