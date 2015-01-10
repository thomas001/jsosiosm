package thomas001le.jsosiosm.iso_8859_10;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class Decoder extends CharsetDecoder {
	
	protected Decoder(Charset cs) {
		super(cs, 1, 1);
	}

	static final String CHARMAP = 
			"\u00A0\u0104\u0112\u0122\u012A\u0128\u0136\u00A7\u013B\u0110\u0160\u0166\u017D\u00AD\u016A\u014A" +
			"\u00B0\u0105\u0113\u0123\u012B\u0129\u0137\u00B7\u013C\u0111\u0161\u0167\u017E\u2015\u016B\u014B" +
			"\u0100\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u012E\u010C\u00C9\u0118\u00CB\u0116\u00CD\u00CE\u00CF" +
			"\u00D0\u0145\u014C\u00D3\u00D4\u00D5\u00D6\u0168\u00D8\u0172\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF" +
			"\u0101\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u012F\u010D\u00E9\u0119\u00EB\u0117\u00ED\u00EE\u00EF" +
			"\u00F0\u0146\u014D\u00F3\u00F4\u00F5\u00F6\u0169\u00F8\u0173\u00FA\u00FB\u00FC\u00FD\u00FE\u0137";

	@Override
	protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
		while(in.remaining() > 0 && out.remaining() > 0) {
			int b = in.get() & 0xFF;
			char o;
			if(b < 128)
				o = (char) b;
			else if (b < 160)
				return CoderResult.malformedForLength(1);
			else
				o = CHARMAP.charAt(b-160);
			out.put(o);
		}
		if(in.remaining() == 0)
			return CoderResult.UNDERFLOW;
		else
			return CoderResult.OVERFLOW;
	}

}
