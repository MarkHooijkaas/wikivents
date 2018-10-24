package club.wikivents.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoRef;

import club.wikivents.model.geo.City;
import club.wikivents.model.geo.Province;
import club.wikivents.model.geo.ProvinceField;

public abstract class WikiventData extends CommonBase<Wikivent> implements Item.Factory {
	@Override public Iterable<String> fieldNames() { return schema.fieldNames(); }
	public static final Schema schema=new Schema();
	public static final class Schema extends CommonBase.Schema<Wikivent> {
		private Schema() { super(Wikivent.class); }
		public final StringField imageUrl = new StringField("imageUrl");
		public final IntField max = new IntField("max"); 
		public final BooleanField membersAllowed = new BooleanField("membersAllowed");
		public final BooleanField backupMembersAllowed = new BooleanField("backupMembersAllowed");
		public final BooleanField cancelled = new BooleanField("cancelled");
		public final BooleanField idea = new BooleanField("idea");
		public final LocalDateField date = new LocalDateField("date"); 
		public final LocalTimeField time = new LocalTimeField("time"); 
		public final LocalTimeField endTime = new LocalTimeField("endTime"); 
		public final StringField city = new StringField("city");
		public final ProvinceField province = new ProvinceField("province");
		public final StringField location = new StringField("location"); 
		public final StringField cost = new StringField("cost"); 
		public final StringField guestInfo = new StringField("guestInfo");
		public final StringField tags = new StringField("tags");
		public final SequenceField<Group.Ref> groups = new SequenceField<>(Group.Ref.class,"groups");
	}
	
	public final String city;
	public final Province province;
	public final String location;
	public final String cost;
	public final String guestInfo;
	public final String imageUrl;
	public final String tags;
	public final LocalDate date;
	public final LocalTime time;
	public final LocalTime endTime;
	public final int max;
	public final boolean membersAllowed;
	public final boolean backupMembersAllowed;
	public final boolean cancelled;
	public final boolean idea;
	public final ImmutableSequence<Group.Ref> groups;
	
	public WikiventData(WikiventsModel model, Struct data) {
		super(schema, model, model.events, data);
		this.guestInfo=schema.guestInfo.getString(data, null);
		this.imageUrl=schema.imageUrl.getString(data);
		this.city=schema.city.getString(data);
		Province prov = schema.province.findProvince(data);
		if (prov==null)
			this.province= City.findProvince(city);
		else
			this.province=prov;
		this.location=schema.location.getString(data);
		this.cost=schema.cost.getString(data);
		this.date=schema.date.getLocalDate(data);
		this.time=schema.time.getLocalTime(data);
		this.endTime=schema.endTime.getLocalTime(data);
		this.max=schema.max.getInt(data);
		this.membersAllowed=schema.membersAllowed.getBoolean(data,true);
		this.backupMembersAllowed=schema.backupMembersAllowed.getBoolean(data,true);
		this.cancelled=schema.cancelled.getBoolean(data,false);
		this.idea=schema.idea.getBoolean(data,false);
		this.groups = schema.groups.getSequenceOrEmpty(model, data);
		String tmp = schema.tags.getString(data,  "");
		if (! tmp.startsWith(","))
			tmp=","+tmp;
		if (! tmp.endsWith(","))
			tmp=tmp+",";
		this.tags=tmp;
	}

	@Override public Ref getRef() { return Ref.of(model,_id); }
	public static class Ref extends PkoRef<Wikivent> {
		static public Ref of(WikiventsModel model, String key) { return key==null ? null : new Ref(model, key); }
		public static class Field extends Schema.BasicField<Wikivent.Ref> {
			public Field(String name) { super(Wikivent.Ref.class, name); }
			public Ref getRef(WikiventsModel model, Struct data) { return of(model, Item.asString(data.getDirectFieldValue(name)));}
		}
		private Ref(WikiventsModel model, String _id) { super(model.events, _id); }
	}
	
	@Override public String getName() { return title; }
}
