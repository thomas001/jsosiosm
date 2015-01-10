package thomas001le.jsosiosm;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import thomas001le.jsosiosm.sosi.Group;

public abstract class OSMGroup extends Group {
	
	class PointIterator implements Iterator<Point2L> {
		
		private Iterator<? extends Group> group_iterator;
		private Iterator<Object> value_iterator;
		
	
		private PointIterator(Iterator<? extends Group> group_iterator,
				Iterator<Object> value_iterator) {
			super();
			this.group_iterator = group_iterator;
			this.value_iterator = value_iterator;
		}

		private boolean nextGroup() {
			while(group_iterator.hasNext()) {
				Group gr = group_iterator.next();
				if( gr.getName().equals("NØ")) {
					value_iterator = gr.getValues().iterator();
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean hasNext() {
			while( !value_iterator.hasNext() ) {
				if( !nextGroup())
					return false;
			}
			return true;
		}

		@Override
		public Point2L next() {
			hasNext();
			long y = (Long) value_iterator.next();
			long x = (Long) value_iterator.next();
			return new Point2L(x,y);
		}
		
	}

	public OSMGroup(String name, long sn, List<? extends Group> children, List<Object> values) {
		super(name, sn, children, values);
	}
	
	public Iterable<Point2L> points() {
		return new Iterable<Point2L>() {
			public Iterator<Point2L> iterator() {
				return pointsIterator();
			}
		};
	}
	
	public Iterator<Point2L> pointsIterator() {
		return new PointIterator(getChildren().iterator(), getValues().iterator());
	}
	
	public abstract long osm(SOSIToOSMParser parser, Map<String, String> tags) throws IOException;

	public String getObjType() {
		return getChild("OBJTYPE").getValues().get(0).toString();
	}

}
