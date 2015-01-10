package thomas001le.jsosiosm.osm;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class OSMXMLWriter implements OSMWriter {
	
	private Writer writer;
	private Formatter formatter;
	
	public OSMXMLWriter(OutputStream out) throws IOException {
		try {
			writer = new OutputStreamWriter(out, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 not supported, WTF?");
		}
		writer = new BufferedWriter(writer);
		formatter = new Formatter(writer, Locale.ROOT);
		
		putPreamble();
	}
	
	private void putLine(String line) throws IOException {
		writer.write(line);
		writer.write("\n");
	}
	
	private void putPreamble() throws IOException {
		putLine("<?xml version='1.0' encoding='UTF-8' ?>");
		putLine("<osm version='0.6'>");
	}
	
	private void putTags(Map<String, String> tags) throws IOException {
		if(tags == null)
			return;
					
		for(Map.Entry<String, String> tag : tags.entrySet()) {
			formatter.format("<tag k='%s' v='%s' />\n", tag.getKey(), tag.getValue());
		}
	}

	@Override
	public void putNode(double lon, double lat, long id,
			Map<String, String> tags, NodeMetadata meta) throws IOException {
		
		boolean visible = meta != null ? meta.visible : true;
		formatter.format("<node id='%d' lat='%.10f' lon='%.10f' version='1' visible='%b'", id, lat, lon, visible);
		if(meta != null) {
			if(meta.timestamp != null)
				formatter.format(" timestamp='%s'", meta.timestamp);
		}
		formatter.format(">\n");
		putTags(tags);
		putLine("</node>");
	}

	@Override
	public void putWay(Iterable<Long> nodes, long id, Map<String, String> tags) throws IOException {
		formatter.format("<way id='%d' version='1' visible='true'>\n", id);
		for(long node : nodes) {
			formatter.format("<nd ref='%d' />\n", node);
		}
		putTags(tags);
		putLine("</way>");
	}

	@Override
	public void putRelation(Relations relations, long id, String type,
			Map<String, String> tags)  throws IOException {
		formatter.format("<relation id='%d' version='1'>\n", id);
		for(int i = 0; i < relations.ids.size(); ++i) {
			formatter.format("<member ref='%d' role='%s' type='%s' />\n",
					relations.ids.get(i),
					relations.roles.get(i),
					relations.types.get(i));
		}
		formatter.format("<tag k='type' v='%s' />\n", type);
		putTags(tags);
		putLine("</relation>");
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws IOException {
		putLine("</osm>");
		writer.close();		
	}

	@Override
	public void comm(String msg) throws IOException {
		formatter.format("<!-- %s -->\n", msg);
	}

}
