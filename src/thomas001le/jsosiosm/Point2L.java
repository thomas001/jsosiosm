package thomas001le.jsosiosm;


public class Point2L implements Cloneable , Comparable<Point2L> 
{
	public long x;
	public long y;
	
	Point2L(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public Point2L clone() {
		return new Point2L(x,y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (x ^ (x >>> 32));
		result = prime * result + (int) (y ^ (y >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point2L other = (Point2L) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public int compareTo(Point2L o) {
		if(x != o.x)
			return Long.compare(x, o.x);
		return Long.compare(y, o.y);
	}
}
