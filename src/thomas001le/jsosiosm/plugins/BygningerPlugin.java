package thomas001le.jsosiosm.plugins;

import java.util.HashMap;
import java.util.Map;

import thomas001le.jsosiosm.AbstractParserPlugin;
import thomas001le.jsosiosm.OSMGroup;
import thomas001le.jsosiosm.TagsResult;
import thomas001le.jsosiosm.TagsResult.TagsGenerator;

public class BygningerPlugin extends AbstractParserPlugin {
	
	static class BuildingTags implements TagsGenerator {
		
		static final private Map<Integer, Map<String,String>> BUILDING_TAGS = new HashMap<>();
		
		static void buildingGroup(String tags, Integer... vals) {
			for(int i:vals) {
				BUILDING_TAGS.put(i, totags(tags));
			}
		}
		
		static {
			buildingGroup("building=farm", 113, 123, 124, 163);
			buildingGroup("building=residential", 111, 112, 121, 122, 131, 133, 135, 136,
					141, 142, 143, 144, 145, 146, 152, 159,
					161, 162, 171, 199, 524 );
			buildingGroup("building=farm_auxiliary", 172, 181, 182, 183, 193, 216, 224, 241,
					243, 244, 245, 248, 249, 654, 830, 840 );
			buildingGroup("building=church", 671);
			// FIXME: many different buildings here
			buildingGroup("building=civic", 662, 672, 674, 675, 679);
			buildingGroup("building=hospital,amenity=hospital", 711, 712, 713);
			buildingGroup("amenity=nursing_home", 714, 719, 721, 722, 723, 729);
			buildingGroup("amenity=school,building=school", 613, 614, 615, 616, 619, 621, 629);
			buildingGroup("building=industrial,industrial=yes", 211, 212, 214, 219, 221, 229, 231, 232);
			buildingGroup("building=transportation", 411, 415, 419);
			buildingGroup("tourism=yes", 641, 643, 661, 669 ); // culture
			buildingGroup("sport=yes", 651, 652, 653);
			buildingGroup("building=yes", 151, 223, 233, 239, 312, 313, 319, 321,
					322, 323, 329, 330, 412, 416, 429, 431,
					439, 441, 449, 532, 611, 612, 623, 642,
					649, 655, 659, 663, 673, 690, 731, 732,
					739, 819, 821, 822, 823, 824, 825, 829,
					999);
			buildingGroup("amenity=townhall", 311);
			buildingGroup("tourism=hotel", 511, 512, 519, 521, 522, 523, 529);
			buildingGroup("tourism=alpine_hut", 956);
			buildingGroup("amenity=cafe", 531);
			buildingGroup("shop=kiosk", 533);
			buildingGroup("amenity=restaurant", 539);
			
		}
		

		@Override
		public TagsResult getTags(OSMGroup gr) {
			int byggnr = ((Number) gr.child("BYGGTYP_NBR").getValues().get(0)).intValue();
			Map<String,String> tags = BUILDING_TAGS.get(byggnr);
			if(tags == null)
				return TagsResult.WARN;
			return TagsResult.tags(tags);
		}
		
	}
	
	public BygningerPlugin() {
		addTags(
	               // BYGGTYP_NBR 
	               "Bygning", new BuildingTags(),
	               "Campingplass", "tourism=camp_site",
	               "Dam", "waterway=dam",
	               "Gruve", "man_made=mineshaft",
	               // TODO
	               "Hoppbakke", "IGNORE",
	               "KaiBrygge", "man_made=pier",
	               "Ledning", "power=cable",
	               "LuftledningLH", "power=line",
	               "Lysløype", "highway=piste,lit=yes",
	               "MastTele", "man_made=mast,tower:type=communication",
	               "Molo", "man_made=breakwater",
	               "Navigasjonsinstallasjon", "man_made=lighthouse",
	               "Parkeringsområde", "amenity=parking",
	               "Pir", "man_made=pier",
	               "Reingjerde", "barrier=fence",
	               "Rørgate", "man_made=pipeline,location=overground",
	               "Skitrekk", "arealway=yes",
	               "Skytebaneinnretning", "sport=shooting",
	               "SpesiellDetalj", "IGNORE",
	               "Takkant", "",
	               "Tank", "man_made=storage_tank",
	               "Tankkant", "",
	               "Taubane", "arealway=cable_car",
	               "Tårn", "man_made=tower",
	               "Vindkraftverk", "power=plant",
	               
	               
	               "Flytebrygge", "" // TODO
	               );

	}

}
