package thomas001le.jsosiosm.plugins;

import java.util.HashMap;
import java.util.Map;

import thomas001le.jsosiosm.AbstractParserPlugin;
import thomas001le.jsosiosm.OSMGroup;
import thomas001le.jsosiosm.TagsResult;
import thomas001le.jsosiosm.TagsResult.TagsGenerator;
import thomas001le.jsosiosm.sosi.Group;

public class BygningerPlugin extends AbstractParserPlugin {
	
	static class BuildingTags implements TagsGenerator {
		
		static final private Map<Integer, Map<String,String>> BUILDING_TAGS = new HashMap<>();
		
		private static void buildingGroup(String tags, Integer... vals) {
			for(int i:vals) {
				BUILDING_TAGS.put(i, totags(tags));
			}
		}
		
		private static void byg(int nr, String tags) {
			BUILDING_TAGS.put(nr, totags(tags));
		}
		
		static {
			buildingGroup("building=farm", 113, 123, 124, 163);
			buildingGroup("building=residential", 111, 112, 121, 122, 131, 133, 135, 136,
					141, 142, 143, 144, 145, 146, 152, 159,
					161, 162, 171, 199, 524 );
			buildingGroup("building=farm_auxiliary", 172, 181, 182, 183, 193, 216, 224, 241,
					243, 244, 245, 248, 249, 654, 830, 840 );
			
			buildingGroup("building=industrial", 211, 212, 214, 219, 221, 229, 231, 232);
			
			/***
			 * TRANSPORT AND COMMUNICATIONS AND COMMUNICATION 
			 * Expedition Building, terminal 
			 * 411 Expedition Building Terminal,   control tower 
			 * 412 Rail and subway station 
			 * 415 freight terminal 
			 * 416 Postterminal 
			 * 419 Other ekspedisjon- and terminal building
			 * Telecommunications Building 
			 * 429 Telecommunications Building
			 * Garages and hangar building 
			 * 431 Parking 
			 * 439 Other garage / hangar
			 * building Roads and traffic supervision building 
			 * 441 Traffic
			 * Supervision Building 
			 * 449 Other road and traffic supervision
			 * building
			 */
			byg(412,"railway=station");
			byg(431,"amenity=parking");
			
			/***
			 * HOTEL AND RESTAURANT 
			 * Hotel Building 
			 * 511 Hotel Building 
			 * 512 Motel
			 * Building 
			 * 519 Other hotel building Building for accommodation 
			 * 521
			 * Hospice, B 
			 * 522 hostel -feriehjem 
			 * 523 Appartement 
			 * 524 Camping- /
			 * rental cottage 
			 * 529 Other building for accommodation estaurant
			 * Building 
			 * 531 restaurant building, cafe building 
			 * 532 Central
			 * Kitchen, canteen building 
			 * 533 Fast, kiosk building 
			 * 539 Other
			 * restaurant building
			 */
			byg(511, "toursim=hotel");
			byg(512, "tourism=motel");
			byg(519, "tourism=hotel"); //FIXME: other hotel?
			byg(521, "tourism=guest_house");
			byg(522, "tourism=hostel");
			byg(523, "tourism=appartment");
			byg(524, "tourism=charlet");
			//FIXME: 529 = ?
			byg(531, "amenity=restaurant");
			byg(532, "amenity=restaurant"); // FIXME: fastfood?
			byg(533, "shop=kiosk");
			byg(539, "amenity=restaurant"); // FIXME: other restaurant?
			
			/**
			 * HEALTH BUILDING CULTURE AND RESEARCH school building 
			 * 611
			 * Playground 
			 * 612 Kindergarten
			 *  613 Primary
			 *   614 Ungdomskole
			 *    615
			 * Combined primary and secondary school 
			 * 616 High school 
			 * 619 Other
			 * school building Universitet- and college building 
			 * 621 Univ. /
			 * College w / auditorium, reading 
			 * 623 Laboratory Building 
			 * 629 Other
			 * university / college and   research building
			 * 
			 * Museum and library building 
			 * 641 Museum, gallery 
			 * 642 Library,
			 * Media Library 
			 * 643 Zoological / Botanical Gardens (buildings) 
			 * 649
			 * Other museum / library building
			 */
			
			byg(611, "leisure=playground");
			byg(612, "amenity=kindergarden");
			buildingGroup("amenity=school", 613, 614, 615, 616, 619);
			buildingGroup("amenity=university", 621,623,629);
			byg(641, "amenity=museum");
			byg(642, "amenity=library");
			byg(643, "tourism=zoo");
			// 649 ???
			byg(651, "leisure=sports_centre");
			byg(652, "leisure=ice_rink,sport=ice_skating");
			byg(653, "leisure=swimming_pool");
			// 654 tribuene, umkleide?
			byg(655, "leisure=sports_centre,sport=fitness");
			// 659 other sports
			byg(661, "amenity=theatre");
			byg(662, "amenity=community_centre");
			byg(663, "amenity=nightclub");
			// 669 other culture building
			byg(671, "amenity=place_of_worship,building=church,religion=christian");
			byg(672, "amenity=parish_hall");
			byg(673, "amenity=crematorium");
			// jewish or moslem...nice one
			byg(674, "amenity=place_of_worship");
			// kloster
			byg(674, "amenity=place_of_worship,building=monastery");
			// 679 other religious buildings
			
			buildingGroup("amenity=hospital,building=hospital", 711, 712, 713, 714, 719);
			byg(721, "amenity=nursing_home");
			byg(722, "amenity=retirement_home");
			byg(723, "amenity=hospital"); // rehabilitation center
			byg(729, "amenity=nursing_home"); // other nursing home
			buildingGroup("amenity=clinic", 731, 732, 739);
			
			byg(819, "amenity=prison");
			byg(821, "amenity=police");
			byg(822, "amenity=fire_station");
			byg(823, "man_made=lighthouse");
			byg(824, "man_made=tower,tower:type=radar");
			byg(825, "military=bunker");
			// 829 other contigency building
			
			byg(830, "historic=monument");
			byg(840, "amenity=toilets");

			byg(956, "tourism=alpine_hut"); // TODO: parse additional info
			
			//999 unknown
			
		}
		

		@Override
		public TagsResult getTags(OSMGroup gr) {
			int byggnr = ((Number) gr.child("BYGGTYP_NBR").getValues().get(0)).intValue();
			Map<String,String> tags = BUILDING_TAGS.get(byggnr);
			if(tags == null)
				return TagsResult.WARN;
			Group name = gr.getChild("NAVN");
			if(name != null) {
				StringBuilder nb = new StringBuilder();
				for(Object o : gr.getValues()) {
					nb.append(o.toString());
					nb.append(' ');
				}
				tags.put("name", nb.toString());
			}
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
