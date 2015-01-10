package thomas001le.jsosiosm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import thomas001le.jsosiosm.osm.OSMWriter;
import thomas001le.jsosiosm.sosi.Group;

public class PolygonGroup extends OSMGroup {

	public PolygonGroup(String name, long sn, List<? extends Group> children,
			List<Object> values) {
		super(name, sn, children, values);
	}
	
	static final Pattern REF_SPLIT_PAT = Pattern.compile("\\s+(?![^(]*\\))");
	static final Pattern IREF_SPLIT_PAT = Pattern.compile("\\s+");
	@Override
	public long osm(SOSIToOSMParser parser, Map<String, String> tags)
			throws IOException {

		Group ref_group = getChild("REF");
		
		if(ref_group != null) {
			
			OSMWriter.Relations relations = new OSMWriter.Relations();
			
			@SuppressWarnings("unchecked")
			String ref_str = String.join(" ", (List<String>) (List<?>) ref_group.getValues()); // UGH!!!
			String[] refs = REF_SPLIT_PAT.split(ref_str);
			for(String ref : refs) {
				if( ref.charAt(0) == '(') {
					ref = ref.substring(1, ref.length() - 1);
					for(String iref : IREF_SPLIT_PAT.split(ref)) {
						long sosi_id = Math.abs(Long.parseLong(iref.substring(1)));
						long id = parser.idForSosiId(sosi_id);
						relations.addMember(id, "inner", "way");
					}
				} else {
					long sosi_id = Math.abs(Long.parseLong(ref.substring(1)));
					long id = parser.idForSosiId(sosi_id);
					relations.addMember(id, "outer", "way");
				}
			}

			return parser.putRelation(relations, getSn(), "multipolygon", tags);
		} else {
			List<Long> ids = new ArrayList<Long>();
			for(Point2L p : points() ) {
				ids.add( parser.putNode(p, 0, null));
			}
			assert( ids.get(0).equals( ids.get(ids.size() - 1)) );
			
			return parser.putWay(ids, getSn(), tags);
		}
		

	}

}
