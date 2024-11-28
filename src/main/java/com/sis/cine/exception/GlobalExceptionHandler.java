package com.sis.cine.exception;

import com.sis.cine.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {



    // Agrega más manejadores de excepciones según sea necesario

    @ExceptionHandler(AsientosNoDisponiblesException.class)
    public ResponseEntity<ErrorResponse> handleAsientosNoDisponiblesException(AsientosNoDisponiblesException ex) {
        ErrorResponse error = new ErrorResponse(
                "Error de Disponibilidad",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PagoExpiradoException.class)
    public ResponseEntity<ErrorResponse> handlePagoExpiradoException(PagoExpiradoException ex) {
        ErrorResponse error = new ErrorResponse(
                "Error de Pago",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.GONE);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "Recurso No Encontrado",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}