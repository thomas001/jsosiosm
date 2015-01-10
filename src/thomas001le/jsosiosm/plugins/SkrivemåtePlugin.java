package thomas001le.jsosiosm.plugins;

import java.util.HashMap;
import java.util.Map;

import thomas001le.jsosiosm.AbstractParserPlugin;
import thomas001le.jsosiosm.OSMGroup;
import thomas001le.jsosiosm.TagsResult;
import thomas001le.jsosiosm.TagsResult.TagsGenerator;
import thomas001le.jsosiosm.sosi.Group;

public class SkrivemåtePlugin extends AbstractParserPlugin {
	
	private static class SkrivemåteTag implements TagsGenerator {
		
		private static final Map<Long,Map<String,String>> TEXTTAGS = new HashMap<>();
		
		static {
			TEXTTAGS.put(1L, totags("natural=peak"));
			TEXTTAGS.put(2L, totags("natural=peak"));
			TEXTTAGS.put(100L, totags("place=city"));
			TEXTTAGS.put(101L, totags("place=town"));
			TEXTTAGS.put(102L, totags("place=quarter")); // suburb? neighborhood?
			TEXTTAGS.put(103L, totags("place=village")); 
			TEXTTAGS.put(104L, totags("place=hamlet")); 
			TEXTTAGS.put(132L, totags("place=borough")); 
		}

		@Override
		public TagsResult getTags(OSMGroup gr) {
			String name;
			
			
			long navntype = ((Number) gr.child("NAVNTYPE").getValues().get(0)).longValue();
			Map<String,String> tags = TEXTTAGS.get(navntype);
			if(tags == null)
				return TagsResult.IGNORE;

			tags = new HashMap<String,String>(tags);
			Group ssr = gr.getChild("SSR");
			if(ssr != null) {
				Group snavn = ssr.child("SNAVN");
				name = stringValue(snavn.getValues());
			} else {
				name = stringValue(gr.child("STRENG").getValues());
			}
			tags.put("name", name);
			return TagsResult.tags(tags);
		}
		
	}
	
	{
		addTags("Skrivemåte", new SkrivemåteTag());
	}

}
