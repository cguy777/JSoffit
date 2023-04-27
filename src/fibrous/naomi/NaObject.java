package fibrous.naomi;

import java.util.LinkedList;

public class NaObject {
	
	private NaObject parent;
	
	private String type;
	private String name;
	
	private LinkedList<NaObject> objects;
	private LinkedList<NaField> fields;
	
	private int level = -1;
	
	public NaObject(String type, String name, NaObject parent) {
		this.type = type;
		this.name = name;
		
		objects = new LinkedList<>();
		fields = new LinkedList<>();
		
		this.parent = parent;
		
		calcNestingLevel(this.parent);
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
	public NaObject getObject(String objectName) {
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
	public NaField getField(String fieldName) {
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
	public LinkedList<NaObject> getObjectsByName(String objectsName) {
		
		LinkedList<NaObject> foundObjects = new LinkedList<>();
		
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
	public LinkedList<NaObject> getObjectsByType(String objectsType) {
		
		LinkedList<NaObject> foundObjects = new LinkedList<>();
		
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
	public LinkedList<NaObject> getAllObjects() {
		return objects;
	}
	
	/**
	 * Returns a LinkedList containing all fields within this object.
	 * @return
	 */
	public LinkedList<NaField> getAllFields() {
		return fields;
	}
	
	public void addObject(NaObject object) {
		objects.add(object);
	}
	
	public void addField(NaField field) {
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
	public NaObject getParent() {
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
	
	private void calcNestingLevel(NaObject parent) {
		if(parent != null) {
			level++;
			calcNestingLevel(parent.getParent());
		}
	}
}
