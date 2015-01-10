package thomas001le.jsosiosm.iso_8859_10;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class ISO885910Charset extends Charset {

	public ISO885910Charset() {
		super("ISO-8859-10", new String[]{"ISO885910","ISO8859-10","iso885910","iso8859-10","iso-8859-10"});
	}

	@Override
	public boolean contains(Charset arg0) {
		return false;
	}

	@Override
	public CharsetDecoder newDecoder() {
		return new Decoder(this);
	}

	@Override
	public CharsetEncoder newEncoder() {
		return new Encoder(this);
	}

}
