package DVE.fiacre.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import obp.explorer.Explorer;
import obp.result.ExplorationResultInfo;

import org.junit.Test;



public class DVEExplorationTest extends AbstractDVEExplorationTest {
	Map<String, Integer[]> result = new HashMap<String, Integer[]>(){
		private static final long serialVersionUID = 1L;
	{
        put("adding.1.dve", new Integer[] { 7372, 		11144 } );
        put("adding.2.dve", new Integer[] { 836838, 	-1 } );
        put("adding.3.dve", new Integer[] { 1894376, 	-1 } );
        put("adding.4.dve", new Integer[] { 3370680, 	-1 } );
        put("adding.5.dve", new Integer[] { 5271456, 	-1 } );
        put("adding.6.dve", new Integer[] { 7609684, 	-1 } );
    }};

    @Override
	protected void exploreModel(String[] command, int expectedConfigurationCount, int expectedActionCount) {
		ExplorationResultInfo result = Explorer.explore(command); 
		assertNotNull("While exploring", result);
		assertEquals("Wrong numbers of configurations", expectedConfigurationCount, result.getConfigurationCount().intValue());
		//assertEquals("Wrong numbers of actions", expectedActionCount, result.getActionCount().intValue());
	}
    
	@Test
	public void testExamples() throws Exception {
		File testDir = new File("../DVE/examples/BEEM");
		
		list(testDir);
	}
	int count = 10000;
	public void list(File file) throws Exception {
	    File[] children = file.listFiles();
	    for (File child : children) {
	    	if (child.isDirectory()) {
	    		list(child);
	    	}
	    	else {
	    		if (count == 0) return;
	    		if (child.getName().contains(".prop")) return;
	    		
	    		if ( child.getName().endsWith(".dve") ) {
	    			count--;
	    			java.lang.System.out.println(child.getName());
	    			int states = -1;
	    			int transitions = -1;
	    			if (result.get(child.getName()) != null) {
	    				states = result.get(child.getName())[0];
	    				transitions = result.get(child.getName())[1];
	    			}
	    			all(child, states, transitions);
	    		}
	    	}
	    }
	}
}
