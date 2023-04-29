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
}
