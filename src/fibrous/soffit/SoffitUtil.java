/* BSD 3-Clause License
 *
 * Copyright (c) 2026, Noah McLean
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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Stack;

//*********************************************************
//String Object Framework For Information Transfer (SOFFIT)
//*********************************************************

/**
 * This class provides your basic stream IO processes for SOFFIT.
 * @author noahm
 *
 */
public class SoffitUtil {
	
	public static final String SOFFIT_START = "__SoffitStart";
	public static final String SOFFIT_END = "__SoffitEnd";
	public static final byte[] SOFFIT_START_BYTES = SOFFIT_START.getBytes();
	public static final byte[] SOFFIT_END_BYTES = SOFFIT_END.getBytes();
	public static final char ESCAPE_SEQUENCE = '\\';
	static final byte[] OPEN_CURLY = "{".getBytes();
	static final byte[] CLOSING_CURLY = "}".getBytes();
	
	private static int lineNumber = 0;
	
	/**
	 * Parses an {@link InputStream} as a root SOFFIT object.
	 * @param stream
	 * @return The SOFFIT root object as parsed from the InputStream.
	 * @throws SoffitException
	 * @throws IOException 
	 */
	public static SoffitObject ReadStream(InputStream stream) throws SoffitException, IOException {
		lineNumber = 0;
		
		//This is an internal buffer used for combining chars/strings, and the number passed into this constructor is ultimately how many characters can be in a line.
		//Rather large buffer to account for an absurdly long line
		ArrayOutputStream internalStream = new ArrayOutputStream(65536);
		
		SoffitObject root = new SoffitObject(null, null);
		
		byte[] header = getLine(stream, internalStream);
		if(!areBytesEqual(header, SOFFIT_START_BYTES))
			throw new SoffitException("SOFFIT header not found.");
		
		parseObject(stream, root, internalStream);
		
		return root;
	}
	
	/**
	 * Writes a SOFFIT object to an {@link OutputStream}.
	 * @param root
	 * @param output
	 */
	public static void WriteStream(SoffitObject root, OutputStream output) throws IOException {
		BufferedOutputStream bStream = new BufferedOutputStream(output);
		//This is an internal buffer used for combining chars/strings, and the number passed into this constructor is ultimately how many characters can be in a line.
		//Rather large buffer to account for an absurdly long line
		ArrayOutputStream internalStream = new ArrayOutputStream(65536);
		
		//Write header
		byte[] lineBytes = (SOFFIT_START + "\n").getBytes();
		bStream.write(lineBytes);
		bStream.flush();
	
		//Write the object itself
		writeObjects(root, bStream, internalStream);
		bStream.flush();
		
		//Write footer
		lineBytes = (SOFFIT_END + "\n").getBytes();
		bStream.write(lineBytes);
		bStream.flush();
	}
	
