package club.wikivents.model;

import java.time.LocalDate;
import java.util.HashMap;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.kisst.crud4j.CrudRef;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

public class MongoCodecs {
	// The model (and users table) will be constructed using the Storage objects, so the user table
	// can only be set after the Storage is created
	private static WikiventsModel model=null;
	public static void setModel(WikiventsModel model) {MongoCodecs.model=model;}

	@SuppressWarnings("rawtypes")
	public static class CrudRefCodec implements Codec<CrudRef> {
		@Override   public void encode(BsonWriter writer, CrudRef t, EncoderContext ec) {
			writer.writeString(t.toString());  
		}
		@Override public Class<CrudRef> getEncoderClass() { return CrudRef.class; }
		@Override public CrudRef<?> decode(BsonReader reader, DecoderContext dc) {
			String json = reader.readString();
			return model.users.createRef(json);
		}
	}

	public static class LocalDateCodec implements Codec<LocalDate> {
		@Override public void encode(BsonWriter writer, LocalDate t, EncoderContext ec) {
			writer.writeString(t.toString());
		}
		@Override public Class<LocalDate> getEncoderClass() { return LocalDate.class; }
		@Override public LocalDate decode(BsonReader reader, DecoderContext dc) {
			String date = reader.readString();
			return LocalDate.parse(date);
		}
	}
	public static class DocumentCodecProvider implements CodecProvider {
	    private final BsonTypeClassMap bsonTypeClassMap;
	    public DocumentCodecProvider(final BsonTypeClassMap bsonTypeClassMap) { 
	        this.bsonTypeClassMap = bsonTypeClassMap;                                       
	    }                                                                       
	    @SuppressWarnings("unchecked")
		@Override public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {                      
	        if (clazz == Document.class)                      
	            return (Codec<T>) new DocumentCodec(registry, bsonTypeClassMap);           
	        if (clazz == CrudRef.class)                      
	            return (Codec<T>) new CrudRefCodec();           
	        return null;                                                                                   
	    }                                                                                                  
	}
	
	public static MongoClientOptions options() {
		// This code does not seem to have any effect. Still getting
		// org.bson.codecs.configuration.CodecConfigurationException: Can't find a codec for class club.wikivents.model.User$Table$Ref.
		// Decided in MongoStruct to just do a toString
		return MongoClientOptions.builder().codecRegistry(codecs()).build();
	}
	public static CodecRegistry codecs() {
		HashMap<BsonType, Class<?>> replacements = new HashMap<BsonType, Class<?>>();
		replacements.put(BsonType.DATE_TIME, LocalDate.class);
		replacements.put(BsonType.STRING, User.class);
		BsonTypeClassMap bsonTypeClassMap = new BsonTypeClassMap(replacements);

		CodecRegistry defaultCodecRegistry = MongoClient.getDefaultCodecRegistry(); 
		DocumentCodecProvider documentCodecProvider = new DocumentCodecProvider(bsonTypeClassMap); 
		Codec<LocalDate> instantCodec = new LocalDateCodec();   
		CodecRegistry codecRegistry = 
				CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(instantCodec,new CrudRefCodec()),
				CodecRegistries.fromProviders(documentCodecProvider),
				defaultCodecRegistry);
		
		//CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
		//		CodecRegistries.fromCodecs(new LocalDateCodec(), new UserRefCodec()),
		//		MongoClient.getDefaultCodecRegistry());
		return codecRegistry;
	}
}
