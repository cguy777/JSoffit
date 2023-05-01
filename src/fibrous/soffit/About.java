package fibrous.soffit;

public class About {
	
	public static final String SOFFIT_VERSION_COMPLIANCE = "1.0.0";
	public static final String LIBRARY_VERSION = "0.1.0";
	
	/**
	 * Returns the version of SOFFIT that this library is compliant with.
	 * @return
	 */
	public static String GetSoffitCompliance() {
		return SOFFIT_VERSION_COMPLIANCE;
	}
	
	/**
	 * Returns the version number of this JSoffit library.
	 * @return
	 */
	public static String getVersion() {
		return LIBRARY_VERSION;
		
	}
	
}