package es.kf.signapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class RecordAlreadyExistException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RecordAlreadyExistException() {
		super("La ressource que vous voulez créer existe déjà!");
	}

	public RecordAlreadyExistException(String message) {
		super(message);
	}
}
