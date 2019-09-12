package club.wikivents.model;

import org.kisst.pko4j.BasicPkoObject;
import org.kisst.pko4j.PkoCommand;
import org.kisst.pko4j.PkoObject;
import org.kisst.util.CallInfo;

public class WikiventsCommands {
	public static abstract class Command<T extends PkoObject> implements PkoCommand<T> {
		public final T record;
		public Command(T record) { this.record=record;}
		public boolean mayBeDoneBy(User user) { return user.isAdmin; }
		public void validate() {}
		@Override public void otherActions(boolean rerun) {}
		@Override public T target() {return record;}
		@Override public String toString() {return this.getClass().getSimpleName()+"("+record.getKey()+")";}
	}
	
	public static class ChangeFieldCommand<T extends PkoObject>  extends Command<T> {
		public final String fieldName; 
		public final String value; 
		public ChangeFieldCommand(T record, String fieldName, String value) { 
			super(record);
			this.fieldName=fieldName;
			this.value=value;
		}
		@Override public boolean mayBeDoneBy(User user) { return user.isAdmin;}//|| record.fieldMayBeChangedBy(field, user);}
		@SuppressWarnings("unchecked")
		@Override public T apply() {
			String logValue=value;
			if (logValue.length()>10)
				logValue=logValue.substring(0, 7)+"...";
			CallInfo.instance.get().action="handleChangeField "+fieldName+" to "+logValue;
			return ((BasicPkoObject<WikiventsModel, T>)record).changeField(fieldName, value); 
		}
	}
}
