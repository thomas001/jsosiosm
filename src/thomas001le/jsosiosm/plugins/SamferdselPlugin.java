package thomas001le.jsosiosm.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import thomas001le.jsosiosm.AbstractParserPlugin;
import thomas001le.jsosiosm.OSMGroup;
import thomas001le.jsosiosm.SOSIToOSMParser;
import thomas001le.jsosiosm.TagsResult;
import thomas001le.jsosiosm.osm.OSMWriter;
import thomas001le.jsosiosm.sosi.Group;

public class SamferdselPlugin extends AbstractParserPlugin {
	
	private Map<String,List<Long>> road_routes = new HashMap<>();

	public SamferdselPlugin() {
		addTags(
			               //K
			               "Bane", "railway=rail",
			               // P
			               "Barmarksløype", "IGNORE",
			               // K, VNR, RUTENR
			               "Bilferjestrekning", "route=ferry,motor_vehicle=yes,motorcar=yes",
			               // K, MEDIUM, RUTENR
			               "GangSykkelveg", "highway=cycleway",
			               // K, RUTENUR
			               "Passasjerferjestrekning", "route=ferry,motor_vehicle=no,motorcar=no",
			               // P
			               "Stasjon", "railway=station",
			               // K, RUTEMERKING, RUTENR, VEDLIKEH, MEDIUM
			               "Sti", "highway=path",
			               // K, MEDIUM, RUTENR
			               "Traktorveg", "highway=track",
			               // K, VNR, MOTORVEGTYPE, MEDIUM, RUTENR
			               "VegSenterlinje", "",
			               // P, VEGSPERRINGTYPE, ROTASJON
			               "Vegsperring", "barrier=yes"
			               );
	}
	
	String[] parseVnr(Group gr) {
		Group vnr = gr.getChild("VNR");
		if(vnr != null) {
			String[] res = new String[3];
			String cat = null, stat = null;
			String route = null;
			if(vnr.getValues().size() > 0)
				cat = (String) vnr.getValues().get(0);
			if(vnr.getValues().size() > 1)
				stat = (String) vnr.getValues().get(1);
			if(vnr.getValues().size() > 2)
				route = vnr.getValues().get(2).toString();
			
			res[0] = cat;
			res[1] = stat;
			res[2] = route;
			return res;
		}
		return null;
	}
	
	String parseMedium(Group gr) {
		Group med = gr.getChild("MEDIUM");
		if(med != null)
			return med.getValues().get(0).toString();
		else
			return null;
	}
	
	@Override
	public TagsResult getTags(OSMGroup gr) {
		TagsResult res =  super.getTags(gr);
		if(res != null && res.isTags()) {
			String[] vnr = parseVnr(gr);
			Map<String, String> tags = new HashMap<String, String>(res.getTags());

			if (vnr != null && gr.getObjType().equals("VegSenterlinje")) {
				String highway = "road";				
				if (vnr[0].equals("R"))
					highway = "primary";
				else if (vnr[0].equals("F"))
					highway = "secondary";
				else if (vnr[0].equals("S"))
					highway = "???"; // TODO
				else if (vnr[0].equals("E"))
					highway = "trunk";
				else if (vnr[0].equals("P")) {
					tags.put("access", "private");
				} else if (vnr[0].equals("K"))
					highway = "tertiary";
				else
					throw new RuntimeException("Unknown road type");

				if (vnr[1] != null && !"SVG".contains(vnr[1]))
					highway = "proposed";

				tags.put("highway", highway);
			}
			
			String medium = parseMedium(gr);
			if(medium != null) {
				if(medium.equals("L"))
					tags.put("bridge", "yes");
				else if(medium.equals("U"))
					tags.put("tunnel", "yes");
			}
			
			return TagsResult.tags(tags);
		}
		return res;
	}
	
	@Override
	public void doGeometry(SOSIToOSMParser parser, OSMGroup gr,
			Map<String, String> tags) throws IOException {
		long id = gr.osm(parser, tags);
		String[] vnr = parseVnr(gr);
		if(vnr != null && vnr[2] != null) {
			if( !road_routes.containsKey(vnr[2]))
				road_routes.put(vnr[2], new ArrayList<Long>());
			road_routes.get(vnr[2]).add(id);
		}
	}
	
	
	@Override
	public void finish(SOSIToOSMParser parser) throws IOException {
		for( Entry<String, List<Long>> route: road_routes.entrySet()) {
			OSMWriter.Relations rel = new OSMWriter.Relations();
			for(long id : route.getValue()) {
				rel.addMember(id, "", "way");
			}
			
			Map<String,String> tags = new HashMap<String,String>();
			tags.put("route", "road"); // FIXME
			tags.put("ref", route.getKey());
			
			parser.putRelation(rel, 0, "route", tags);
		}
	}
}
