# JSoffit

JSoffit is an Object oriented SOFFIT processor for Java.
SOFFIT (String Object Framework For Information Transfer) is a general-purpose data serialization framework/format.
See 'SOFFIT Definition.txt' for info on the SOFFIT standard.

## Basic Usage

JSoffit has two primary classes:  
`SoffitObject`  
`SoffitField`  
SoffitObject is a container that holds SoffitFields and other objects.
SoffitField is glorified version of a key-value pair.
Main Functions

There are two main functions in the SoffitUtil class that help you process SOFFIT streams:  
`public static SoffitObject ReadStream(InputStream)`  
`public static void WriteStream(SoffitObject, OutputStream)`  

There are also two alternate functions to work with Java Strings instead of iostreams:  
`public static SoffitObject ReadStreamFromString(String)`  
`public static String WriteStreamToString(SoffitObject)`  

Call one of the read functions to de-serialize a stream and create a SoffitObject.
You can manually objects and fields with traditional constructors:  
`SoffitObject exampleObject = new SoffitObject("ObjectType", "ObjectName");`  
There is a plethora methods associated with the SoffitObject and SoffitField classes to help you manage your data in many different ways.
