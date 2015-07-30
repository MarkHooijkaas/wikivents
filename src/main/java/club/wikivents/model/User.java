package club.wikivents.model;

import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public class User extends ReflectStruct {
	public final String _id;
	public final String username;
	public final String email;
	public final String password;
	
	public User(Struct data) {
		this._id=data.getString("_id");
		this.username=data.getString("username");
		this.email=data.getString("email");
		this.password=data.getString("password");
		
	}
}
