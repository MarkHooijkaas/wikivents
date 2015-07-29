package org.kisst.type4j.registry;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.kisst.type4j.registry.ReflectionFieldsTypeRegistry.JavaClass;

public interface JavaTimeTypes extends JavaType  {

	@JavaClass(Instant.class) 
	public final static StringParseType<Instant> instant = new StringParseType<Instant>(Instant.class, JavaTimeTypes::parseInstant);

	@JavaClass(LocalDate.class) 
	public final static StringParseType<LocalDate> LocalDate = new StringParseType<LocalDate>(LocalDate.class, JavaTimeTypes::parseLocalDate);

	@JavaClass(LocalTime.class) 
	public final static StringParseType<LocalTime> LocalTime = new StringParseType<LocalTime>(LocalTime.class, JavaTimeTypes::parseLocalTime);

	@JavaClass(LocalDateTime.class) 
	public final static StringParseType<LocalDateTime> LocalDateTime = new StringParseType<LocalDateTime>(LocalDateTime.class, JavaTimeTypes::parseLocalDateTime);

	
	
	public static Instant parseInstant(String str) { return null; } // TODO
	public static LocalDate parseLocalDate(String str) { return null; } // TODO
	public static LocalTime parseLocalTime(String str) { return null; } // TODO
	public static LocalDateTime parseLocalDateTime(String str) { return null; } // TODO
}