	/**
	 * Convenience method to write an object to an {@link OutputStream} and then format that stream into a string.
	 * The returned string appears exactly as it would in in a stream, containing both the header and footer.
	 * @param root
	 * @return
	 */
	public static String WriteStreamToString(SoffitObject root) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			WriteStream(root, baos);
		} catch (IOException e) {
			//This shouldn't really get called
			throw new RuntimeException(e);
		}
		
		return baos.toString();
	}
	
	/**
	 * Convenience method to read a Soffit stream from a formatted string.
	 * @param stream
	 * @return
	 * @throws IOException 
	 * @throws SoffitException 
	 */
	public static SoffitObject ReadStreamFromString(String stream) throws SoffitException, IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(stream.getBytes());
		return ReadStream(bais);
	}
	
	/**
	 * Writes fields first, and then every object recursively.
	 * @throws IOException 
	 */
	private static void writeObjects(SoffitObject object, BufferedOutputStream bStream, ArrayOutputStream internalStream) throws IOException {		
		//Write the fields first
		for(int i = 0; i < object.getAllFields().size(); i++) {			
			//Write the line containing a field...
			
			SoffitField field = object.getAllFields().get(i);
			
			//Check for null pointer assigned to the field's value.
			if(field.getValue() == null)
				throw new NullPointerException("Value assigned to SOFFIT field \"" + field.getName() + "\" is a null pointer.");
		
			bStream.write(convertFieldToLineBytes(field, internalStream));
			bStream.flush();
		}
		
		//Write object declarations and then recursively write other objects
		for(int i = 0; i < object.getAllObjects().size(); i++) {
			
			SoffitObject currentObject = object.getAllObjects().get(i);
			//Write the line the object declaration...
			bStream.write(convertObjectDeclarationToLineBytes(currentObject, internalStream));
			bStream.flush();
			
			//Recursively write the next fields and object declarations.
			writeObjects(currentObject, bStream, internalStream);
			
			//Write closing brackets.
			internalStream.reset();
			//Set indentation
			if(currentObject.getParent() != null) {
				for(int i2 = 0; i2 < currentObject.getNestedLevel(); i2++) {
					internalStream.write((byte) '\t');
				}	
			}
			
			internalStream.write((byte) '}');
			internalStream.write((byte) '\n');
			
			//Flush the output stream.
			bStream.write(internalStream.getWrittenBytes());
			bStream.flush();
		}
	}	
	
	/**
	 * Parses and interprets SoffitObjects and its contained SoffitFields and nested SoffitObjects.
	 * @throws IOException 
	 */
	
	private static void parseObject(InputStream stream, SoffitObject parent, ArrayOutputStream internalStream) throws SoffitException, IOException {
		Stack<SoffitObject> stack = new Stack<>();
		stack.push(parent);
		
		while (!stack.isEmpty()) {
			SoffitObject currentObject = stack.peek();
			byte[] line = getLine(stream, internalStream);
			
			//If we didn't get anything, then break out.
			if (line == null) {
			    throw new SoffitException("Incomplete SOFFIT stream.");
			}
			
			ArrayList<byte[]> tokens = getLineTokens(line, internalStream);
			
			//Ensure there are no double quotes in first token (The first token would be an object type, field name, or closing bracket)
			if(containsCharacter(tokens.get(0), '"'))
				throw new SoffitException("SOFFIT syntax error.", lineNumber);
			
			//Closing Bracket
			if (tokens.size() == 1 && areBytesEqual(tokens.get(0), CLOSING_CURLY)) {
				if (!currentObject.isRoot()) {
					stack.pop();
				} else {
					throw new SoffitException("SOFFIT stream contained too many closing brackets.", lineNumber);
				}
			//SOFFIT Footer
			} else if (areBytesEqual(tokens.get(0), SOFFIT_END_BYTES)) {
				if (!currentObject.isRoot()) {
					throw new SoffitException("SOFFIT footer encountered in non-root object.", lineNumber);
				}
				break;
			//Handle Objects
			} else if (isObject(tokens)) {
				SoffitObject object;
				byte[] type = tokens.get(0);
				
				if(tokens.size() == 2) {
					object = new SoffitObject(new String(type, StandardCharsets.US_ASCII));
				} else {
					byte[] name = stripQuotations(tokens.get(1));
					
					//Check for proper escape sequences
					try {
						name = convertFromEscapeSequence(name, internalStream);
					} catch (SoffitException e) {
						throw new SoffitException(e, lineNumber);
					}
					object = new SoffitObject(new String(type, StandardCharsets.US_ASCII), new String(name, StandardCharsets.US_ASCII));
				}
				
				currentObject.add(object);
				stack.push(object);
			//Handle Fields
			} else if (isField(tokens)) {
				byte[] name = tokens.get(0);
				
				//Set value, if defined.
				if(tokens.size() > 1) {
					byte[] value = stripQuotations(tokens.get(1));
			
					// Check for proper escape sequences
					try {
						value = convertFromEscapeSequence(value, internalStream);
					} catch (SoffitException e) {
						throw new SoffitException(e, lineNumber);
					}
					currentObject.add(new SoffitField(new String(name, StandardCharsets.US_ASCII), new String(value, StandardCharsets.US_ASCII)));
				} else {
					currentObject.add(new SoffitField(new String(name, StandardCharsets.US_ASCII), ""));
				}
			} else {
			    throw new SoffitException("SOFFIT syntax error.", lineNumber);
			}
		}
	}
	
	/**
	 * Internal to the parseObject method.
	 * @throws IOException 
	 */
	private static ArrayList<byte[]> getLineTokens(byte[] line, ArrayOutputStream tokenStream) throws IOException {
		ArrayList<byte[]> tokens = new ArrayList<>();
		
		int mark = 0;
		byte[] nextToken = null;
		
		try {
			while(true) {
				tokenStream.reset();
				
				//Look for quotes
				if(line[mark] == (byte) '"') {
					
					tokenStream.write(line[mark]);
					mark++;
					for(int i = mark;; i++) {
						mark++;
						
						//Check for escape sequence.
						if(line[i] == (byte) '\\') {
							if(line[i + 1] == (byte) '"') {
								tokenStream.write((byte) '"');
								mark++;
								i++;
								continue;
							}
						}
						
						if(line[i] == (byte) '"') {
							tokenStream.write(line[i]);
							break;
						}
						
						tokenStream.write(line[i]);
					}
				} else {
					for(int i = mark;; i++) {
						mark++;
						
						//Check for space
						if(line[i] == ' ')
							break;
						
						tokenStream.write(line[i]);
					}
				}
				
				nextToken = stripByteArray(tokenStream.getWrittenBytes());
				if(!areBytesBlank(nextToken))
					tokens.add(nextToken);
			}
		} catch (IndexOutOfBoundsException e) {
			nextToken = stripByteArray(tokenStream.getWrittenBytes());
			if(!areBytesBlank(nextToken))
				tokens.add(nextToken);
		}
		
		return tokens;
	}
	
	private static byte[] getLine(InputStream is, ArrayOutputStream internalStream) {
		
		while(true) {
			internalStream.reset();
			boolean eos = false;
			lineNumber++;
			
			while(true) {
				
				try {
					int c = is.read();
					
					//Check for EOS;
					if(c == -1) {
						eos = true;
						break;
					}
					
					//Check for new line
					if(c == (int) '\n')
						break;
					if(c == (int) '\r')
						break;
					
					internalStream.write((byte) c);
					
				} catch(IOException e) {
					//Explicitly return null
					return null;
				}
			}
			
			byte[] bytes = stripByteArray(internalStream.getWrittenBytes());
			//String line = new String(bytes, StandardCharsets.US_ASCII);
			
			//Check for EOS and essentially a null line
			if(eos && bytes.length == 0)
				return null;
			
			//Return if EOS is reached
			if(eos)
				return bytes;
			
			//Check for blank line
			if(bytes.length == 0)
				continue;
			
			//Check for comments
			if(bytes[0] == (byte) '#')
				continue;
			
			return bytes;
		}
	}
	
	/**
	 * Removes whitespace from a byte array.
	 * @param bytes
	 * @return
	 */
	private static byte[] stripByteArray(byte[] bytes) {
		int left = 0;
		for(; left < bytes.length; left++) {
			if(!isWhitespace(bytes[left])) {
				break;
			}
		}
		
		int right = bytes.length - 1;
		for(; right >= 0; right--) {
			if(!isWhitespace(bytes[right])) {
				break;
			}
		}
		
		byte[] strippedBytes = new byte[right - left + 1];
		System.arraycopy(bytes, left, strippedBytes, 0, strippedBytes.length);
		return strippedBytes;
	}
	
	private static boolean areBytesBlank(byte[] bytes) {
		if(bytes.length == 0)
			return true;
		
		for(int i = 0; i < bytes.length; i++) {
			if(!isWhitespace(bytes[i]))
				return false;
		}
		
		return true;
	}
	
	private static boolean isWhitespace(int i) {
		switch(i) {
		//tab
		case 9:
		//space
		case 32:
			return true;
			
		default:
			return false;
		}
	}
	
	/**
	 * Internal to the writeObject method.
	 * @throws IOException 
	 */
	private static byte[] convertFieldToLineBytes(SoffitField field, ArrayOutputStream internalStream) throws IOException {
		internalStream.reset();
		
		String name = field.getName();
		String value = field.getValue();
		
		//Set indentation
		for(int i = 0; i < field.getNestingLevel(); i++) {
			internalStream.write((byte) '\t');
		}
		
		//Name
		for(int i = 0; i < name.length(); i++) {
			internalStream.write((byte) name.charAt(i));
		}
		internalStream.write((byte) ' ');
		
		//Value
		//Check for blank value
		if(!field.getValue().isEmpty()) {
			internalStream.write((byte) '"');
			
			internalStream.mark();
			byte[] valueBytes = convertToEscapeSequence(value, internalStream);
			internalStream.goToMark();
			
			internalStream.write(valueBytes);
			internalStream.write((byte) '"');
		}
		
		internalStream.write((byte) '\n');
		return internalStream.getWrittenBytes();
	}
	
	private static byte[] convertObjectDeclarationToLineBytes(SoffitObject object, ArrayOutputStream internalStream) throws IOException {
		internalStream.reset();
		
		String type = object.getType();
		String name = object.getName();
		
		//Set indentation
		for(int i = 0; i < object.getNestedLevel(); i++) {
			internalStream.write((byte) '\t');
		}
		
		//Type
		for(int i = 0; i < type.length(); i++) {
			internalStream.write((byte) type.charAt(i));
		}
		internalStream.write((byte) ' ');
		
		//name
		if(name.length() > 0 && name != null) {
			internalStream.write((byte) '"');
			
			internalStream.mark();
			byte[] valueBytes = convertToEscapeSequence(name, internalStream);
			internalStream.goToMark();
			
			internalStream.write(valueBytes);
			internalStream.write((byte) '"');
			internalStream.write((byte) ' ');
		}
		internalStream.write((byte) '{');
		internalStream.write((byte) '\n');
		
		return internalStream.getWrittenBytes();
	}
	
	/**
	 * Internal to the parseObject method.
	 * @throws IOException 
	 */
	private static byte[] convertFromEscapeSequence(byte[] s, ArrayOutputStream internalStream) throws IOException {
		
		//String convertedString = "";
		internalStream.reset();
		
		for(int i = 0; i < s.length; i++) {
			//Look for escape character
			if(s[i] == (byte) '\\') {
				
				//Double quote
				if(s[i + 1] == (byte) '"') {
					internalStream.write((byte) '"');
					i++;
					continue;
				}
				
				//Newline
				if(s[i + 1] == 'n') {
					internalStream.write((byte) '\n');
					i++;
					continue;
				}
				
				//Backslash
				if(s[i + 1] == '\\') {
					internalStream.write((byte) '\\');
					i++;
					continue;
				}
				
				throw new SoffitException("Invalid SOFFIT escape sequence");
			}
			
			//Add all normal characters
			internalStream.write(s[i]);
		}
		
		return internalStream.getWrittenBytes();
	}
	
	private static byte[] convertToEscapeSequence(String s, ArrayOutputStream internalStream) throws IOException {
		//String convertedString = "";
		for(int i = 0; i < s.length(); i++) {
			//Double quote correction
			if(s.charAt(i) == '"') {
				internalStream.write((byte) '\\');
				internalStream.write((byte) '"');
				continue;
			}
			//Newline correction
			if(s.charAt(i) == '\n') {
				internalStream.write((byte) '\\');
				internalStream.write((byte) 'n');
				continue;
			}
			//Backslash correction
			if(s.charAt(i) == ESCAPE_SEQUENCE) {
				internalStream.write((byte) '\\');
				internalStream.write((byte) '\\');
				continue;
			}
			
			//Add all normal characters
			internalStream.write((byte) s.charAt(i));
		}
		
		//return convertedString;
		return internalStream.getWrittenBytesFromMark();
	}
	
	private static byte[] stripQuotations(byte[] s) {
		byte[] stripped = new byte[s.length - 2];
		System.arraycopy(s, 1, stripped, 0, stripped.length);
		return stripped;
	}
	
	private static boolean isField(ArrayList<byte[]> tokens) {
		if(tokens.size() > 2)
			return false;
		
		byte[] lastToken = tokens.get(tokens.size() - 1);
		
		//Bracket indicates object
		//TODO: is this redundant?  This seems logically accounted for in the parseObject method
		if(areBytesEqual(lastToken, OPEN_CURLY))
			return false;
		
		//Check for quotes
		if(lastToken[0] == (byte) '"' && lastToken[lastToken.length - 1] == (byte) '"')
			return true;
		
		//Null field
		if(tokens.size() == 1) {
			return true;
		}
		
		//default determination of false
		return false;
	}
	
	private static boolean isObject(ArrayList<byte[]> tokens) {
		
		//Check requirements for object without a name
		if(tokens.size() == 2) {
			//Check for open bracket
			//Bracket is required to be an object
			byte[] lastToken = tokens.get(1);
			if(areBytesEqual(lastToken, OPEN_CURLY))
				return true;
		}
		
		//Check requirements for object with a name
		if(tokens.size() == 3) {
			byte[] token2 = tokens.get(1);
			byte[] token3 = tokens.get(2);
			
			//Verify enclosing quotes for name, if a name is specified
			if(token2[0] == (byte) '"' && token2[token2.length - 1] == (byte) '"') {
				
				//Check for trailing bracket
				if(areBytesEqual(token3, OPEN_CURLY))
					return true;
			}
		}		
		
		//default determination of false
		return false;
	}
	
	private static boolean containsCharacter(byte[] s, char c) {
		for(int i = 0; i < s.length; i++) {
			if(s[i] == c)
				return true;
		}
		
		return false;
	}
	
	private static boolean areBytesEqual(byte[] a, byte[] b) {
		if(a.length != b.length)
			return false;
		
		for(int i = 0; i < a.length; i++) {
			if(a[i] != b[i])
				return false;
		}
		
		return true;
	}
}

