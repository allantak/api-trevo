package br.com.jacto.trevo.config.exception;

import br.com.jacto.trevo.config.exception.dto.Error400Dto;
import br.com.jacto.trevo.config.exception.dto.Error409;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity errorHandler404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity errorHandler400(MethodArgumentNotValidException exception) {
        var erros = exception.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(Error400Dto::new).collect(Collectors.toList()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity errorHandler409(DataIntegrityViolationException exception) {
        var duplicate = exception.getCause();
        return ResponseEntity.status(409).body(new Error409(duplicate.getCause()));
    }

}
