package com.tpi.backend.msrutas.dto.osrm;

public class Route {
    private double distance;  // distancia en metros
    private double duration;  // duración en segundos

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}