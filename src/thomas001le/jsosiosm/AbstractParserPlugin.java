package thomas001le.jsosiosm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thomas001le.jsosiosm.TagsResult.TagsGenerator;

public class AbstractParserPlugin implements ParserPlugin {
	
	protected Map<Object, TagsResult> tagdict = new HashMap<>();
	
	protected void addTags(Object...vals) {
		for(int i=0; i < vals.length; i+=2 ) {
			String key = (String) vals[i];
			Object key2;
			
			if( key.contains("/") ) {
				Pair<String,String> pair = new Pair<String,String>();
				int j = key.indexOf('/');
				pair.first = key.substring(0,j);
				pair.second = key.substring(j+1);
				key2 = pair;
			} else {
				key2 = key;
			}
			
			TagsResult res;
			Object obj_val = vals[i+1];
			if (obj_val instanceof String) {
				String val = (String) vals[i + 1];
				if (val.equals("IGNORE"))
					res = TagsResult.IGNORE;
				else if (val.equals("WARN"))
					res = TagsResult.WARN;
				else {
					Map<String, String> tags = totags(val);
					res = TagsResult.tags(tags);
				}
			} else {
				TagsGenerator f = (TagsGenerator) obj_val;
				res = TagsResult.generator(f);
			}
			
			tagdict.put(key2, res);
		}
	}
	
	public static Map<String,String> totags(String val) {
		Map<String, String> tags = new HashMap<>();
		if (!val.isEmpty()) {
			for (String tag : val.split("\\s*,\\s*")) {
				String[] tag_elements = tag.split("\\s*=\\s*", 2);
				tags.put(tag_elements[0], tag_elements[1]);
			}
		}
		return tags;
	}
	
	public static String stringValue(List<Object> values) {
		if(values.size() == 0)
			return "";
					
		StringBuilder sb = new StringBuilder();
		sb.append(values.get(0).toString());
		for(int i = 1; i < values.size(); ++i) {
			sb.append(" ");
			sb.append(values.get(i).toString());
		}
		return sb.toString();
	}

	@Override
	public TagsResult getTags(OSMGroup gr) {
		TagsResult res = tagdict.get(new Pair<String, String>(gr.getObjType(), gr.getName()));
		if(res != null)
			return res;
		
		return tagdict.get(gr.getObjType());
	}

	@Override
	public void doGeometry(SOSIToOSMParser parser, OSMGroup gr,
			Map<String, String> tags) throws IOException {
		gr.osm(parser, tags);
	}

	@Override
	public void finish(SOSIToOSMParser parser) throws IOException {
	}

}
