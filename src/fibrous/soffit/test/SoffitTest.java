package fibrous.soffit.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import fibrous.soffit.SoffitField;
import fibrous.soffit.SoffitObject;
import fibrous.soffit.SoffitUtil;

public class SoffitTest {
	public static void main(String[]args) throws FileNotFoundException, Exception {
		SoffitObject root = SoffitUtil.ReadStream(new FileInputStream("input.soffit"));
		SoffitUtil.WriteStream(root, new FileOutputStream("output.soffit"));
		
		SoffitField make = root.getAllObjects().get(0).getAllFields().get(0);
		
		System.out.println(make);
	}
	
	private static void printAllChildren(SoffitObject root) {
		for(SoffitObject object : root.getAllObjects()) {
			System.out.println(object.toString());
		}

		for(SoffitObject object : root.getAllObjects()) {
			printAllChildren(object);
		}
	}
}
