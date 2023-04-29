package fibrous.soffit;

import java.util.LinkedList;

public class SoffitObject {
	
	private SoffitObject parent = null;
	
	private String type;
	private String name;
	
	private LinkedList<SoffitObject> objects;
	private LinkedList<SoffitField> fields;
	
	private int level = -1;
	
	public SoffitObject(String type, String name) {
		this.type = type;
		this.name = name;
		
		objects = new LinkedList<>();
		fields = new LinkedList<>();
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the first instance of an object with a name that matches objectName.
	 * Returns null if the object cannot be found.
	 * @param objectName
	 * @return
	 */
	public SoffitObject getObject(String objectName) {
		for(int i = 0; i < objects.size(); i++) {
			if(objects.get(i).getName().compareTo(objectName) == 0) {
				return objects.get(i);
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * Returns the first instance of a field with a name that matches fieldName.
	 * Returns null if the object cannot be found.
	 * @param fieldName
	 * @return
	 */
	public SoffitField getField(String fieldName) {
		for(int i = 0; i < fields.size(); i++) {
			if(fields.get(i).getName().compareTo(fieldName) == 0) {
				return fields.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a LinkedList containing all objects with a name matching objectsName.
	 * Returns null if no object are found.
	 * @param objectsName
	 * @return
	 */
	public LinkedList<SoffitObject> getObjectsByName(String objectsName) {
		
		LinkedList<SoffitObject> foundObjects = new LinkedList<>();
		
		for(int i = 0; i < objects.size(); i++) {
			if(objects.get(i).getName().compareTo(objectsName) == 0) {
				foundObjects.add(objects.get(i));
			}
		}
		
		if(foundObjects.isEmpty())
			return null;
		else
			return foundObjects;
	}
	
	/**
	 * Returns a LinkedList containing all objects of a type matching objectsType.
	 * Returns null if no object are found.
	 * @param objectsName
	 * @return
	 */
	public LinkedList<SoffitObject> getObjectsByType(String objectsType) {
		
		LinkedList<SoffitObject> foundObjects = new LinkedList<>();
		
		for(int i = 0; i < objects.size(); i++) {
			if(objects.get(i).getType().compareTo(objectsType) == 0) {
				foundObjects.add(objects.get(i));
			}
		}
		
		if(foundObjects.isEmpty())
			return null;
		else
			return foundObjects;
	}
	
	/**
	 * Returns a LinkedList containing all objects within this object.
	 * @return
	 */
	public LinkedList<SoffitObject> getAllObjects() {
		return objects;
	}
	
	/**
	 * Returns a LinkedList containing all fields within this object.
	 * @return
	 */
	public LinkedList<SoffitField> getAllFields() {
		return fields;
	}
	
	public void addObject(SoffitObject object) {
		object.setParent(this);
		objects.add(object);
	}
	
	public void addField(SoffitField field) {
		field.setParent(this);
		fields.add(field);
	}
	
	/**
	 * Returns true if this object contains more objects.
	 * @return
	 */
	public boolean containsObjects() {
		return !objects.isEmpty();
	}
	
	/**
	 * Returns true if this object contains fields.
	 * @return
	 */
	public boolean containsFields() {
		return !fields.isEmpty();
	}
	
	/**
	 * Returns the parent object of this object.
	 * Returns null if it attached to the root object.
	 * @return
	 */
	public SoffitObject getParent() {
		return parent;
	}
	
	/**
	 * Returns how deeply nested this object is.
	 * Returns 0 if this object is attached to the root object.
	 * @return
	 */
	public int getNestingLevel() {
		return level;
	}
	
	/**
	 * Returns true if this object was parsed as the root object from a stream.
	 * I.e., if the nesting level of this object is -1, then this is the root object.
	 * @return
	 */
	public boolean isRoot() {
		return (level == -1);
	}
	
	protected void setParent(SoffitObject parent) {
		this.parent = parent;
		calcNestingLevel(this.parent);
	}
	
	protected void calcNestingLevel(SoffitObject parent) {
		//Calculate for yourself first
		if(parent != null) {
			level++;
			calcNestingLevel(parent.getParent());
		}
		
		//Calculate for your children next
		for(SoffitObject childObject : objects) {
			childObject.calcNestingLevel(parent);
		}
		
		for(SoffitField childField : fields) {
			childField.calcNestingLevel(parent);
		}
	}
}
