package fibrous.soffit;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

//*********************************************************
//String Object Framework For Information Transfer (SOFFIT)
//*********************************************************

public class SoffitUtil {
	
	public static final String SOFFIT_START = "__SoffitStart";
	public static final String SOFFIT_END = "__SoffitEnd";
	public static final char ESCAPE_SEQUENCE = '\\';
	
	private static int lineNumber = 0;
	
	/**
	 * Parses an {@link InputStream} as a root SOFFIT object.
	 * @param stream
	 * @return The SOFFIT root object as parsed from the InputStream.
	 * @throws SoffitException
	 */
	public static SoffitObject ReadStream(InputStream stream) throws SoffitException {
		lineNumber = 0;
		
		Scanner scanner = new Scanner(stream);
		SoffitObject root = new SoffitObject(null, null);
		
		//Look for __SoffitStart first
		String header = getLine(scanner);
		if(header.compareTo(SOFFIT_START) != 0)
			throw new SoffitException("SOFFIT header not found.");
		
		parseObject(scanner, root);
		
		scanner.close();
		return root;
	}
	
	/**
	 * Writes a SOFFIT object to an {@link OutputStream}.
	 * @param root
	 * @param output
	 */
	public static void WriteStream(SoffitObject root, OutputStream output) {
		BufferedOutputStream bStream = new BufferedOutputStream(output);
		
		//Write header
		byte[] lineBytes = convertLineToBytes(SOFFIT_START + "\n");
		try {
			bStream.write(lineBytes);
			bStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		writeObjects(root, bStream);
		
		//Write footer
		lineBytes = convertLineToBytes(SOFFIT_END);
		try {
			bStream.write(lineBytes);
			bStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeObjects(SoffitObject object, BufferedOutputStream bStream) {
		String line = null;
		byte[] lineBytes = null;
		
		//Write the fields first
		for(int i = 0; i < object.getAllFields().size(); i++) {			
			//Write the line containing a field...
			
			SoffitField field = object.getAllFields().get(i);
			
			try {
				bStream.write(convertFieldToLineBytes(field));
				bStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Write object declarations and then recursively write other objects
		for(int i = 0; i < object.getAllObjects().size(); i++) {
			
			SoffitObject currentObject = object.getAllObjects().get(i);
			//Write the line the object declaration...
			
			try {
				bStream.write(convertObjectDeclarationToLineBytes(currentObject));
				bStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Recursively write the next fields and object declarations.
			writeObjects(currentObject, bStream);
			
			//Write closing brackets.
			line = "";
			
			//Set indentation
			if(currentObject.getParent() != null) {
				for(int i2 = 0; i2 < currentObject.getNestedLevel(); i2++) {
					line += "\t";
				}	
			}
			
			line += "}\n";
			
			//Convert to byte array.
			lineBytes = convertLineToBytes(line);
			
			//Flush the output stream.
			try {
				bStream.write(lineBytes);
				bStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static byte[] convertLineToBytes(String line) {
		byte[] lineCharArray = new byte[line.length()];
		//Convert to byte array.
		for(int i = 0; i < line.length(); i++) {
			lineCharArray[i] = (byte) line.charAt(i);
		}

		return lineCharArray;
	}
	
	
	private static void parseObject(Scanner scanner, SoffitObject parent) throws SoffitException {
		
		while(true) {
			
			boolean isObject = false;
			boolean isField = false;
			String line;
			
			line = getLine(scanner);
			//If we didn't get anything, then break out.
			if(line == null)
				throw new SoffitException("Incomplete SOFFIT stream.");
			
			ArrayList<String> tokens = getLineTokens(line);
			
			//Check for end of object/closing curly bracket
			if(tokens.size() == 1 && tokens.get(0).compareTo("}") == 0) {
				if(!parent.isRoot())
					break;
				else
					throw new SoffitException("SOFFIT stream contained to many closing brackets.", lineNumber);
			}
			
			//Check for footer
			if(tokens.get(0).compareTo(SOFFIT_END) == 0) {
				//Check to see if it's possible to correctly end the stream at this point.
				if(!parent.isRoot())
					throw new SoffitException("SOFFIT footer encountered in non-root object.", lineNumber);
				
				break;
			}
			
			isField = isField(tokens);
			isObject = isObject(tokens);
			
			if(isField) {
				String name = tokens.get(0);
				
				String value = stripQuotations(tokens.get(1));
				
				//Check for proper escape sequences
				try {
					value = convertFromEscapeSequence(value);
				} catch (SoffitException e) {
					throw new SoffitException(e, lineNumber);
				}
				
				parent.add(new SoffitField(name, value));
			}
			
			if(isObject) {
				SoffitObject object;
				
				String type = tokens.get(0);
				
				if(tokens.size() == 2) {
					object = new SoffitObject(type, null);
				} else {
					String name = stripQuotations(tokens.get(1));
					
					//Check for proper escape sequences
					try {
						name = convertFromEscapeSequence(name);
					} catch (SoffitException e) {
						throw new SoffitException(e, lineNumber);
					}
					
					object = new SoffitObject(type, name);
				}
					
				parent.add(object);
				parseObject(scanner, object);
			}
			
			//Check for problems
			if(!isField && !isObject)
				throw new SoffitException("SOFFIT syntax error", lineNumber);
			
		}
	}
	
	private static ArrayList<String> getLineTokens(String s) {
		
		String line = s.strip();
		
		ArrayList<String> tokens = new ArrayList<>();
		
		int mark = 0;
		String nextToken = null;
		
		try {
			while(true) {
				nextToken = "";
				
				//Look for quotes
				if(line.charAt(mark) == '"') {
					
					nextToken += line.charAt(mark);
					mark++;
					for(int i = mark;; i++) {
						mark++;
						
						//Check for escape sequence.
						if(line.charAt(i) == '\\') {
							if(line.charAt(i + 1) == '"') {
								nextToken += '"';
								mark++;
								i++;
								continue;
							}
						}
						
						if(line.charAt(i) == '"') {
							nextToken += line.charAt(i);
							break;
						}
						
						nextToken += line.charAt(i);
					}
				} else {
					for(int i = mark;; i++) {
						mark++;
						
						//Check for space
						if(line.charAt(i) == ' ')
							break;
						
						nextToken += line.charAt(i);
					}
				}
				
				if(!nextToken.isBlank() && !nextToken.isEmpty()) {
					nextToken.strip();
					tokens.add(nextToken);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			if(!nextToken.isBlank() && !nextToken.isEmpty()) {
				nextToken.strip();
				tokens.add(nextToken);
			}
		}
		
		return tokens;
	}
	
	private static boolean isField(ArrayList<String> tokens) {
		if(tokens.size() != 2)
			return false;
		
		String lastToken = tokens.get(1);
		
		//Bracket indicates object
		if(lastToken.compareTo("{") == 0)
			return false;
		
		//Check for quotes
		if(lastToken.charAt(0) == '"' && lastToken.charAt(lastToken.length() - 1) == '"')
			return true;
		
		
		//default determination of false
		return false;
	}
	
	private static boolean isObject(ArrayList<String> tokens) {
		
		//Check requirements for object without a name
		
		if(tokens.size() == 2) {
			//Check for trailing bracket
			//Bracket is required to be an object
			String lastToken = tokens.get(1);
			if(lastToken.compareTo("{") == 0)
				return true;
		}
		
		//Check requirements for object with a name
		if(tokens.size() == 3) {
			String token2 = tokens.get(1);
			String token3 = tokens.get(2);
			
			//Verify enclosing quotes for name, if a name is specified
			if(token2.charAt(0) == '"' && token2.charAt(token2.length() - 1) == '"') {
				
				//Check for trailing bracket
				if(token3.compareTo("{") == 0)
					return true;
			}
		}		
		
		//default determination of false
		return false;
	}
	
	private static String getLine(Scanner scanner) {
		while(true) {
			lineNumber++;
			String line = null;
			
			try {
				line = scanner.nextLine();
			} catch(NoSuchElementException e) {
				//Explicitly return null
				return null;
			}
			
			//Check for blank line
			if(line.isEmpty() || line.isBlank())
				continue;
			
			//Check for comments
			if(line.charAt(0) == '#')
				continue;
			
			if(line != null)
				return line;
		}
	}
	
	private static byte[] convertFieldToLineBytes(SoffitField field) {
		
		String name = field.getName();
		String value = field.getValue();
		
		String line = "";
		
		//Set indentation
		for(int i = 0; i < field.getNestingLevel(); i++) {
			line += '\t';
		}
		
		//Name
		for(int i = 0; i < name.length(); i++) {
			line += name.charAt(i);
		}
		line += ' ';
		
		//Value
		line += '"';
		value = convertToEscapeSequence(value);
		for(int i = 0; i < value.length(); i++) {
			//Add all normal characters
			line += value.charAt(i);
		}
		line += "\"\n";
		
		return convertLineToBytes(line);
	}
	
	private static byte[] convertObjectDeclarationToLineBytes(SoffitObject object) {		
		String type = object.getType();
		String name = object.getName();
		
		String line = "";
		
		//Set indentation
		for(int i = 0; i < object.getNestedLevel(); i++) {
			line += '\t';
		}
		
		//Type
		for(int i = 0; i < type.length(); i++) {
			line += type.charAt(i);
		}
		line += " ";
		
		//name
		if(name != null) {
			line += '"';
			name = convertToEscapeSequence(name);
			for(int i = 0; i < name.length(); i++) {
				//Add all normal characters
				line += name.charAt(i);
			}
			line += "\" ";
		}
		line += "{\n";
		
		return convertLineToBytes(line);
	}
	
	private static String convertFromEscapeSequence(String s) {
		
		String convertedString = "";
		
		for(int i = 0; i < s.length(); i++) {
			//Look for escape character
			if(s.charAt(i) == '\\') {
				
				//Double quote
				if(s.charAt(i + 1) == '"') {
					convertedString += '"';
					i++;
					continue;
				}
				
				//Newline
				if(s.charAt(i + 1) == 'n') {
					convertedString += '\n';
					i++;
					continue;
				}
				
				//Backslash
				if(s.charAt(i + 1) == '\\') {
					convertedString += '\\';
					i++;
					continue;
				}
				
				throw new SoffitException("Invalid SOFFIT escape sequence");
			}
			
			//Add all normal characters
			convertedString += s.charAt(i);
		}
		
		return convertedString;
	}
	
	private static String convertToEscapeSequence(String s) {
		String convertedString = "";
		for(int i = 0; i < s.length(); i++) {
			//Double quote correction
			if(s.charAt(i) == '"') {
				convertedString += "\\\"";
				continue;
			}
			//Newline correction
			if(s.charAt(i) == '\n') {
				convertedString += "\\n";
				continue;
			}
			//Backslash correction
			if(s.charAt(i) == ESCAPE_SEQUENCE) {
				convertedString += "\\\\";
				continue;
			}
			
			//Add all normal characters
			convertedString += s.charAt(i);
		}
		
		return convertedString;
	}
	
	private static String stripQuotations(String s) {
		String stripped = "";
		for(int i = 0; i < s.length() - 2; i++) {
			stripped += s.charAt(i + 1);
		}
		return stripped;
	}
}