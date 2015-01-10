package thomas001le.jsosiosm;

import java.util.Map;

public class TagsResult {
	
	public interface TagsGenerator {
		public TagsResult getTags(OSMGroup gr);
	}
	
	public static final TagsResult IGNORE = new TagsResult();
	public static final TagsResult WARN = new TagsResult();
	
	public static TagsResult tags(Map<String, String> t) {
		return new TagsResult(t);
	}
	
	public static TagsResult generator(TagsGenerator g) {
		return new TagsResult(g);
	}
	
	private Map<String, String> the_tags;
	private TagsGenerator the_generator;
	
	private TagsResult(Map<String,String> the_tags) {
		this.the_tags = the_tags;
		the_generator = null;
	}
	
	private TagsResult(TagsGenerator gen) {
		the_generator = gen;
		the_tags = null;
	}
	
	public boolean isTags() {
		return the_tags != null;
	}
	
	public boolean isGenerator() {
		return the_generator != null;
	}
	
	private TagsResult() { }

	public Map<String, String> getTags() {
		return the_tags;
	}
	
	public TagsGenerator getGenerator() {
		return the_generator;
	}
}
