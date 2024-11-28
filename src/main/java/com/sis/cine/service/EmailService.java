package com.sis.cine.service;

import com.sis.cine.dto.EntradaDigitalDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Envía un correo con las entradas digitales
     */
    public void enviarEntradasDigitales(String emailDestino, List<EntradaDigitalDTO> entradas)
            throws MessagingException {
        Context context = new Context(new Locale("es"));
        context.setVariable("entradas", entradas);
        context.setVariable("fechaEmision",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        String contenido = templateEngine.process("emails/entradas-digitales", context);

        MimeMessage mensaje = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setTo(emailDestino);
        helper.setSubject("Tus entradas para " + entradas.get(0).getTituloPelicula());
        helper.setText(contenido, true);

        // Adjuntar cada QR como imagen inline
        for (int i = 0; i < entradas.size(); i++) {
            EntradaDigitalDTO entrada = entradas.get(i);
            if (entrada.getCodigoQR() != null) {
                helper.addInline("qr-code-" + i,
                        new ByteArrayResource(Base64.getDecoder().decode(entrada.getCodigoQR())),
                        "image/png");
            }
        }

        emailSender.send(mensaje);
    }

    /**
     * Envía recordatorio de pago pendiente
     */
    public void enviarRecordatorioPago(String emailDestino, String nombrePelicula,
                                       LocalDateTime fechaExpiracion, String montoPendiente) throws MessagingException {
        Context context = new Context(new Locale("es"));
        context.setVariable("nombrePelicula", nombrePelicula);
        context.setVariable("fechaExpiracion",
                fechaExpiracion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        context.setVariable("montoPendiente", montoPendiente);

        String contenido = templateEngine.process("emails/recordatorio-pago", context);

        MimeMessage mensaje = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setTo(emailDestino);
        helper.setSubject("¡Completa tu pago para " + nombrePelicula + "!");
        helper.setText(contenido, true);

        emailSender.send(mensaje);
    }

    /**
     * Envía confirmación de pago y entradas
     */
    public void enviarConfirmacionPago(String emailDestino, List<EntradaDigitalDTO> entradas,
                                       String montoTotal) throws MessagingException {
        Context context = new Context(new Locale("es"));
        context.setVariable("entradas", entradas);
        context.setVariable("montoTotal", montoTotal);
        context.setVariable("fechaCompra",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        String contenido = templateEngine.process("emails/confirmacion-pago", context);

        MimeMessage mensaje = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setTo(emailDestino);
        helper.setSubject("Confirmación de compra - " + entradas.get(0).getTituloPelicula());
        helper.setText(contenido, true);

        emailSender.send(mensaje);
    }
}