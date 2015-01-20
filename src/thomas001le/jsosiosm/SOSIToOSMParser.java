package thomas001le.jsosiosm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

import thomas001le.jsosiosm.osm.OSMPBFWriter;
import thomas001le.jsosiosm.osm.OSMWriter;
import thomas001le.jsosiosm.osm.OSMWriter.Relations;
import thomas001le.jsosiosm.osm.OSMXMLWriter;
import thomas001le.jsosiosm.sosi.Group;
import thomas001le.jsosiosm.sosi.SOSIParser;
import thomas001le.jsosiosm.sosi.SOSIReader;

import com.carrotsearch.hppc.LongLongMap;
import com.carrotsearch.hppc.LongLongOpenHashMap;

public class SOSIToOSMParser extends SOSIParser<OSMGroup> {
	
	private OSMWriter writer;
	
	private Map<Long,Long> sosi_ids = new HashMap<Long,Long>();
	private long idcounter = 0;
	
	//private Map<Point2L, Long> node_map = new PatriciaTrie<>(Point2LKeyAnalyser.INSTANCE);//new HashMap<Point2L, Long>();
	//private Map<Point2L, Long> node_map = new HashMap<Point2L, Long>();
	//private Map<Point2L, Long> node_map = new TreeMap<Point2L, Long>();
	final static long INT_MASK = (1L << 32) - 1;
	private LongLongMap long_node_map = new LongLongOpenHashMap();
	private Map<Point2L,Long> extra_node_map = new HashMap<>();
	private boolean use_node_map = true;
	
	// private PJ source_projection;
	// private PJ target_projection = new PJ("+proj=latlong +datum=WGS84");
	private CoordinateReferenceSystem source_crs;
	private CoordinateReferenceSystem target_crs = new CRSFactory().createFromParameters(null, "+proj=latlong +datum=WGS84");
	private CoordinateTransform coordinate_transform;
	
	private double origin_x = 0, origin_y = 0;
	private double unit = 1;
	
	// private double[] transform_buffer = new double[2];
	
	//PatriciaTrie<Point2L, Long> trie = new PatriciaTrie<>(Point2LKeyAnalyser.INSTANCE);
	
	private List<ParserPlugin> plugins = new ArrayList<ParserPlugin>();

	private Set<String> unhandled_groups = new HashSet<>();
	
