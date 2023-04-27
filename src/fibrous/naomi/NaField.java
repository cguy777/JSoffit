package fibrous.naomi;

public class NaField {
	private NaObject parent;
	
	private String name;
	private String value;
	
	private int level = -1;
	
	public NaField(String name, String value, NaObject parent) {
		this.name = name;
		this.value = "";
		
		//Strip quotations
		for(int i = 0; i < value.length() - 2; i++) {
			this.value += value.charAt(i + 1);
		}
		
		this.parent = parent;
		
		calcNestingLevel(this.parent);
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
	public NaObject getParent() {
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
	
	private void calcNestingLevel(NaObject parent) {
		if(parent != null) {
			level++;
			calcNestingLevel(parent.getParent());
		}
	}
}
