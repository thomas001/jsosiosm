package thomas001le.jsosiosm.plugins;

import java.util.HashMap;
import java.util.Map;

import thomas001le.jsosiosm.AbstractParserPlugin;
import thomas001le.jsosiosm.OSMGroup;
import thomas001le.jsosiosm.TagsResult;
import thomas001le.jsosiosm.TagsResult.TagsGenerator;

public class RestriksjonsområderPlugin extends AbstractParserPlugin {

	private static class NatureReserveTags implements TagsGenerator {

		@Override
		public TagsResult getTags(OSMGroup gr) {
			Map<String,String> tags = new HashMap<>();
			tags.put("boundary","protected_area");
			tags.put("leisure","nature_reserve"); // mkgmap does not know about national_park
			tags.put("name", gr.child("NAVN").getValues().get(0).toString());
			String verneform = gr.child("VERNEFORM").getValues().get(0).toString();
			if(verneform.startsWith("B")) {
				tags.put("protect_class", "1");
			} else if(verneform.startsWith("D")) {
				tags.put("protect_class", "4");
			} else if(verneform.startsWith("G")) {
				tags.put("protect_class", "7");
			} else if(verneform.startsWith("L")) {
				tags.put("protect_class", "5");
			} else if(verneform.startsWith("M")) {
				// ???	
			} else if(verneform.startsWith("NM")) {
				tags.put("protect_class", "3");
			} else if(verneform.startsWith("NP")) {
				tags.put("protect_class", "2");
			} else if(verneform.startsWith("NR")) {
				tags.put("protect_class", "1");
			} else if(verneform.startsWith("P")) {
				tags.put("protect_class", "4");
			} else {
				System.err.println("Unknown VERNEFORM: " + verneform);
			}
			return TagsResult.tags(tags);
		}
		
	}

	{
		addTags(
				"Allmenning", "IGNORE",
				"Allmenninggrense", "IGNORE",
				"Naturverngrense", "",
				"Naturvernområde", new NatureReserveTags(),
				"Skytefelt","military=range",
				"Skytefeltgrense", ""
				);
	}
}