	static final Map<Integer,String> SOSI_COORDINATE_CODES = new HashMap<Integer,String>();
	static {
		Map<Integer, String> tr = SOSI_COORDINATE_CODES;

		tr.put(1,
				"+proj=tmerc +lat_0=58 +lon_0=-4.666666666666667 +k=1 +x_0=0 +y_0=0 +a=6377492.018 +b=6356173.508712696 +towgs84=278.3,93,474.5,7.889,0.05,-6.61,6.21 +pm=oslo +units=m +no_defs ");
		tr.put(2,
				"+proj=tmerc +lat_0=58 +lon_0=-2.333333333333333 +k=1 +x_0=0 +y_0=0 +a=6377492.018 +b=6356173.508712696 +towgs84=278.3,93,474.5,7.889,0.05,-6.61,6.21 +pm=oslo +units=m +no_defs ");
		tr.put(3,
				"+proj=tmerc +lat_0=58 +lon_0=0 +k=1 +x_0=0 +y_0=0 +a=6377492.018 +b=6356173.508712696 +towgs84=278.3,93,474.5,7.889,0.05,-6.61,6.21 +pm=oslo +units=m +no_defs ");
		tr.put(4,
				"+proj=tmerc +lat_0=58 +lon_0=2.5 +k=1 +x_0=0 +y_0=0 +a=6377492.018 +b=6356173.508712696 +towgs84=278.3,93,474.5,7.889,0.05,-6.61,6.21 +pm=oslo +units=m +no_defs ");
		tr.put(5,
				"+proj=tmerc +lat_0=58 +lon_0=6.166666666666667 +k=1 +x_0=0 +y_0=0 +a=6377492.018 +b=6356173.508712696 +towgs84=278.3,93,474.5,7.889,0.05,-6.61,6.21 +pm=oslo +units=m +no_defs ");
		tr.put(6,
				"+proj=tmerc +lat_0=58 +lon_0=10.16666666666667 +k=1 +x_0=0 +y_0=0 +a=6377492.018 +b=6356173.508712696 +towgs84=278.3,93,474.5,7.889,0.05,-6.61,6.21 +pm=oslo +units=m +no_defs ");
		tr.put(7,
				"+proj=tmerc +lat_0=58 +lon_0=14.16666666666667 +k=1 +x_0=0 +y_0=0 +a=6377492.018 +b=6356173.508712696 +towgs84=278.3,93,474.5,7.889,0.05,-6.61,6.21 +pm=oslo +units=m +no_defs ");
		tr.put(8,
				"+proj=tmerc +lat_0=58 +lon_0=18.33333333333333 +k=1 +x_0=0 +y_0=0 +a=6377492.018 +b=6356173.508712696 +towgs84=278.3,93,474.5,7.889,0.05,-6.61,6.21 +pm=oslo +units=m +no_defs ");
		// UTM ZONE 31-36
		tr.put(21, "+proj=utm +zone=31 +ellps=GRS80 +units=m +no_defs ");
		tr.put(22, "+proj=utm +zone=32 +ellps=GRS80 +units=m +no_defs ");
		tr.put(23, "+proj=utm +zone=33 +ellps=GRS80 +units=m +no_defs ");
		tr.put(24, "+proj=utm +zone=34 +ellps=GRS80 +units=m +no_defs ");
		tr.put(25, "+proj=utm +zone=35 +ellps=GRS80 +units=m +no_defs ");
		tr.put(26, "+proj=utm +zone=36 +ellps=GRS80 +units=m +no_defs ");
		// UTM ZONE 31-36 / ED50
		tr.put(31, "+proj=utm +zone=31 +ellps=intl +units=m +no_defs ");
		tr.put(32, "+proj=utm +zone=32 +ellps=intl +units=m +no_defs ");
		tr.put(33, "+proj=utm +zone=33 +ellps=intl +units=m +no_defs ");
		tr.put(34, "+proj=utm +zone=34 +ellps=intl +units=m +no_defs ");
		tr.put(35, "+proj=utm +zone=35 +ellps=intl +units=m +no_defs ");
		tr.put(36, "+proj=utm +zone=36 +ellps=intl +units=m +no_defs ");
		// WSG84
		tr.put(84, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs ");

	}
	
	public SOSIToOSMParser(String filename) throws IOException {
		super(null);
		OutputStream file = new FileOutputStream(filename);
		if(filename.endsWith(".pbf"))
			writer = new OSMPBFWriter(file);
		else
			writer = new OSMXMLWriter(file);
	}
	
	public void reset(InputStream in) throws IOException {
		reader = new SOSIReader<>(in, new SOSIToOSMGroupFactory());
		//source_projection = null;
		source_crs = null;
		sosi_ids.clear();
		origin_x = 0;
		origin_y = 0;
		unit = 1;
	}

	public Point2D transform(Point2L p) {
		//transform_buffer[0] = p.x * unit + origin_x;
		//transform_buffer[1] = p.y * unit + origin_y;
		//try {
		//	source_projection.transform(target_projection, 2, transform_buffer, 0, 1);
		//} catch(PJException e) {
		//	throw new RuntimeException(e);
		//}
		
		// TODO: optimize me
		ProjCoordinate src = new ProjCoordinate(), dst = new ProjCoordinate();
		src.x = p.x * unit + origin_x;
		src.y = p.y * unit + origin_y;
		src.z = 0;
		coordinate_transform.transform(src, dst);
		return new Point2D(dst.x, dst.y);
	}

	@Override
	protected void handleGroup(OSMGroup t) {
		for(ParserPlugin plugin : plugins) {
			TagsResult res = plugin.getTags(t);
			
			while(res != null && res.isGenerator())
				res = res.getGenerator().getTags(t);
			
			if(res == null)
				continue;
			if(res == TagsResult.IGNORE) {
				return;
			}
			if(res == TagsResult.WARN) {
				System.err.println("Warned while parsing group:");
				t.pprint(System.err);
				System.err.println();
				return;
			}
			
			Map<String,String> 	tags = res.getTags();
			
			try {
				plugin.doGeometry(this, t, tags);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return;
		}
		
		unhandled_groups.add(String.format("%s/%s", t.getObjType(), t.getName()));
	}
	
	@Override
	protected void handleHeader(Group t) {
		
		Group transpar = t.getChild("TRANSPAR");
		for(Group cld : transpar.getChildren()) {
			if(cld.getName().equals("KOORDSYS")) {
				String trans = SOSI_COORDINATE_CODES.get(((Number) cld.getValues().get(0)).intValue());
				if(trans == null)
					throw new RuntimeException("Invalid coordinate system");
				//source_projection = new PJ(trans);
				source_crs = new CRSFactory().createFromParameters(null, trans);
				coordinate_transform = new CoordinateTransformFactory().createTransform(source_crs, target_crs);
			} else if(cld.getName().equals("ENHET")) {
				unit = ((Number) cld.getValues().get(0)).doubleValue();
			} else if(cld.getName().equals("ORIGO-NØ")) {
				origin_y = ((Number) cld.getValues().get(0)).doubleValue();
				origin_x = ((Number) cld.getValues().get(1)).doubleValue();
			} else
				throw new RuntimeException("Unknown TRANSPAR field: " + cld.getName());
		}
		
	}
	
	public void registerPlugin(ParserPlugin plugin) {
		plugins.add(plugin);
	}

	@Override
	protected void handleEnd() {
		try {
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void close() throws IOException {
		
		for(ParserPlugin plugin : plugins)
			plugin.finish(this);
		
		writer.close();
		
		for(String s : unhandled_groups) {
			System.err.format("Unhandled group %s\n", s);
		}
	}
	
	public boolean useCache() {
		return use_node_map;
	}
	
	public void useCache(boolean val) {
		use_node_map = val;
	}
	
	private long keyForPoint(Point2L p) {
		return (p.x & INT_MASK) | (( p.y & INT_MASK) << 32);
	}
	
	private Long getFromNodeCache(Point2L p) {
		if(p.x < Integer.MIN_VALUE || p.x > Integer.MAX_VALUE || p.y < Integer.MIN_VALUE || p.y > Integer.MAX_VALUE) {
			return extra_node_map.get(p);
		}
		
		long id = long_node_map.getOrDefault(keyForPoint(p), Long.MAX_VALUE);
		if(id == Long.MAX_VALUE)
			return null;
		else
			return id;
	}
	
	private void putInNodeCache(Point2L p, long id) {
		if(p.x < Integer.MIN_VALUE || p.x > Integer.MAX_VALUE || p.y < Integer.MIN_VALUE || p.y > Integer.MAX_VALUE) {
			extra_node_map.put(p, id);
		} else {
			long_node_map.put(keyForPoint(p), id);
		}
	}

	
	public long putNode(Point2L p, long sn, Map<String,String> tags) throws IOException {
		if(use_node_map) {
			Long old_id = getFromNodeCache(p);
			if(old_id != null) {
				return old_id.longValue();
			}
		}
		
		long id = idForSosiId(sn);
		if(use_node_map)
			putInNodeCache(p, id);
		
		Point2D q = transform(p);
		writer.comm(String.format("SOSI ID: %d", sn));
		writer.putNode(q.x, q.y, id, tags, null);
		return id;
	}
	
	private long getNewId() {
		++idcounter;
		return -idcounter;
	}
	
	public long idForSosiId(long sn) {
		if(sn == 0) {
			return getNewId();
		}
		
		Long id = sosi_ids.get(sn);
		if(id == null) {
			long id2 = getNewId();
			sosi_ids.put(sn, id2);
			return id2;
		} else {
			return id;
		}
	}

	public long putWay(Iterable<Long> nodes, long sn, Map<String, String> tags) throws IOException {
		long id = idForSosiId(sn);
		writer.comm(String.format("SOSI ID: %d", sn));
		writer.putWay(nodes, id, tags);
		return id;
	}

	public long putRelation(Relations relations, long sn,
			String type, Map<String, String> tags) throws IOException {
		long id = idForSosiId(sn);
		writer.comm(String.format("SOSI ID: %d", sn));
		writer.putRelation(relations, id, type, tags);
		return id;
	}

}
