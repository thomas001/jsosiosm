package thomas001le.jsosiosm.plugins;

import java.util.HashMap;
import java.util.Map;

import thomas001le.jsosiosm.AbstractParserPlugin;
import thomas001le.jsosiosm.OSMGroup;
import thomas001le.jsosiosm.TagsResult;
import thomas001le.jsosiosm.TagsResult.TagsGenerator;

public class AdministrativeOmråderPlugin extends AbstractParserPlugin {
	
	private static class KommuneTags implements TagsGenerator {

		@Override
		public TagsResult getTags(OSMGroup gr) {
			Map<String,String> tags = new HashMap<>();
			tags.put("boundary", "administrative");
			tags.put("admin_level", "7");
			tags.put("name", gr.child("NAVN").getValues().get(0).toString());
			return TagsResult.tags(tags);
		}
		
	}

	{
		addTags(
				"Fylkesgrense", "boundary=administrative,admin_level=4",
				"Grunnlinje", "IGNORE",
				"Grunnlinjepunkt", "IGNORE",
				"Kommunegrense", "boundary=administrative,admin_level=7",
				"Kommune", new KommuneTags(),
				"Riksgrense", "boundary=administrative,admin_level=2",
				"Teiggrensepunkt", "IGNORE",
				// border at sea?
				"Territorialgrense", "boundary=administrative,admin_level=2"
				);
	}

}
