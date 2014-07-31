package in.parapengu.craftbot.auth;

public class YggdrasilAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = 3103621797945414317L;

	private String error;
	private String errorMessage;
	private String errorCause;

	public YggdrasilAuthenticationException(String error, String errorMessage) {
		this(error, errorMessage, null);
	}

	public YggdrasilAuthenticationException(String error, String errorMessage, String errorCause) {
		super(error + ": " + errorMessage + (errorCause != null ? " (Caused by " + errorCause + ")" : ""));

		this.error = error;
		this.errorMessage = errorMessage;
		this.errorCause = errorCause;
	}

	public String getError() {
		return error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorCause() {
		return errorCause;
	}

}
