/**
 * Small helper class used in the class simulator
 * @author Travis
 *
 * @param <T>
 * @param <K>
 */
public class Pair<T,K> {
	public T first;
	public K second;
	
	Pair(T t, K k)
	{
		this.first = t;
		this.second = k;
	}
	Pair()
	{
		this.first = null;
		this.second = null;
	}
}
