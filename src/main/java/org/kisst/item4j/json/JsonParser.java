package org.kisst.item4j.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;

public class JsonParser {
	private final JSONParser parser= new JSONParser();

	public Struct parse(String json) {
		try {
			return Item.asStruct(parser.parse(json));
		} 
		catch (ParseException e) { throw new RuntimeException(e);}
	}

	public Struct parse(File f) {
		FileReader reader=null;
		try {
			reader = new FileReader(f);
			return Item.asStruct(parser.parse(reader));
		} 
		catch (ParseException e) { throw new RuntimeException(e);} 
		catch (FileNotFoundException e) { throw new RuntimeException(e);}
		catch (IOException e) { throw new RuntimeException(e);}
		finally {
			if (reader!=null) {
				try {
					reader.close();
				}
				catch (IOException e) { throw new RuntimeException(e);}
			}
		}
	}
	
}
