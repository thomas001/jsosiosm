package thomas001le.jsosiosm.iso_8859_10;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ISO885910Provider extends CharsetProvider {
	
	static private List<Charset> charsets = new ArrayList<>();
	
	static {
		charsets.add(new ISO885910Charset());
	}

	public ISO885910Provider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Charset charsetForName(String name) {
		if(name.replace("-", "").toLowerCase(Locale.ROOT).equals("iso885910"))  
			return charsets.get(0);
		return null;
	}

	@Override
	public Iterator<Charset> charsets() {
		return charsets.iterator();
	}

}
