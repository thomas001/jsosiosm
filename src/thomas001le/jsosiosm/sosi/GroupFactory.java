package thomas001le.jsosiosm.sosi;

import java.util.List;

public interface GroupFactory<T> {
	public T makeGroup(String name, long sn, List<? extends Group> children, List<Object> values);
	
	public GroupFactory<? extends Group> childrenGroupFactory(String name, long sn, List<Object> values);
}
