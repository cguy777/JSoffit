package fibrous.soffit;

/**
 * The main class for all SOFFIT related exceptions.
 * It is an extension of {@link RuntimeException}
 * @author Noah
 *
 */
public class SoffitException extends RuntimeException {
	SoffitException() {
		super();
	}
	
	SoffitException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message
	 * @param soffitLineNumber The line number of the stream where the issue was encountered.
	 */
	SoffitException(String message, int soffitLineNumber) {
		super("SOFFIT Stream, line " + String.valueOf(soffitLineNumber) + ": " + message);
	}
}
