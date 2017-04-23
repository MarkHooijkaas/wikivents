package club.wikivents.model.geo;

import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;

public class ProvinceField extends Schema.BasicField<Province> {
	public ProvinceField(String name) { super(Province.class, name); }

	public Province findProvince(Struct s) { return Province.from(s.getDirectFieldValue(name),null); }
	public Province getProvince(Struct s, Province defaultValue) {
		return Province.from(getObject(s,null), defaultValue);
	}
}