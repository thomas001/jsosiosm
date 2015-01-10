package thomas001le.jsosiosm.sosi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import thomas001le.jsosiosm.iso_8859_10.ISO885910Charset;

public class SOSIReader<T extends Group> {
	/*
	 *     elementnavn_re = re.compile(r'^(?![\d])[\d\w\-_]+')
    level_re = re.compile(r'^\.+')
    # fix me
    #verdi_re = re.compile(r'^(?:\((?:\s*:[+-]?[0-9]+)+\s*\)|(?![.(])[^\s]+)')
    verdi_re = re.compile(r'^(?![.])[^\s]+')
    reference_re = re.compile(r'^(:[+\-]?[0-9]+)|\(([+\-]?[0-9]+(?:[\s]+[+\-]?[0-9]+)*)\)')
    seriennummer_re = re.compile(r'^([0-9]+):')

	 */
	
	private static final Pattern ELEMENTNAME_PAT = Pattern.compile("(\\.+)([\\w^\\d][\\w\\-_]*)",Pattern.UNICODE_CHARACTER_CLASS);
	private static final Pattern SERIALNUM_RE = Pattern.compile("([\\d]+):");
	
	private static final Pattern VALUE_RE = Pattern.compile("[^\\s.][^\\s]*", Pattern.UNICODE_CHARACTER_CLASS);
	private static final Pattern NUMBER_RE = Pattern.compile("[+\\-]?[0-9]+");
	private static final Pattern TRUE_DOUBLE_RE = Pattern.compile("[+\\-]?[0-9]+\\.[0-9]+");
	
	//private Scanner scanner;
	//private int line_pos;
	
	private GroupFactory<T> factory;
	private PatternScanner scanner;
	
	public void setGroupFactory(GroupFactory<T> factory) {
		this.factory = factory;
	}
	
	private Object nextValue() throws IOException {
		MatchResult res;
		
		res = scanner.tryToken(TRUE_DOUBLE_RE);
		if(res != null)
			return Double.parseDouble(res.group());
		
		res = scanner.tryToken(NUMBER_RE);
		if(res != null)
			return Long.parseLong(res.group());
		
		res = scanner.tryToken(VALUE_RE);
		if(res != null)
			return res.group();
		
		return null;
	}
	
	public <U extends Group> U nextGroupItem(GroupFactory<U> factory) throws IOException {
		MatchResult match = scanner.tryToken(ELEMENTNAME_PAT);
		if( match != null ) {
			int level = match.end(1) - match.start(1);
			String name = match.group(2);
			
			long sn = 0;
			match = scanner.tryToken(SERIALNUM_RE);
			if( match != null ) {
				sn = Long.parseUnsignedLong(match.group(1));
			}
			
			List<Object> values = new ArrayList<Object>();
			for(Object o = nextValue(); o != null; o = nextValue())
				values.add(o);
			
			List<Group> children = new ArrayList<Group>();
			GroupFactory<? extends Group> child_factory = factory.childrenGroupFactory(name, sn, values);
			while( (match = scanner.peekToken(ELEMENTNAME_PAT)) != null ) {
				int next_level = match.end(1) - match.start(1);
				if( next_level <= level )
					break;
				if( next_level > level + 1)
					throw new RuntimeException("Syntax Error");
				
				children.add(nextGroupItem(child_factory));
			}
			
			return factory.makeGroup(name, sn, children, values);
			
		} else {
			throw new RuntimeException("References not supported!");
		}
	}
	
	public T nextGroupItem() throws IOException {
		return nextGroupItem(factory);
	}
	
	public SOSIReader(InputStream input, GroupFactory<T> factory) throws IOException {
		Reader reader = new InputStreamReader(input, new ISO885910Charset());
		this.scanner = new PatternScanner(reader);
		this.factory = factory;
	}

	
}
