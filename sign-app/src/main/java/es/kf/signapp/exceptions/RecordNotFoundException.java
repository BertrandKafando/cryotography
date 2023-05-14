package es.kf.signapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecordNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RecordNotFoundException() {
		super("La ressource que vous cherchez n'existe pas");
	}

	public RecordNotFoundException(String message) {
		super(message);
	}
}
