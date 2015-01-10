package thomas001le.jsosiosm.iso_8859_10;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.Map;

public class Encoder extends CharsetEncoder {
	
	private static Map<Character,Integer> makeCodeMap() {
		HashMap<Character, Integer> map = new HashMap<Character,Integer>();
		for(int i = 0; i < 256 - 160; ++i) {
			map.put(Decoder.CHARMAP.charAt(i+160), i);
		}
		return map;
	}
	
	private static Map<Character,Integer> CODEMAP = makeCodeMap();

	protected Encoder(Charset cs) {
		super(cs, 1, 1);
	}

	@Override
	protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
		while(in.remaining() > 0 && out.remaining() > 0) {
			char c = in.get();
			int o;
			if(c < 128) {
				o = c;
			} else {
				Integer i = CODEMAP.get(c);
				if(i != null) {
					o = i;
				} else {
					return CoderResult.unmappableForLength(1);
				}
			}
			out.put( (byte) o);
		}
		
		if(in.remaining() == 0)
			return CoderResult.UNDERFLOW;
		else
			return CoderResult.OVERFLOW;		
	}

}
