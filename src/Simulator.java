/**
 * an abstract class for running some simulation
 * T is the output type, K is the input type
 * @author Travis
 */

import java.util.HashMap;
import java.util.Vector;

public abstract class Simulator<T,K>
{
	private HashMap<K,T> record;
	private Vector<T> states;
	Pair<K,K> bounds;
	
	private T initialState;
	Simulator()
	{
		states = new Vector<T>();
		record = new HashMap<K,T>();
		bounds = new Pair<K,K>();
		initialState = null;
	}
	Simulator(K lowBound,K upBound)
	{
		states = new Vector<T>();
		record = new HashMap<K,T>();
		bounds = new Pair<K,K>(lowBound,upBound);
		initialState = null;
	}
	

	abstract T simulate(K k);
	T lastState()
	{
		if(states.isEmpty()) return null;
		return states.get(states.size() - 1);
	}
}
