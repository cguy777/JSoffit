package fibrous.soffit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

//******************************************************
//String Object Format For Information Transfer (SOFFIT)
//******************************************************

public class SoffitUtil {
	
	public static final String SOFFIT_START = "__SoffitStart";
	public static final String SOFFIT_END = "__SoffitEnd";
	
	public static void main(String[]args) throws Exception {
		SoffitObject root = SoffitUtil.ReadStream(new FileInputStream(new File("input.soffit")));
		SoffitUtil.WriteStream(root, new FileOutputStream(new File("output.soffit")));
	}
	
	/**
	 * Parses an {@link InputStream} as a root SOFFIT object.
	 * @param stream
	 * @return The SOFFIT root object as parsed from the InputStream.
	 * @throws Exception
	 */
	public static SoffitObject ReadStream(InputStream stream) throws Exception {
		Scanner scanner = new Scanner(stream);
		SoffitObject root = new SoffitObject(null, null);
		
		//Look for __SoffitStart first
		String header = getLine(scanner);
		if(header.compareTo(SOFFIT_START) != 0)
			throw new Exception("SOFFIT header not found.");
		
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
			
			SoffitField field = object.getAllFields().get(i);
			line = "";
			
			//Set indentation
			for(int i2 = 0; i2 < field.getNestingLevel(); i2++) {
				line += "\t";
			}
			
			//Write field information
			line += field.getName();
			line += " \"";
			line += field.getValue();
			line += "\"\n";
			
			//Convert to byte array.
			lineBytes = convertLineToBytes(line);
			
			//Write the line containing a field...
			try {
				bStream.write(lineBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Write object declarations and then recursively write other objects
		for(int i = 0; i < object.getAllObjects().size(); i++) {
			
			SoffitObject currentObject = object.getAllObjects().get(i);
			line = "";
			
			//Set indentation
			for(int i2 = 0; i2 < currentObject.getNestingLevel(); i2++) {
				line += "\t";
			}
			
			//Write object declaration
			line += currentObject.getType();
			if(currentObject.getName() != null) {
				line += " \"";
				line += currentObject.getName();
				line += "\"";
			}
			
			line += " {\n";
			
			//Convert to byte array.
			lineBytes = convertLineToBytes(line);
			
			//Write the line the object declaration...
			try {
				bStream.write(lineBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Recursively write the next fields and object declarations.
			writeObjects(currentObject, bStream);
			
			//Write closing brackets.
			line = "";
			
			//Set indentation
			if(currentObject.getParent() != null) {
				for(int i2 = 0; i2 < currentObject.getNestingLevel(); i2++) {
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
		char[] lineChars = line.toCharArray();
		byte[] lineBytes = new byte[line.toCharArray().length];
		for(int i2 = 0; i2 < lineChars.length; i2++) {
			lineBytes[i2] = (byte) lineChars[i2];
		}
		
		return lineBytes;
	}
	
	private static void parseObject(Scanner scanner, SoffitObject parent) throws Exception {
		
		while(true) {
			
			boolean isObject = false;
			boolean isField = false;
			String line;
			
			line = getLine(scanner);
			//If we didn't get anything, then break out.
			if(line == null)
				break;		
			
			ArrayList<String> tokens = getLineTokens(line);
			
			//Check for end of object/closing curly bracket
			if(tokens.size() == 1 && tokens.get(0).compareTo("}") == 0)
				break;
			
			isField = isField(tokens);
			isObject = isObject(tokens);
			
			if(isField) {
				parent.addField(new SoffitField(tokens.get(0), stripQuotations(tokens.get(1))));
			}
			
			if(isObject) {
				
				SoffitObject object;
				
				if(tokens.size() == 2) {
					object = new SoffitObject(tokens.get(0), null);
				} else {
					object = new SoffitObject(tokens.get(0), stripQuotations(tokens.get(1)));
				}
					
				parseObject(scanner, object);
				parent.addObject(object);
			}
			
			//Check for problems
			if(!isField && !isObject)
				throw new Exception("Malformed SOFFIT stream.");
			
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
				
				if(line.charAt(mark) == '"') {
					nextToken += line.charAt(mark);
					mark++;
					for(int i = mark;; i++) {
						mark++;
						
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
				
				if(!nextToken.isBlank() && !nextToken.isEmpty())
					tokens.add(nextToken);
			}
		} catch (IndexOutOfBoundsException e) {
			if(!nextToken.isBlank() && !nextToken.isEmpty())
				tokens.add(nextToken);
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
		String line = null;
		
		while(true) {
			try {
				line = scanner.nextLine();
			} catch(NoSuchElementException e) {
				break;
			}
			
			//Check for blank line
			if(line.isEmpty() || line.isBlank())
				continue;
			
			//Check for comments
			if(line.charAt(0) == '#')
				continue;
			
			//If we see the footer, return null as that is the end
			//of the SOFFIT portion of the stream.
			if(line.compareTo(SOFFIT_END) == 0) {
				line = null;
				break;
			}
			
			if(line != null)
				break;
		}
		
		return line;
	}
	
	private static String stripQuotations(String s) {
		String stripped = "";
		for(int i = 0; i < s.length() - 2; i++) {
			stripped += s.charAt(i + 1);
		}
		return stripped;
	}
}

enum ParseEndState {
	EOS, SoffitFooter
}