package fibrous.soffit;

import java.lang.reflect.Field;

import fibrous.soffit.SoffitException;
import fibrous.soffit.SoffitField;
import fibrous.soffit.SoffitObject;

public abstract class SimpleSerialSoffitObject {
	
	/**
	 * Returns a serialized version of this instance as a {@link SoffitObject}.
	 * Currently only functional on objects that only have primitives and Strings as fields.
	 * Override this method and {@link deserialize()} for bespoke solutions.
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public SoffitObject serialize() throws IllegalArgumentException, IllegalAccessException {		
		Field[] fields = this.getClass().getDeclaredFields();
		
		//Initializes an object with the type matching the java object type.
		SoffitObject serializedObject = new SoffitObject(this.getClass().getTypeName());
		
		for(int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			//Need this for out-of-package access.
			field.setAccessible(true);
			
			SoffitField soffitField = new SoffitField(field.getName(), field.get(this).toString());
			serializedObject.add(soffitField);
		}
		
		return serializedObject;
	}
	
	/**
	 * Configures this object based on the {@link SoffitObject} that is passed as an input.
	 * Currently only functional on objects that only have primitives and Strings as fields.
	 * Override this method and {@link serialize()} for bespoke solutions.
	 * @param serializedObject
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 */
	public void deserialize(SoffitObject serializedObject) throws IllegalArgumentException, IllegalAccessException, SecurityException {
		String javaObjectType = this.getClass().getTypeName();
		String soffitObjectType = serializedObject.getType();
		
		//Check for same object type...
		if(javaObjectType.compareTo(soffitObjectType) != 0)
			throw new SoffitException("Cannot deserialize from " + soffitObjectType + " to " + javaObjectType);		
		
		Field[] fields = this.getClass().getDeclaredFields();
		
		for(int i = 0; i < fields.length; i++) {
			String value = serializedObject.getAllFields().get(i).getValue();
			Field field = fields[i];
			//Need this for out-of-package access.
			field.setAccessible(true);
			
			if(field.getType() == String.class)
				field.set(this, value);
			else
				setFieldAsANumber(field, value);
		}
	}
	
	private void setFieldAsANumber(Field field, String value) throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
		
		Class<?> numberType = field.getType();
		
		switch(numberType.getName()) {
		case "byte":
			field.set(this, Byte.parseByte(value));
			break;
			
		case "short":
			field.set(this, Short.parseShort(value));
			break;
		
		case "int":
			field.set(this, Integer.parseInt(value));
			break;
			
		case "long":
			field.set(this, Long.parseLong(value));
			break;
			
		case "float":
			field.set(this, Float.parseFloat(value));
			break;
			
		case "double":
			field.set(this, Double.parseDouble(value));
			break;
			
		case "boolean":
			field.set(this, Boolean.parseBoolean(value));
			break;
			
		case "char":
			field.set(this, value.charAt(0));
			break;
			
		default:
			throw new SoffitException("Can only automatically deserialize primitives and Strings");	
		}
	}
}
