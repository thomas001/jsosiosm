package thomas001le.jsosiosm.sosi;

import java.io.PrintStream;
import java.util.List;

public class Group {
	
	private String name;
	private long sn;
	private List<? extends Group> children;
	private List<Object> values;
	
	public String getName() {
		return name;
	}
	
	public long getSn() {
		return sn;
	}
	
	public List<? extends Group> getChildren() {
		return children;
	}
	
	public List<Object> getValues() {
		return values;
	}
	
	public Group getChild(String name) {
		for(Group cld : getChildren()) {
			if(cld.getName().equals(name))
				return cld;
		}
		return null;
	}
	
	public Group child(String name) {
		Group cld = getChild(name);
		if(cld != null)
			return cld;
		throw new RuntimeException("Child not found: " + name);
	}
	
	public Group(String name, long sn, List<? extends Group> children2, List<Object> values) {
		this.name = name;
		this.sn = sn;
		this.children = children2;
		this.values = values;
	}
	
	private void pprint(PrintStream out, String prefix) {
		out.printf("%s%s %d:", prefix, getName(), getSn());
		for(Object value: getValues() ) {
			out.printf(" %s", value);
		}
		out.println();
		String new_prefix = prefix + ".";
		for(Group cld : getChildren()) {
			cld.pprint(out, new_prefix);
		}
	}
	
	public void pprint() {
		pprint(System.out);
	}

	public void pprint(PrintStream out) {
		pprint(out, ".");
	}
	
}
