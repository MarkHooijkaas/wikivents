package club.wikivents.model.geo;

import java.io.File;
import java.util.HashMap;

import org.kisst.util.FileUtil;

public class City {
	public static Province findProvince(String city) {
		if (city==null)
			return null;
		return index.get(cleanCity(city));
	}
	
	private static final HashMap<String, Province> index = new HashMap<>();

	private static String cleanCity(String city) {
		return city.replaceAll("[0-1 '-]","").toLowerCase();
	}
	public static void load(String filename) {
		File f = new File(filename);
		if (!f.exists())
			return;
		for (String line : FileUtil.loadString(filename).split("\n")) {
			String[] parts = line.split("\t");
			if (parts.length == 2)
				addCity(parts[0], parts[1],true);
			else if (parts.length >= 4) {
				addCity(parts[2], parts[3], true);
				addCity(parts[0], parts[3], parts[1].length()>4);
			} else {
				System.out.println("Ignoring " + line);
			}
		}
	}

	private static void addCity(String cityFull, String provinceName, boolean important) {
		String city =cleanCity(cityFull);
		Province province = Province.fromString(provinceName);
		Province existing= index.get(city);
		if (existing==null)
			index.put(city,province);
		else if (existing!=province){
			if (important) {
				index.put(city,province);
				//System.out.println(city+"->"+province+"\tIgnoring "+existing);
			}
			//else System.out.println(city+"->"+existing+"\tIgnoring "+province);
		}
	}

}
