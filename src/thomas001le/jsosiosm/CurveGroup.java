package thomas001le.jsosiosm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import thomas001le.jsosiosm.sosi.Group;

public class CurveGroup extends OSMGroup {
	
	CurveGroup(String name, long sn, List<? extends Group> children, List<Object> values) {
		super(name, sn, children, values);
	}

	@Override
	public long osm(SOSIToOSMParser parser, Map<String, String> tags) throws IOException {
		List<Long> nodes = new ArrayList<Long>();
		for(Point2L p : points()) {
			long id = parser.putNode(p, 0, null);
			nodes.add(id);
		}
		return parser.putWay(nodes, getSn(), tags);
	}

}
