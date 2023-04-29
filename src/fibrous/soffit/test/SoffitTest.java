package fibrous.soffit.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fibrous.soffit.SoffitField;
import fibrous.soffit.SoffitObject;
import fibrous.soffit.SoffitUtil;

public class SoffitTest {
	public static void main(String[]args) throws FileNotFoundException, Exception {
		
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
