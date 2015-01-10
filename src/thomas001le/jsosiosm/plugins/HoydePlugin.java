package thomas001le.jsosiosm.plugins;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import thomas001le.jsosiosm.AbstractParserPlugin;
import thomas001le.jsosiosm.OSMGroup;
import thomas001le.jsosiosm.SOSIToOSMParser;
import thomas001le.jsosiosm.TagsResult;

public class HoydePlugin extends AbstractParserPlugin {

	public HoydePlugin() {
		addTags("Terrengpunkt", "IGNORE",
				"Høydekurve", "contour=elevation,contour_ext=elevation_minor", // tags everysthing as minor?
				"Hjelpekurve", "contour=elevation,contour_ext=elevation_minor"
			);
    }
	
	@Override
	public TagsResult getTags(OSMGroup gr) {
		TagsResult res = super.getTags(gr);
		if(res != null && res.isTags()) {
			double ele = ((Number) gr.getChild("HØYDE").getValues().get(0)).doubleValue();
			Map<String, String> tags = new HashMap<>(res.getTags());
			tags.put("ele", Double.toString(ele));
			return TagsResult.tags(tags);
		} 
		return res;
	}
	
	@Override
	public void doGeometry(SOSIToOSMParser parser, OSMGroup gr,
			Map<String, String> tags) throws IOException {
		// java guards.....
		boolean old_cache = parser.useCache();
		try {
			parser.useCache(false);
			super.doGeometry(parser, gr, tags);
		} finally {
			parser.useCache(old_cache);
		}
	}
}
