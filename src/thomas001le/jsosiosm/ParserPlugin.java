package thomas001le.jsosiosm;

import java.io.IOException;
import java.util.Map;

public interface ParserPlugin {
	
	public TagsResult getTags(OSMGroup gr);
	
	public void doGeometry(SOSIToOSMParser parser, OSMGroup gr, Map<String, String> tags) throws IOException; 
	
	public void finish(SOSIToOSMParser parser) throws IOException;
}
