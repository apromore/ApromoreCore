package de.vogella.algorithms.dijkstra.model;

public class Edge<T> {
    private final String id;
    private final Vertex<T> source;
    private final Vertex<T> destination;
    private final int weight;

    public Edge(String id, Vertex<T> source, Vertex<T> destination, int weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }

}