package DVE.beem;

import java.util.HashMap;
import java.util.Map;

public class BeemInstance {
	String id;
	Map<String, String> parameters = new HashMap<String, String>();
	
	@Override
	public String toString() {
		return id + " -> " + parameters.toString();
	}
}
