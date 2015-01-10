package thomas001le.jsosiosm.sosi;

import java.util.List;

public class DefaultGroupFactory implements GroupFactory<Group> {

	@Override
	public Group makeGroup(String name, long sn, List<? extends Group> children,
			List<Object> values) {
		return new Group(name, sn, children, values);
	}

	@Override
	public GroupFactory<? extends Group> childrenGroupFactory(String name, long sn,
			List<Object> values) {
		return this;
	}
	
	public static final DefaultGroupFactory INSTANCE = new DefaultGroupFactory();

}
