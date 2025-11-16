package com.tpi.backend.msflota.util;

import com.tpi.backend.msflota.dto.CamionDTO;
import com.tpi.backend.msflota.dto.TarifaDTO;
import com.tpi.backend.msflota.dto.TransportistaDTO;
import entities.Camion;
import entities.Tarifa;
import entities.Transportista;
import org.springframework.stereotype.Component;

@Component
public class FlotaMapper {

    // ---------- CAMION ----------
    public CamionDTO toCamionDTO(Camion e) {
        CamionDTO dto = new CamionDTO();

        // ID / dominio
        dto.setDominioCamion(e.getDominioCamion());

        // En la entidad no hay 'modelo' (lo dejamos null por ahora)
        dto.setModelo(null);

        // Capacidades
        dto.setCapacidadKg(e.getCapacidadPesoMax());
        dto.setVolumenM3(e.getCapacidadVolumenMax());

        // Disponibilidad y estado
        Boolean disp = e.getDisponibilidad();
        boolean disponible = disp != null && disp;
        dto.setDisponibilidad(disponible);

        // Consumo y costo
        dto.setConsumoPromKm(
                e.getConsumoPromKm() != null ? e.getConsumoPromKm() : 0f
        );
        dto.setCostoTraslado(
                e.getCostoTraslado() != null ? e.getCostoTraslado() : 0f
        );

        // Transportista (solo id)
        if (e.getTransportista() != null) {
            dto.setIdTransportista(e.getTransportista().getIdTransportista());
        } else {
            dto.setIdTransportista(null);
        }

        return dto;
    }


    public Camion toCamionEntity(CamionDTO dto) {
        Camion e = new Camion();

        e.setDominioCamion(dto.getDominioCamion());

        // Campos obligatorios de la entidad
        e.setCapacidadPesoMax(dto.getCapacidadKg());
        e.setCapacidadVolumenMax(dto.getVolumenM3());
        e.setConsumoPromKm(dto.getConsumoPromKm());
        e.setCostoTraslado(dto.getCostoTraslado());
        e.setDisponibilidad(dto.isDisponibilidad());

        // Transportista:
        e.setTransportista(null);

        return e;
    }


    // ---------- TRANSPORTISTA ----------
    public TransportistaDTO toTransportistaDTO(Transportista e) {
        TransportistaDTO dto = new TransportistaDTO();
        dto.setIdTransportista(e.getIdTransportista());
        dto.setNombre(e.getNombre());
        dto.setApellido(e.getApellido());
        dto.setDni(e.getDni());
        dto.setTelefono(e.getTelefono());
        // La entidad Transportista en common-data no contiene 'licencia' ni 'estado'
        dto.setLicencia(null);
        // Mapear 'activo' booleano a estado String
        Boolean activo = e.getActivo();
        dto.setEstado(activo != null && activo ? "activo" : "inactivo");
        return dto;
    }

    public Transportista toTransportistaEntity(TransportistaDTO dto) {
        Transportista e = new Transportista();
        e.setIdTransportista(dto.getIdTransportista());
        e.setNombre(dto.getNombre());
        e.setApellido(dto.getApellido());
        e.setDni(dto.getDni());
        e.setTelefono(dto.getTelefono());
        // No existe campo 'licencia' en la entidad común; dejamos email/fechaNacimiento/activo sin setear
        e.setActivo(dto.getEstado() != null && dto.getEstado().equalsIgnoreCase("activo"));
        return e;
    }

    // ---------- TARIFA ----------
    public TarifaDTO toTarifaDTO(Tarifa e) {
        TarifaDTO dto = new TarifaDTO();
        dto.setIdTarifa(e.getIdTarifa());
        dto.setTipoTarifa(e.getTipoTarifa());
        dto.setCostoLitroCombustible(e.getCostoLitroCombustible());
        dto.setCargoGestionTramo(e.getCargoGestionTramo());
        if (e.getCamion() != null) {
            dto.setDominioCamion(e.getCamion().getDominioCamion());
        }
        return dto;
    }

    public Tarifa toTarifaEntity(TarifaDTO dto) {
        Tarifa e = new Tarifa();
        e.setIdTarifa(dto.getIdTarifa());
        e.setTipoTarifa(dto.getTipoTarifa());
        e.setCostoLitroCombustible(dto.getCostoLitroCombustible());
        e.setCargoGestionTramo(dto.getCargoGestionTramo());
        // No seteamos Camion completo aquí, se puede resolver luego en el Service
        return e;
    }
}
