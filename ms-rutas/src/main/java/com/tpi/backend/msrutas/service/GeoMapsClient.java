package com.tpi.backend.msrutas.service;

import com.tpi.backend.msrutas.dto.osrm.OsrmResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service; // Cambiado a @Service
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import entities.Geolocalizacion; // Asumo que esta es tu entidad compartida
import java.net.URI;

@Service // Usamos @Service ya que es un componente de servicio
public class GeoMapsClient {

    private final RestTemplate restTemplate;
    
    // Configuraciones de OSRM (de application.properties)
    private final String osrmBaseUrl;
    private final String osrmRouteEndpoint; // /route/v1/driving/

    /**
     * Inyección del RestTemplate y las configuraciones de OSRM.
     * Es recomendable configurar el RestTemplate como Bean en una clase @Configuration.
     */
    public GeoMapsClient(RestTemplate restTemplate,
                         @Value("${osrm.base-url:http://osrm:5000}") String osrmBaseUrl,
                         @Value("${osrm.route-endpoint:/route/v1/driving/}") String osrmRouteEndpoint) {
        this.restTemplate = restTemplate;
        this.osrmBaseUrl = osrmBaseUrl;
        this.osrmRouteEndpoint = osrmRouteEndpoint;
    }

    /**
     * Consulta al servicio OSRM local para obtener la distancia y duración entre dos puntos.
     * @param origen Objeto Geolocalizacion del punto de origen.
     * @param destino Objeto Geolocalizacion del punto de destino.
     * @return OsrmResponse con la información de la ruta.
     */
    public OsrmResponse getRouteInfo(Geolocalizacion origen, Geolocalizacion destino) {
        
        if (origen == null || destino == null || origen.getLatitud() == null || destino.getLongitud() == null) {
            return null;
        }

        [cite_start]// Formato de OSRM: longitud,latitud;longitud,latitud [cite: 84]
        String coordinates = String.format("%.6f,%.6f;%.6f,%.6f", 
                                           origen.getLongitud(), origen.getLatitud(), 
                                           destino.getLongitud(), destino.getLatitud());

        URI uri = UriComponentsBuilder.fromHttpUrl(osrmBaseUrl + osrmRouteEndpoint + coordinates)
                                    [cite_start].queryParam("overview", "false") // Para respuesta más ligera [cite: 83]
                                    .build()
                                    .toUri();
        
        try {
            // Realiza la petición y mapea directamente a nuestro DTO OsrmResponse
            return restTemplate.getForObject(uri, OsrmResponse.class);
        } catch (Exception ex) {
            System.err.println("Error al consultar OSRM en: " + uri.toString() + " - " + ex.getMessage());
            return null;
        }
    }
    
    /* * NOTA: Eliminamos el método geocodeAddress, ya que la funcionalidad es de ruteo con OSRM. 
     * Si necesitas la geocodificación por dirección, deberías mantener la lógica de Google Maps 
     * en un cliente separado (ej. GoogleMapsClient) para no mezclar responsabilidades.
     */
}