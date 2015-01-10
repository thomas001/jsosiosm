package thomas001le.jsosiosm;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import thomas001le.jsosiosm.sosi.Group;

public class PointGroup extends OSMGroup {

	public PointGroup(String name, long sn, List<? extends Group> children, List<Object> values) {
		super(name, sn, children, values);
	}

	@Override
	public long osm(SOSIToOSMParser parser, Map<String, String> tags) throws IOException {
		Point2L p = pointsIterator().next();
		long id = parser.putNode(p, getSn(), tags);
		return id;
	}

}
