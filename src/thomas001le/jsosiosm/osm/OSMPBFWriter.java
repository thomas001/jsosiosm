package thomas001le.jsosiosm.osm;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.osmbinary.Fileformat.Blob;
import org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeader;
import org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlock;
import org.openstreetmap.osmosis.osmbinary.Osmformat.Node;
import org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlock;
import org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroup;
import org.openstreetmap.osmosis.osmbinary.Osmformat.Relation;
import org.openstreetmap.osmosis.osmbinary.Osmformat.StringTable;
import org.openstreetmap.osmosis.osmbinary.Osmformat.Way;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;

public class OSMPBFWriter implements OSMWriter {
	
	private OutputStream out;
	private ByteBuffer tmpbuf = ByteBuffer.allocate(4);
	
	private Map<String,Integer> stringtable = new HashMap<>();
	
	private PrimitiveGroup.Builder nodes = PrimitiveGroup.newBuilder(), ways = PrimitiveGroup.newBuilder(), relations = PrimitiveGroup.newBuilder();
	private int written_elements = 0;
	
	public OSMPBFWriter(OutputStream out) throws IOException {
		this.out = out;
		writeHeader();
	}
	
	private void writeMessage(String type, AbstractMessageLite msg) throws IOException {
		ByteString raw = msg.toByteString();
		
		Blob.Builder blob = Blob.newBuilder();
		blob.setRawSize(raw.size());
		blob.setRaw(raw);
		Blob the_blob = blob.build();
		
		BlobHeader.Builder head = BlobHeader.newBuilder();
		head.setDatasize(the_blob.getSerializedSize());
		head.setType(type);
		BlobHeader the_head = head.build();
		
		tmpbuf.rewind();
		tmpbuf.putInt(the_head.getSerializedSize());
		
		out.write(tmpbuf.array());
		the_head.writeTo(out);
		the_blob.writeTo(out);
	}
	
	private void writeData() throws IOException {
		PrimitiveBlock.Builder block = PrimitiveBlock.newBuilder();
		block.addPrimitivegroup(nodes);
		block.addPrimitivegroup(ways);
		block.addPrimitivegroup(relations);
		
		String[] helper_table = new String[stringtable.size()+1];
		for(Map.Entry<String, Integer> e : stringtable.entrySet()) {
			helper_table[e.getValue()] = e.getKey();
		}
		StringTable.Builder st = StringTable.newBuilder();
		st.addS(ByteString.EMPTY);
		for(int i = 1; i < helper_table.length; ++i)
			st.addS(ByteString.copyFromUtf8(helper_table[i]));
		block.setStringtable(st);
		
		writeMessage("OSMData", block.build());
		
		nodes.clear();
		ways.clear();
		relations.clear();

	}
	
	private void writeHeader() throws IOException {
		HeaderBlock.Builder head = HeaderBlock.newBuilder();
		head.addRequiredFeatures("OsmSchema-V0.6");
		writeMessage("OSMHeader", head.build());
	}
	
	private void afterElement(AbstractMessageLite msg) throws IOException {
		written_elements += msg.getSerializedSize();
		if(written_elements > 8 * 1024 * 1024) {
			writeData();
			written_elements = 0;
		}
		
	}
	
	private int stringId(String s) {
		Integer lid = stringtable.get(s);
		if(lid != null)
			return lid;
		int id = stringtable.size() + 1;
		stringtable.put(s, id);
		return id;
	}
	

	@Override
	public void putNode(double lon, double lat, long id,
			Map<String, String> tags, NodeMetadata meta) throws IOException {
		
		Node.Builder node = Node.newBuilder();
		node.setId(id);
		node.setLon( (long) (lon * 1e7) );
		node.setLat( (long) (lat * 1e7));
		
		if (tags != null) {
			for (Map.Entry<String, String> tag : tags.entrySet()) {
				node.addKeys(stringId(tag.getKey()));
				node.addVals(stringId(tag.getValue()));
			}
		}
		
		Node the_node = node.build();
		nodes.addNodes(the_node);
		
		afterElement(the_node);
	}

	@Override
	public void putWay(Iterable<Long> nodes, long id, Map<String, String> tags)
			throws IOException {
		
		Way.Builder way = Way.newBuilder();
		way.setId(id);
		
		long m = 0;
		for(long n : nodes) {
			way.addRefs(n - m);
			m = n;
		}

		if (tags != null) {
			for (Map.Entry<String, String> tag : tags.entrySet()) {
				way.addKeys(stringId(tag.getKey()));
				way.addVals(stringId(tag.getValue()));
			}
		}
		
		Way the_way = way.build();
		ways.addWays(the_way);
		
		afterElement(the_way);
	}

	@Override
	public void putRelation(Relations relations, long id, String type,
			Map<String, String> tags) throws IOException {
		
		Relation.Builder rel = Relation.newBuilder();
		rel.setId(id);
		
		long m = 0;
		for(int i = 0; i < relations.ids.size(); ++i) {
			long n = relations.ids.get(i);
			rel.addMemids(n - m);
			rel.addRolesSid(stringId(relations.roles.get(i)));
			String ty = relations.types.get(i);
			rel.addTypes(Relation.MemberType.valueOf(ty.toUpperCase()));
			m = n;
		}

		rel.addKeys(stringId("type"));
		rel.addVals(stringId(type));
		if (tags != null) {
			for (Map.Entry<String, String> tag : tags.entrySet()) {
				rel.addKeys(stringId(tag.getKey()));
				rel.addVals(stringId(tag.getValue()));
			}
		}
		
		Relation the_rel = rel.build();
		this.relations.addRelations(the_rel);
		
		afterElement(the_rel);
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		writeData();
		out.close();
	}

	@Override
	public void comm(String msg) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