/**
 * This class is essentially a fixed-length ByteArrayOutputStream with some specific features that help in the SoffitUtil class.
 * This class performs NO bounds checking on the buffer to increase speed.
 */
class ArrayOutputStream extends OutputStream {
	byte[] buffer;
	int pos = 0;
	int mark = 0;
	
	public ArrayOutputStream(int bufferSize) {
		buffer = new byte[bufferSize];
	}

	@Override
	public void write(int b) throws IOException {
		buffer[pos] = (byte) b;
		pos++;
	}
	
	@Override
	public void write(byte[] b) {
		System.arraycopy(b, 0, buffer, pos, b.length);
		pos += b.length;
	}
	
	@Override
	public void write(byte[] b, int off, int len) {
		System.arraycopy(b, 0, buffer, pos, len);
		pos += len;
	}
	
	public void reset() {
		pos = 0;
		mark = 0;
	}
	
	public void resetMark() {
		mark = 0;
	}
	
	public void mark() {
		mark = pos;
	}
	
	public void goToMark() {
		pos = mark;
	}
	
	public void pipeToOutputStream(OutputStream os) throws IOException {
		os.write(buffer, 0, pos);
	}
	
	public byte[] getWrittenBytes() {
		byte[] copy = new byte[pos];
		System.arraycopy(buffer, 0, copy, 0, pos);
		return copy;
	}
	
	public byte[] getWrittenBytesFromMark() {
		byte[] copy = new byte[pos - mark];
		System.arraycopy(buffer, mark, copy, 0, copy.length);
		return copy;
	}
}
