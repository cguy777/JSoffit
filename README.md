# JSoffit

JSoffit is an Object oriented SOFFIT processor for Java.
SOFFIT (String Object Framework For Information Transfer) is a general-purpose and schema-less data serialization framework/format.
See 'SOFFIT Definition.txt' for info on the SOFFIT standard.

## Basic Usage

JSoffit has two primary classes:  
`SoffitObject`  
`SoffitField`  
SoffitObject is a container that holds SoffitFields and other objects.
SoffitField is glorified version of a key-value pair.
By and large, the values are accessed using `getValue` and `setValue`.
There's also a variety of convenience getters and setters for use with primitives (e.g., `put(int)` and `asInt()`).

### Main Functions
There are two main functions in the SoffitUtil class that help you process SOFFIT streams:  
`public static SoffitObject ReadStream(InputStream)`  
`public static void WriteStream(SoffitObject, OutputStream)`  

There are also two convenience functions to work with Java Strings instead of iostreams:
`public static SoffitObject ReadStreamFromString(String)`  
`public static String WriteStreamToString(SoffitObject)`  

Call one of the read functions to de-serialize a stream and create a SoffitObject.  
You can manually create objects and fields with traditional constructors:  
`SoffitObject exampleObject = new SoffitObject("ObjectType", "ObjectName");`  
Call one of the write functions to serialize a root object.  
There is a plethora of methods associated with the SoffitObject and SoffitField classes to help you manage your data in many different ways.

Copyright (c) 2026, Noah McLean
