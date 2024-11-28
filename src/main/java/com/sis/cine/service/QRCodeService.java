package com.sis.cine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sis.cine.dto.EntradaDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    /**
     * Genera un código QR como imagen en formato Base64
     * @param contenido El contenido a codificar en el QR
     * @param ancho Ancho de la imagen QR
     * @param alto Alto de la imagen QR
     * @return String en formato Base64 de la imagen QR
     */
    public String generarQRBase64(String contenido, int ancho, int alto) throws Exception {
        // Configurar los parámetros del código QR
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Alto nivel de corrección de errores
        hints.put(EncodeHintType.MARGIN, 2); // Margen alrededor del QR

        // Crear el escritor de QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                contenido,
                BarcodeFormat.QR_CODE,
                ancho,
                alto,
                hints
        );

        // Convertir la matriz a imagen y luego a Base64
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Genera contenido JSON para el QR de una entrada
     * @param entradaId ID de la entrada
     * @param proyeccionId ID de la proyección
     * @param asientoInfo Información del asiento
     * @return String con el contenido JSON codificado
     */
    public String generarContenidoQREntrada(Long entradaId, Long proyeccionId, String asientoInfo) throws JsonProcessingException {
        // Crear objeto con la información necesaria para validar la entrada
        Map<String, Object> datosEntrada = new HashMap<>();
        datosEntrada.put("id", entradaId);
        datosEntrada.put("proyeccion", proyeccionId);
        datosEntrada.put("asiento", asientoInfo);
        datosEntrada.put("timestamp", System.currentTimeMillis());

        // Generar una firma simple (en producción usar un sistema más robusto)
        String firma = generarFirma(datosEntrada);
        datosEntrada.put("firma", firma);

        return new ObjectMapper().writeValueAsString(datosEntrada);
    }

    /**
     * Genera una firma simple para validar la autenticidad del QR
     * En producción, usar un sistema de firma más seguro
     */
    private String generarFirma(Map<String, Object> datos) {
        String contenido = datos.get("id") + "|" +
                datos.get("proyeccion") + "|" +
                datos.get("asiento") + "|" +
                datos.get("timestamp");

        return Base64.getEncoder().encodeToString(
                contenido.getBytes()
        );
    }

    /**
     * Método de ejemplo para usar el servicio
     */
    public String generarQREntrada(EntradaDTO entrada) {
        try {
            // Generar el contenido del QR
            String contenidoQR = generarContenidoQREntrada(
                    entrada.getId(),
                    entrada.getReservaId(),
                    entrada.getAsientoCompleto()
            );

            // Generar el QR como imagen Base64
            return generarQRBase64(contenidoQR, 300, 300);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el código QR", e);
        }
    }
}