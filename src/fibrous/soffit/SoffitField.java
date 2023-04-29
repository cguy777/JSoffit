package fibrous.soffit;

public class SoffitField {
	private SoffitObject parent = null;
	
	private String name;
	private String value;
	
	private int level = -1;
	
	/**
	 * Constructs a SoffitField with a specified name and value.
	 * @param name
	 * @param value
	 */
	public SoffitField(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the name/label of this SoffitField.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the value of this SoffitField.
	 * @return
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the value of this SoffitField.
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Returns the parent object of this field.
	 * Returns null if it is directly attached to the root object.
	 * @return
	 */
	public SoffitObject getParent() {
		return parent;
	}
	
	/**
	 * Returns how deeply nested this field is.
	 * Returns 0 if this field is attached directly to the root object.
	 * @return
	 */
	public int getNestingLevel() {
		return level;
	}
	
	/**
	 * Returns the value of the field.
	 */
	@Override
	public String toString() {
		return value;
	}
	
	/**
	 * Sets the parent of this SoffitField.
	 * It is really only called internally when adding a field to another object.
	 * @param parent
	 */
	protected void setParent(SoffitObject parent) {
		this.parent = parent;
		calcNestingLevel(this.parent);
	}
	
	/**
	 * Calculates how deeply nested this field is.
	 * It is really only called internally when adding a field to another object.
	 * @param parent
	 */
	protected void calcNestingLevel(SoffitObject parent) {
		if(parent == null)
			level = 0;
		else			
			level = parent.getNestedLevel() + 1;
	}
}
