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
import com.sis.cine.dto.EntradaDigitalDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class QRCodeGenerator {

    @Value("${app.qr.secret-key:tu_clave_secreta_aqui}")
    private String secretKey;

    /**
     * Verifica el contenido de un QR
     * @param qrContent Contenido del QR en formato JSON
     * @return true si el contenido es válido y no ha sido manipulado
     */
    public boolean verifyQRContent(String qrContent) {
        try {
            // Deserializar el contenido del QR
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> qrData = mapper.readValue(qrContent, Map.class);

            // Obtener y remover la firma para verificación
            String receivedSignature = (String) qrData.remove("signature");
            if (receivedSignature == null) {
                return false;
            }

            // Verificar fecha de expiración
            if (qrData.containsKey("expirationTime")) {
                LocalDateTime expirationTime = LocalDateTime.parse(qrData.get("expirationTime").toString());
                if (LocalDateTime.now().isAfter(expirationTime)) {
                    return false;
                }
            }

            // Generar firma para comparación
            String calculatedSignature = generateSignature(qrData);

            // Verificar firma
            return receivedSignature.equals(calculatedSignature);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera una firma digital para los datos del QR
     */
    private String generateSignature(Map<String, Object> data) throws NoSuchAlgorithmException {
        // Ordenar datos para consistencia
        TreeMap<String, Object> sortedData = new TreeMap<>(data);

        // Crear string con datos ordenados
        StringBuilder dataString = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedData.entrySet()) {
            if (!entry.getKey().equals("signature")) {
                dataString.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append(";");
            }
        }

        // Añadir clave secreta
        dataString.append(secretKey);

        // Generar hash SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(dataString.toString().getBytes(StandardCharsets.UTF_8));

        // Retornar hash en Base64
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Genera el contenido del QR incluyendo la firma de seguridad
     */
    public String generateQRContent(Map<String, Object> data) throws NoSuchAlgorithmException {
        try {
            // Añadir timestamp y fecha de expiración
            data.put("timestamp", LocalDateTime.now().toString());
            data.put("expirationTime", LocalDateTime.now().plusHours(24).toString());

            // Generar firma
            String signature = generateSignature(data);
            data.put("signature", signature);

            // Convertir a JSON
            return new ObjectMapper().writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Error generando contenido QR", e);
        }
    }

    /**
     * Genera código QR como imagen Base64
     */
    public String generateQRCodeBase64(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(
                content,
                BarcodeFormat.QR_CODE,
                300,
                300,
                hints
        );

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    // Utilidad para verificar entrada específica
    public boolean verifyEntradaQR(String qrContent, Long entradaId, Long proyeccionId) {
        try {
            if (!verifyQRContent(qrContent)) {
                return false;
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> qrData = mapper.readValue(qrContent, Map.class);

            // Verificar que los IDs coincidan
            return entradaId.equals(Long.valueOf(qrData.get("entradaId").toString())) &&
                    proyeccionId.equals(Long.valueOf(qrData.get("proyeccionId").toString()));

        } catch (Exception e) {
            return false;
        }
    }
    public String generateEntradaQRContent(EntradaDigitalDTO entrada) throws Exception {
        // Crear mapa con datos esenciales de la entrada
        Map<String, Object> qrData = new HashMap<>();

        // Identificadores únicos
        qrData.put("entradaId", entrada.getId());
        qrData.put("reservaId", entrada.getReservaId());
        qrData.put("proyeccionId", entrada.getProyeccionId());

        // Información de la entrada
        qrData.put("pelicula", entrada.getTituloPelicula());
        qrData.put("sala", entrada.getNombreSala());
        qrData.put("asiento", entrada.getAsientoCompleto());
        qrData.put("fecha", entrada.getFechaProyeccion().toString());

        // Metadatos de seguridad
        qrData.put("timestamp", LocalDateTime.now().toString());
        qrData.put("fechaEmision", entrada.getFechaEmision().toString());

        // Generar firma de seguridad
        String signature = generateSecureSignature(qrData);
        qrData.put("signature", signature);

        // Convertir a JSON
        return new ObjectMapper().writeValueAsString(qrData);
    }
    private String generateSecureSignature(Map<String, Object> data) throws Exception {
        // Ordenar datos para consistencia
        StringBuilder dataToSign = new StringBuilder();
        data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (!entry.getKey().equals("signature")) {
                        dataToSign.append(entry.getKey())
                                .append("=")
                                .append(entry.getValue())
                                .append(";");
                    }
                });

        // Añadir clave secreta
        dataToSign.append(secretKey);

        // Generar hash SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(dataToSign.toString().getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hash);
    }

}