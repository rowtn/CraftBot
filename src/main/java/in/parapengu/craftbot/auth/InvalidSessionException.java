package in.parapengu.craftbot.auth;

public class InvalidSessionException extends RuntimeException {

	private static final long serialVersionUID = 1493555339716298885L;

	public InvalidSessionException(String message) {
		super(message);
	}

}
