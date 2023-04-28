package fibrous.soffit;

public class SoffitField {
	private SoffitObject parent = null;
	
	private String name;
	private String value;
	
	private int level = -1;
	
	public SoffitField(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Returns the parent object of this field.
	 * Returns null if it attached to the root object.
	 * @return
	 */
	public SoffitObject getParent() {
		return parent;
	}
	
	/**
	 * Returns how deeply nested this field is.
	 * Returns 0 if this field is attached to the root object.
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
	
	protected void setParent(SoffitObject parent) {
		this.parent = parent;
		calcNestingLevel(this.parent);
	}
	
	protected void calcNestingLevel(SoffitObject parent) {
		if(parent != null) {
			level++;
			calcNestingLevel(parent.getParent());
		}
	}
}
