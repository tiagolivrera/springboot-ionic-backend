package com.tiagolivrera.cursomc.resources.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {
    public static final long serialVersionUID = 1L;

    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Integer status, String msg, Long timestamp) {
        super(status, msg, timestamp);
    }

    public void addError(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }

    public List<FieldMessage> getErrors() { // O nome 'Errors' é convertido em JSON e exibido no log
        return errors;
    }
}
