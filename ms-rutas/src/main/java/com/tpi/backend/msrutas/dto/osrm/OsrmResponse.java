package com.tpi.backend.msrutas.dto.osrm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsrmResponse {
    private String code;
    private List<Route> routes;
    private String message;
    
    public Double getDistance() {
        if (routes != null && !routes.isEmpty() && routes.get(0) != null) {
            return routes.get(0).getDistance();
        }
        return null;
    }
    
    public Double getDuration() {
        if (routes != null && !routes.isEmpty() && routes.get(0) != null) {
            return routes.get(0).getDuration();
        }
        return null;
    }
    
    public boolean isSuccess() {
        return "Ok".equalsIgnoreCase(code);
    }
}