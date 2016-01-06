package club.wikivents.model;

import org.kisst.item4j.HasName;
import org.kisst.pko4j.PkoCommand;
import org.kisst.pko4j.PkoObject;
import org.kisst.util.CallInfo;

public class WikiventsCommands {
	public static abstract class Command<T extends PkoObject<WikiventsModel, T>> implements PkoCommand<T> {
		public final T record;
		public Command(T record) { this.record=record;}
		public boolean mayBeDoneBy(User user) { return user.isAdmin; }
		@Override public void otherActions(boolean rerun) {}
		@Override public T target() {return record;}
		@Override public String toString() {return this.getClass().getSimpleName()+"("+record._id+")";}
	}
	
	public static class ChangeFieldCommand<T extends PkoObject<WikiventsModel, T>>  extends Command<T> {
		public final HasName field; 
		public final String value; 
		public ChangeFieldCommand(T record, HasName field, String value) { 
			super(record);
			this.field=field;
			this.value=value;
		}
		@Override public boolean mayBeDoneBy(User user) { return user.isAdmin;}//|| record.fieldMayBeChangedBy(field, user);}
		@Override public T apply() {
			String logValue=value;
			if (logValue.length()>10)
				logValue=logValue.substring(0, 7)+"...";
			CallInfo.instance.get().action="handleChangeField "+field+" to "+logValue;
			return record.changeField(field, value); 
		}
	}


}
