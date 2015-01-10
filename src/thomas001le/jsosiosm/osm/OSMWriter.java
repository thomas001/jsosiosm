package thomas001le.jsosiosm.osm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface OSMWriter {
	
	static class Relations {
		public List<Long> ids = new ArrayList<Long>();
		public List<String> roles = new ArrayList<String>();
		public List<String> types = new ArrayList<String>();
		
		public void addMember(long id, String role, String type) {
			ids.add(id);
			roles.add(role);
			types.add(type);
		}
	}
	
	static class NodeMetadata {
		public boolean visible = true;
		public String timestamp = null;
	}
	
	public void putNode(double lon, double lat, long id, Map<String,String> tags, NodeMetadata meta) throws IOException;
	public void putWay(Iterable<Long> nodes, long id, Map<String,String> tags)  throws IOException;
	public void putRelation(Relations relations, long id, String type, Map<String,String> tags)  throws IOException;
	
	public void flush()  throws IOException;
	
	public void close() throws IOException;
	public void comm(String msg) throws IOException;
}
