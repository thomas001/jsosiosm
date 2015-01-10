package thomas001le.jsosiosm.sosi;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternScanner {

	private Reader reader;
	private CharBuffer buffer;
	private boolean eof = false;
	private MatchResult last_match = null;

	
	public PatternScanner(Reader reader) throws IOException {
		this(reader, 4096);
	}
	
	public PatternScanner(Reader reader, int buffer_size) throws IOException {
		super();
		this.reader = reader;
		
		buffer = CharBuffer.allocate(buffer_size);
		buffer.limit(0);
		fillBuffer();
	}

	private boolean skipSpace() {
		while(buffer.remaining() > 0) {
			char c = buffer.get(buffer.position());
			if(Character.isWhitespace(c)) {
				buffer.get();
				continue;
			}
			if(c == '!') {
				while(c != '\n' && buffer.remaining() > 0)
					c = buffer.get();
				continue;
			}
			
			return false;
		}
		
		return true;
	}
	
	// TODO is this okay?
	private void maybeFillBuffer() throws IOException {
		for(int i = buffer.position(); i < buffer.limit(); ++i)
			if(buffer.get(i) == '\n')
				return;
		fillBuffer();
	}
	
	private void fillBuffer() throws IOException {
		CharBuffer slice = buffer.slice();
		buffer.flip();
		buffer.put(slice);
		buffer.limit(buffer.capacity());
		if(reader.read(buffer) == -1)
			eof = true;
		buffer.flip();
	}
	
	private void skipAndFill() throws IOException {
		while(!eof && skipSpace())
			fillBuffer();
		if(!eof)
			maybeFillBuffer();
	}
	
	public MatchResult peekToken(Pattern pat) throws IOException {
		if(last_match != null) {
			buffer.position( buffer.position() + last_match.end());
			skipAndFill();
			last_match = null;
		}
		Matcher res = pat.matcher(buffer);
		if(res.lookingAt())
			return res;
		else
			return null;
	}

	public MatchResult tryToken(Pattern pat) throws IOException {
		MatchResult res = peekToken(pat);
		if(res != null) {
			last_match = res;
			return res;
		} else {
			return null;
		}
	}
	
	public MatchResult nextToken(Pattern pat) throws IOException {
		MatchResult res = tryToken(pat);
		if(res != null)
			return res;
		else
			throw new RuntimeException("Syntax error");
	}
	
	
}
