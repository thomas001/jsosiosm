package thomas001le.jsosiosm.sosi;

import java.io.IOException;

public abstract class SOSIParser<T extends Group> {
	
	protected SOSIReader<T> reader;

	abstract protected void handleGroup(T t);
	abstract protected void handleHeader(Group t);
	abstract protected void handleEnd();
	
	public SOSIParser(SOSIReader<T> reader) {
		this.reader = reader;
	}
	
	public void parse() throws IOException {
		Group header = reader.nextGroupItem(DefaultGroupFactory.INSTANCE);
		if( header.getName().equals("HODE"))
			handleHeader(header);
		else
			throw new RuntimeException("No header found");
		
		while(true) {
			T group = reader.nextGroupItem();
			if( group == null || group.getName().equals("SLUTT") ) {
				handleEnd();
				break;
			} else {
				handleGroup(group);
			}
		}
	}
	
}
