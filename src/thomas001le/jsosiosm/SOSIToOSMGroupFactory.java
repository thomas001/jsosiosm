package thomas001le.jsosiosm;

import java.util.List;

import thomas001le.jsosiosm.sosi.DefaultGroupFactory;
import thomas001le.jsosiosm.sosi.Group;
import thomas001le.jsosiosm.sosi.GroupFactory;

class SOSIToOSMGroupFactory implements GroupFactory<OSMGroup> {
	
	@Override
	public OSMGroup makeGroup(String name, long sn,
			List<? extends Group> children, List<Object> values) {
		if(name.equals("PUNKT")) {
			return new PointGroup(name, sn, children, values);
		} else if(name.equals("KURVE")) {
			return new CurveGroup(name, sn, children, values);
		} else if(name.equals("FLATE")) {
			return new PolygonGroup(name, sn, children, values);
		} else if(name.equals("TEKST")) {
			return new TextGroup(name, sn, children, values);
		} else if(name.equals("SLUTT")) {
			return null;
		} else {
			throw new RuntimeException("Invalid group name: " + name);
		}
	}

	@Override
	public GroupFactory<? extends Group> childrenGroupFactory(String name,
			long sn, List<Object> values) {
		return DefaultGroupFactory.INSTANCE;
	}
}