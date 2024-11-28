package com.sis.cine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sis.cine.dto.EntradaDigitalDTO;
import com.sis.cine.dto.ReservaResponseDTO;
import com.sis.cine.exception.AsientosNoDisponiblesException;
import com.sis.cine.exception.PagoExpiradoException;
import com.sis.cine.exception.ResourceNotFoundException;
import com.sis.cine.exception.UnauthorizedException;
import com.sis.cine.model.*;
import com.sis.cine.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class ReservaService {
    @Value("${app.reserva.expiracion:10}")
    private int TIEMPO_EXPIRACION_MINUTOS;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private EntradaRepository entradaRepository;

    @Autowired
    private ProyeccionRepository proyeccionRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private PreciosRepository preciosRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);
    /**
     * Crea una nueva reserva temporal con tiempo límite de pago
     */
    public ReservaResponseDTO crearReservaTemporal(Long proyeccionId, List<String> asientos, Long usuarioId) {
        // Validar parámetros de entrada
        if (proyeccionId == null || asientos == null || asientos.isEmpty() || usuarioId == null) {
            throw new IllegalArgumentException("Parámetros de reserva inválidos");
        }

        // Verificar disponibilidad de asientos
        if (!verificarDisponibilidadAsientos(proyeccionId, asientos)) {
            throw new AsientosNoDisponiblesException("Algunos asientos ya no están disponibles");
        }

        try {
            // Obtener proyección
            Proyeccion proyeccion = proyeccionRepository.findById(proyeccionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Proyección no encontrada"));

            // Obtener usuario
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Crear la reserva
            Reserva reserva = new Reserva();
            reserva.setProyeccion(proyeccion);
            reserva.setFechaReserva(LocalDateTime.now());
            reserva.setUsuario(usuario);

            // Guardar la reserva
            Reserva reservaGuardada = reservaRepository.save(reserva);

            // Crear el registro de pago pendiente
            Pago pago = new Pago();
            pago.setReserva(reservaGuardada);
            pago.setFechaGeneracion(LocalDateTime.now());
            pago.setFechaExpiracion(LocalDateTime.now().plusMinutes(TIEMPO_EXPIRACION_MINUTOS));
            //pago.setFechaPago(LocalDateTime.now());
            pago.setEstadoPago("PENDIENTE");
            pago.setMonto(calcularMontoTotal(proyeccionId, asientos.size()));

            // Generar código QR para pago
            String codigoPago = generarCodigoPago(reservaGuardada.getId());
            pago.setDatosQr(codigoPago);
            pago = pagoRepository.save(pago);

            // Crear entradas para cada asiento
            List<Entrada> entradas = new ArrayList<>();
            for (String asientoRef : asientos) {
                String[] parts = asientoRef.split("(?<=\\D)(?=\\d)");
                Asiento asiento = asientoRepository.findBySalaIdAndFilaAndNumero(
                        proyeccion.getSala().getId(),
                        parts[0],
                        parts[1]
                ).orElseThrow(() -> new ResourceNotFoundException("Asiento no encontrado: " + asientoRef));

                Entrada entrada = new Entrada();
                entrada.setAsiento(asiento);
                entrada.setReserva(reservaGuardada);
                entrada.setPrecio(obtenerPrecioAplicable(proyeccion, usuario));
                entradas.add(entrada);
            }

            entradaRepository.saveAll(entradas);

            return construirReservaResponse(reservaGuardada, pago, asientos);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si los asientos están disponibles para una proyección
     */
    private boolean verificarDisponibilidadAsientos(Long proyeccionId, List<String> asientos) {
        try {
            // Obtener la proyección y su sala
            Proyeccion proyeccion = proyeccionRepository.findById(proyeccionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Proyección no encontrada"));

            Long salaId = proyeccion.getSala().getId();
            log.info("Verificando asientos para Sala ID: {}", salaId);

            // Obtener asientos ocupados de una sola vez
            List<Long> asientosOcupados = entradaRepository.findAsientosOcupadosByProyeccionId(proyeccionId);

            for (String asientoRef : asientos) {
                // Normalizar el formato del asiento (ejemplo: "A1" -> "A01")
                String[] parts = asientoRef.split("(?<=\\D)(?=\\d)");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Formato de asiento inválido: " + asientoRef);
                }

                String fila = parts[0].toUpperCase().trim();
                // Asegurarse de que el número tenga dos dígitos
                String numero = String.format("%02d", Integer.parseInt(parts[1].trim()));

                log.info("Verificando asiento - Fila: '{}', Número: '{}'", fila, numero);

                // Buscar el asiento en la base de datos
                Asiento asiento = asientoRepository.findBySalaIdAndFilaAndNumero(salaId, fila, numero)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                String.format("Asiento %s%s no encontrado", fila, numero)));

                // Verificar si está ocupado
                if (asientosOcupados.contains(asiento.getId())) {
                    throw new AsientosNoDisponiblesException(
                            String.format("El asiento %s%s ya está ocupado", fila, numero));
                }

                log.info("Asiento {} {} disponible", fila, numero);
            }
            return true;
        } catch (Exception e) {
            log.error("Error verificando disponibilidad de asientos: {}", e.getMessage());
            throw e;
        }
    }
    /**
     * Calcula el monto total para una reserva
     */
    private BigDecimal calcularMontoTotal(Long proyeccionId, int cantidadAsientos) {
        Proyeccion proyeccion = proyeccionRepository.findById(proyeccionId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyección no encontrada"));

        // Obtener el precio base para el tipo de sala y proyección
        Precios precio = preciosRepository.findByTipoSalaIdAndTipoProyeccionIdAndDiaSemana(
                proyeccion.getSala().getTipoSala().getId(),
                proyeccion.getTipoProyeccion().getId(),
                proyeccion.getDia().getDayOfWeek().toString()
        ).orElseThrow(() -> new ResourceNotFoundException("Precio no encontrado"));

        return precio.getPrecioFinal().multiply(BigDecimal.valueOf(cantidadAsientos));
    }

    /**
     * Genera un código único para el pago
     */
    private String generarCodigoPago(Long reservaId) {
        return String.format("PAY-%s-%d",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE),
                reservaId
        );
    }

    /**
     * Programa la verificación de expiración de la reserva
     */
    private void programarVerificacionExpiracion(Long reservaId, LocalDateTime fechaExpiracion) {
        // Aquí podrías implementar la lógica para programar la verificación
        // Por ejemplo, usando Spring Scheduler o una cola de mensajes
    }

    /**
     * Construye la respuesta de la reserva
     */
    private ReservaResponseDTO construirReservaResponse(Reserva reserva, Pago pago, List<String> asientos) {
        ReservaResponseDTO response = new ReservaResponseDTO();
        response.setReservaId(reserva.getId());
        response.setFechaExpiracion(pago.getFechaExpiracion());
        response.setCodigoPago(pago.getDatosQr());
        response.setEstadoPago(EstadoPago.valueOf(pago.getEstadoPago()));
        response.setAsientosSeleccionados(asientos);
        response.setMontoTotal(pago.getMonto().doubleValue());
        return response;
    }

    /**
     * Procesa el pago de una reserva
     */
    public List<EntradaDigitalDTO> procesarPago(Long reservaId, String comprobantePago) {
        Pago pago = pagoRepository.findByReservaId(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        if (pago.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new PagoExpiradoException("El tiempo para realizar el pago ha expirado");
        }

        // Validar comprobante y procesar pago
        pago.setEstadoPago("PAGADO");
        pago.setFechaPago(LocalDateTime.now());
        pagoRepository.save(pago);

        // Generar entradas
        List<EntradaDigitalDTO> entradas = generarEntradas(reservaId);

        // Enviar correo con las entradas
        try {
            emailService.enviarEntradasDigitales(
                    pago.getReserva().getUsuario().getEmail(),
                    entradas
            );
        } catch (Exception e) {
            // Log error pero no interrumpir la transacción
            e.printStackTrace();
        }

        return entradas;
    }

    /**
     * Genera las entradas para una reserva
     */
    private List<EntradaDigitalDTO> generarEntradas(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // Obtener los asientos reservados y generar entradas
        return entradaRepository.findByReservaId(reservaId)
                .stream()
                .map(entrada -> {
                    try {
                        EntradaDigitalDTO dto = convertirAEntradaDigital(entrada);
                        String qrContent = qrCodeGenerator.generateEntradaQRContent(dto);
                        dto.setCodigoQR(qrCodeGenerator.generateQRCodeBase64(qrContent));
                        return dto;
                    } catch (Exception e) {
                        throw new RuntimeException("Error generando entrada digital", e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entrada a DTO
     */
    private EntradaDigitalDTO convertirAEntradaDigital(Entrada entrada) {
        EntradaDigitalDTO dto = new EntradaDigitalDTO();
        dto.setId(entrada.getId());
        dto.setReservaId(entrada.getReserva().getId());

        Proyeccion proyeccion = entrada.getReserva().getProyeccion();
        dto.setProyeccionId(proyeccion.getId());
        dto.setFechaProyeccion(proyeccion.getDia().atTime(proyeccion.getComienzo()));

        Asiento asiento = entrada.getAsiento();
        dto.setAsientoCompleto(asiento.getFila() + asiento.getNumero());

        // ... establecer resto de campos

        return dto;
    }

    /**
     * Verifica la validez de una entrada
     */
    public boolean verificarEntrada(String qrContent) {
        // Primero verificar la integridad del QR
        if (!qrCodeGenerator.verifyQRContent(qrContent)) {
            return false;
        }

        try {
            // El QR contiene un JSON con la información de la entrada
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> qrData = mapper.readValue(qrContent, java.util.Map.class);

            Long entradaId = Long.valueOf(qrData.get("id").toString());
            Long proyeccionId = Long.valueOf(qrData.get("proyeccionId").toString());

            return entradaRepository.isEntradaValida(entradaId, proyeccionId);
        } catch (Exception e) {
            return false;
        }
    }
    public List<ReservaResponseDTO> obtenerReservasUsuario(Long usuarioId) {
        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);

        return reservas.stream()
                .map(reserva -> {
                    ReservaResponseDTO dto = new ReservaResponseDTO();
                    dto.setReservaId(reserva.getId());

                    // Buscar el pago asociado
                    Pago pago = pagoRepository.findByReservaId(reserva.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

                    dto.setFechaExpiracion(pago.getFechaExpiracion());
                    dto.setEstadoPago(EstadoPago.valueOf(pago.getEstadoPago()));
                    dto.setMontoTotal(pago.getMonto().doubleValue());

                    // Obtener asientos seleccionados
                    List<String> asientos = entradaRepository.findByReservaId(reserva.getId())
                            .stream()
                            .map(entrada -> entrada.getAsiento().getFila() + entrada.getAsiento().getNumero())
                            .collect(Collectors.toList());

                    dto.setAsientosSeleccionados(asientos);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las entradas digitales de una reserva específica
     */
    public List<EntradaDigitalDTO> obtenerEntradasReserva(Long reservaId, Long usuarioId) {
        // Verificar que la reserva pertenezca al usuario
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new UnauthorizedException("No tiene permiso para ver estas entradas");
        }

        // Verificar que el pago esté completado
        Pago pago = pagoRepository.findByReservaId(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        if (!pago.getEstadoPago().equals("PAGADO")) {
            throw new IllegalStateException("Las entradas no están disponibles. Pago pendiente.");
        }

        // Obtener las entradas
        List<Entrada> entradas = entradaRepository.findByReservaId(reservaId);

        // Convertir a DTOs
        return entradas.stream()
                .map(entrada -> {
                    try {
                        EntradaDigitalDTO dto = convertirAEntradaDigital(entrada);

                        // Generar QR para cada entrada
                        String qrContent = qrCodeGenerator.generateEntradaQRContent(dto);
                        dto.setCodigoQR(qrCodeGenerator.generateQRCodeBase64(qrContent));

                        return dto;
                    } catch (Exception e) {
                        throw new RuntimeException("Error generando entrada digital", e);
                    }
                })
                .collect(Collectors.toList());
    }
    /**
     * Obtiene el precio aplicable para una proyección específica basado en varios criterios
     * @param proyeccion La proyección para la cual se calculará el precio
     * @param usuario El usuario que realiza la reserva
     * @return El precio aplicable para la entrada
     */
    private Precios obtenerPrecioAplicable(Proyeccion proyeccion, Usuario usuario) {
        // Obtener el día de la semana de la proyección
        String diaSemana = proyeccion.getDia().getDayOfWeek().toString();

        // Obtener la sala y el tipo de proyección
        TipoSala tipoSala = proyeccion.getSala().getTipoSala();
        TipoProyeccion tipoProyeccion = proyeccion.getTipoProyeccion();

        // Agregar logs para debugging
        log.info("Buscando precio para: Sala Tipo={}, Proyección Tipo={}, Día={}",
                tipoSala.getNombre(), tipoProyeccion.getNombre(), diaSemana);

        // Buscar precio específico para el día, tipo de sala y tipo de proyección
        Optional<Precios> precioEspecifico = preciosRepository
                .findByTipoSalaIdAndTipoProyeccionIdAndDiaSemana(
                        tipoSala.getId(),
                        tipoProyeccion.getId(),
                        diaSemana
                );

        if (precioEspecifico.isPresent()) {
            log.info("Precio específico encontrado: {}", precioEspecifico.get().getPrecioFinal());
            return precioEspecifico.get();
        }

        // Si no hay precio específico, buscar precio por defecto
        List<Precios> preciosAplicables = preciosRepository.findByTipoSalaIdAndTipoProyeccionId(
                tipoSala.getId(),
                tipoProyeccion.getId()
        );

        if (preciosAplicables.isEmpty()) {
            log.error("No se encontró ningún precio para: Sala Tipo={}, Proyección Tipo={}",
                    tipoSala.getNombre(), tipoProyeccion.getNombre());
            throw new ResourceNotFoundException(String.format(
                    "No se encontró precio para sala tipo %s, proyección %s",
                    tipoSala.getNombre(),
                    tipoProyeccion.getNombre()
            ));
        }

        // Usar el primer precio disponible como default
        log.info("Usando precio por defecto: {}", preciosAplicables.get(0).getPrecioFinal());
        return preciosAplicables.get(0);
    }

}