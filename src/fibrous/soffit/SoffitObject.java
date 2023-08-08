/* BSD 3-Clause License
 *
 * Copyright (c) 2023, Noah McLean
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package fibrous.soffit;

import java.util.LinkedList;

public class SoffitObject {
	
	private SoffitObject parent = null;
	
	private String type;
	private String name;
	
	private LinkedList<SoffitObject> objects;
	private LinkedList<SoffitField> fields;
	
	private int level = -1;
	
	/**
	 * Constructs a SoffitObject with a specified type and name.
	 * @param type
	 * @param name
	 */
	public SoffitObject(String type, String name) {
		this.type = type;
		this.name = name;
		
		objects = new LinkedList<>();
		fields = new LinkedList<>();
	}
	
	/**
	 * Returns the type of this object.
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Returns the name of this object.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Renames this object.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Re-types this object.
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Returns the first instance of an object with a name that matches objectName.
	 * According to SOFFIT conventions, multiple SoffitObjects may be named the same.
	 * Only the first instance is returned in this case.
	 * Returns null if no matching object is found.
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
	 * According to SOFFIT conventions, multiple SoffitFields may be named the same.
	 * Only the first instance is returned in this case.
	 * Returns null if no matching field is found.
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
	 * Returns a LinkedList containing all fields with a name matching fieldName.
	 * According to SOFFIT conventions, multiple SoffitFields may be named the same.
	 * Returns an empty linked list if no objects are found.
	 * @param fieldName
	 * @return
	 */
	public LinkedList<SoffitField> getFieldsByName(String fieldName) {
		
		LinkedList<SoffitField> foundFields = new LinkedList<>();
		
		for(int i = 0; i < fields.size(); i++) {
			if(fields.get(i).getName().compareTo(fieldName) == 0) {
				foundFields.add(fields.get(i));
			}
		}

		return foundFields;
	}
	
	/**
	 * Returns a LinkedList containing all objects with a name matching objectsName.
	 * According to SOFFIT conventions, multiple SoffitObjects may be named the same.
	 * Returns an empty linked list if no objects are found.
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

		return foundObjects;
	}
	
	/**
	 * Returns a LinkedList containing all objects of a type matching objectsType.
	 * Returns an empty LinkedList if no matching objects are found.
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
		
		return foundObjects;
	}
	
	/**
	 * Removes the first occurrence of an object with a name matching the passed String.
	 * @param name
	 */
	public void removeObject(String name) {
		for(int i = 0; i < objects.size(); i++) {
			if(objects.get(i).getName().compareTo(name) == 0) {
				objects.remove(i);
				return;
			}
		}
	}
	
	/**
	 * Removes all objects of a type matching the passed String.
	 * @param name
	 */
	public void removeObjectsByType(String type) {
		for(int i = 0; i < objects.size(); i++) {
			if(objects.get(i).getType().compareTo(type) == 0) {
				objects.remove(i);
			}
		}
	}
	
	/**
	 * Removes the first occurrence of a field with a name matching the passed String.
	 * @param name
	 */
	public void removeField(String name) {
		for(int i = 0; i < fields.size(); i++) {
			if(fields.get(i).getName().compareTo(name) == 0) {
				fields.remove(i);
				return;
			}
		}
	}
	
	
	
	/**
	 * Removes all objects contained within this object.
	 */
	public void removeAllObjects() {
		objects.clear();
	}
	
	/**
	 * Removes all fields contained within this object.
	 */
	public void removeAllFields() {
		fields.clear();
	}
	
	/**
	 * Returns a LinkedList containing all SoffitObjects within this object.
	 * Returns an empty LinkedList if this object contains no other objects.
	 * @return
	 */
	public LinkedList<SoffitObject> getAllObjects() {
		return objects;
	}
	
	/**
	 * Returns a LinkedList containing all SoffitFields within this object.
	 * Returns an empty LinkedList if this object contains no SoffitFields.
	 * @return
	 */
	public LinkedList<SoffitField> getAllFields() {
		return fields;
	}
	
	/**
	 * Adds a SoffitObject as a child object.
	 * @param object
	 */
	public void add(SoffitObject object) {
		object.setParent(this);
		objects.add(object);
	}
	
	/**
	 * Adds a SoffitField to this object.
	 * @param field
	 */
	public void add(SoffitField field) {
		field.setParent(this);
		fields.add(field);
	}
	
	/**
	 * Returns true if this object contains objects.
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
	 * Returns null if it is directly attached to the root object.
	 * @return
	 */
	public SoffitObject getParent() {
		return parent;
	}
	
	/**
	 * Returns how deeply nested this object is.
	 * Returns 0 if this object is attached directly to the root object.
	 * @return
	 */
	public int getNestedLevel() {
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
	
	/**
	 * Returns this objects fully qualified name.
	 * I.e, it lists this objects ancestry.
	 * Each object is separated by a forward slash (/).
	 * If an object is unnamed/anonymous, then the object type is substituted for the object name, and it is annotated as "(anon)".
	 * According to SOFFIT conventions, multiple objects may have the same name and type, so this may not be useful in some circumstances.
	 */
	@Override
	public String toString() {
		String path;
		String tempPath;
		
		if(name == null)
			tempPath = "(anon)_" + type;
		else
			tempPath = name;
		
		if(parent != null) {
			if(parent.isRoot())
				path = name;
			else
				path = parent.toString() + "/" + tempPath;
		} else {
			path = "";
		}
		
		return path;
	}
	
	/**
	 * Sets the parent of this SoffitObject.
	 * This is really only called internally when adding an object to another object.
	 * @param parent
	 */
	protected void setParent(SoffitObject parent) {
		this.parent = parent;
		calcNestingLevel(this.parent);
	}
	
	/**
	 * Calculates how deeply nested this field is.
	 * This is really only called internally when adding a field to another object.
	 * @param parent
	 */
	protected void calcNestingLevel(SoffitObject parent) {
		
		//I would like to change this in the future as this operation could get slow with large trees.
		//It runs through everything attached to it and its children whenever something is added to itself.
		//It's a necessary evil at this point though.
		
		if(parent == null)
			level = 0;
		else {
			level = parent.getNestedLevel() + 1;
			
			//Calculate child field nesting level
			for(SoffitField childFields : fields)
				childFields.calcNestingLevel(this);
			
			//Calculate child object nesting level
			for(SoffitObject childObjects : objects)
				childObjects.calcNestingLevel(this);
		}
	}
}
