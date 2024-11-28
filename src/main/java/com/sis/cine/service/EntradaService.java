package com.sis.cine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sis.cine.dto.EntradaDigitalDTO;
import com.sis.cine.exception.ResourceNotFoundException;
import com.sis.cine.model.*;
import com.sis.cine.repository.AsientoRepository;
import com.sis.cine.repository.EntradaRepository;
import com.sis.cine.repository.ProyeccionRepository;
import com.sis.cine.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EntradaService {
    @Autowired
    private EntradaRepository entradaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ProyeccionRepository proyeccionRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    /**
     * Genera entradas digitales para una reserva pagada
     */
    public List<EntradaDigitalDTO> generarEntradasDigitales(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        List<Entrada> entradas = entradaRepository.findByReservaId(reservaId);
        List<EntradaDigitalDTO> entradasDigitales = new ArrayList<>();

        for (Entrada entrada : entradas) {
            try {
                EntradaDigitalDTO entradaDigital = convertirAEntradaDigitalDTO(entrada);
                String qrContent = qrCodeGenerator.generateEntradaQRContent(entradaDigital);
                String qrCodeBase64 = qrCodeGenerator.generateQRCodeBase64(qrContent);
                entradaDigital.setCodigoQR(qrCodeBase64);

                entradasDigitales.add(entradaDigital);
            } catch (Exception e) {
                throw new RuntimeException("Error generando QR para entrada: " + entrada.getId(), e);
            }
        }

        return entradasDigitales;
    }

    private EntradaDigitalDTO convertirAEntradaDigitalDTO(Entrada entrada) {
        EntradaDigitalDTO dto = new EntradaDigitalDTO();

        // Datos básicos
        dto.setId(entrada.getId());
        dto.setReservaId(entrada.getReserva().getId());

        // Datos de proyección
        Proyeccion proyeccion = entrada.getReserva().getProyeccion();
        dto.setProyeccionId(proyeccion.getId());
        dto.setFechaProyeccion(proyeccion.getDia().atTime(proyeccion.getComienzo()));
        dto.setFormato(proyeccion.getTipoProyeccion().getNombre());

        // Datos de película
        Pelicula pelicula = proyeccion.getPelicula();
        dto.setPeliculaId(pelicula.getId());
        dto.setTituloPelicula(pelicula.getTitulo());
        dto.setPosterUrl(pelicula.getPoster());

        // Datos de sala y asiento
        Sala sala = proyeccion.getSala();
        Asiento asiento = entrada.getAsiento();
        dto.setNombreSala(sala.getNombre());
        dto.setTipoSala(sala.getTipoSala().getNombre());
        dto.setAsientoFila(asiento.getFila());
        dto.setAsientoNumero(asiento.getNumero());
        dto.setAsientoCompleto(asiento.getFila() + asiento.getNumero());

        // Datos de precio
        dto.setPrecio(entrada.getPrecio().getPrecioFinal());
        dto.setTipoPrecio(entrada.getPrecio().getNombre());

        // Metadatos
        dto.setFechaEmision(LocalDateTime.now());
        dto.setEstado("ACTIVA");

        return dto;
    }

    /**
     * Valida una entrada digital mediante su QR
     */
    public boolean validarEntradaDigital(String qrContent) {
        // Primero verificar la integridad del QR
        if (!qrCodeGenerator.verifyQRContent(qrContent)) {
            return false;
        }

        try {
            Map<String, Object> qrData = new ObjectMapper().readValue(qrContent, Map.class);
            Long entradaId = Long.parseLong(qrData.get("id").toString());
            Long proyeccionId = Long.parseLong(qrData.get("proyeccionId").toString());

            return entradaRepository.isEntradaValida(entradaId, proyeccionId);
        } catch (Exception e) {
            return false;
        }
    }
}