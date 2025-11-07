package com.tpi.backend.msrutas.dto.osrm;

import java.util.List;
// Usa Lombok si lo tienes, si no, añade getters/setters/constructores manualmente.

public class OsrmResponse {
    private List<Route> routes;
    private String code;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}