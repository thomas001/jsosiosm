package thomas001le.jsosiosm.plugins;

import java.util.HashMap;
import java.util.Map;

import thomas001le.jsosiosm.AbstractParserPlugin;
import thomas001le.jsosiosm.OSMGroup;
import thomas001le.jsosiosm.TagsResult;
import thomas001le.jsosiosm.TagsResult.TagsGenerator;

public class ArealdekkePlugin extends AbstractParserPlugin {
	
	private static class ElvBekkGenerator implements TagsGenerator {

		@Override
		public TagsResult getTags(OSMGroup gr) {
			long vannbr = (Long) gr.getChild("VANNBR").getValues().get(0);
			Map<String,String> tags = new HashMap<>();
			if(vannbr == 2) {
				tags.put("waterway", "stream");
				tags.put("width", "2");
			} else if(vannbr == 3) {
				tags.put("waterway", "river");
				tags.put("width", "9");
			} else if(vannbr == 4) {
				tags.put("waterway", "river");
				tags.put("width", "15");
			} else
				throw new RuntimeException("Invalid river width");
			return TagsResult.tags(tags);
		}
		
	}

	public ArealdekkePlugin() {

		addTags(
	               // K
	               "Alpinbakke", "landuse=winter_sports",
	               // K
	               "Arealbrukgrense" , "", // tag me?
	               // downtown block
	               "BymessigBebyggelse",  "landuse=commercial",
	               "DyrketMark",  "landuse=farmland" ,
	               // todo parse VANNBR
	               "ElvBekk/KURVE" ,  new ElvBekkGenerator() ,
	               "ElvBekk/FLATE" ,  "waterway=riverbank" ,
	               "ElvBekkKant" , "", // tag me?
	               "ElveElvSperre", "" ,
	               "FerskvannTørrfall", "IGNORE",
	               "FerskvannTørrfallkant", "", // FIXME
	               // drains ? borders in reality
	               "Flomløpkant" , "waterway=drain",
	               "Golfbane", "leisure=golf_course",
	               "Gravplass",  "landuse=cemetery" ,
	               "HavElvSperre", "",
	               "Havflate",   "natural=water"  ,               
	               "HavInnsjøSperre", "",
	               "Hyttefelt", "WARN",
	               "Industriområde" ,  "landuse=industrial",
	               "Innsjø" ,  "natural=water,water=lake" ,
	               "InnsjøElvSperre" , "",
	               "InnsjøInnsjøSperre" , "",
	               "Innsjøkant" ,  "natural=coastline",
	               "InnsjøkantRegulert" , "waterway=dam",
	               "Kystkontur" ,  "natural=coastline" ,
	               // todo, parse IATA,...
	               "Lufthavn" ,  " aeroway=aerodrome",
	               "Myr" ,  "natural=wetland", 
	               "Park" ,  "leisure=park",
	               "Rullebane" ,  "areoway=runway",
	               "Skjær",   "place=islet" ,
	               "Skog",  "natural=wood" ,
	               "SnøIsbre" ,  "natural=glacier" ,
	               "SportIdrettPlass" ,  "leisure=pitch",
	               "Steinbrudd",  "landuse=quarry" ,
	               "Steintipp",  "man_made=spoil_heap", // not in mkgmap
	               "TettBebyggelse" ,  "landuse=residential" ,
	               "Tregruppe",  "natural=tree" , // not mkgmap
	               "ÅpentOmråde",  "natural=heath" 
	               );

	}
	
}
