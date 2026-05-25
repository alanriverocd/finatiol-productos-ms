package com.finatiol.productos.exception;

import com.finatiol.common.exception.ResourceNotFoundException;
import com.finatiol.common.constants.productos.ErrorCodes;
import com.finatiol.common.constants.productos.ErrorMessages;
import com.finatiol.productos.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCodes.PRODUCTO_NO_ENCONTRADO, ex.getMessage()));
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> handleProductoNoEncontrado(
            ProductoNoEncontradoException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCodes.PRODUCTO_NO_ENCONTRADO, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidacion(
            MethodArgumentNotValidException ex) {

        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCodes.VALIDACION_FALLIDA, detalle));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCodes.ERROR_INTERNO, ErrorMessages.ERROR_INTERNO));
    }
}
