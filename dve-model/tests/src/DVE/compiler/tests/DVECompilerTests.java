package DVE.compiler.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import DVE.compiler.DVECompiler;
import org.junit.Test;
import DVE.model.System;

public class DVECompilerTests {
	
	@Test
	public void testExamples() {
		File testDir = new File("../beem-benchmark/original-benchmark");
		
		list(testDir);
	}
	public void list(File file) {
	    File[] children = file.listFiles();
	    for (File child : children) {
	    	if (child.isDirectory()) {
	    		list(child);
	    	}
	    	else {
	    		if (child.getName().endsWith(".dve")) {
	    			java.lang.System.out.println(child.getName());
	    			assertFile(child.getPath());
	    		}
	    	}
	    }
	}
	
	public void assertFile(String filename) {
		System sys = null;
		try {
			sys = DVECompiler.compile(new File(filename));
		} catch (Exception e) {
			java.lang.System.err.println("testing "+ filename);
			e.printStackTrace();
		}
		assertTrue("testing "+ filename, sys != null);
	}
}

