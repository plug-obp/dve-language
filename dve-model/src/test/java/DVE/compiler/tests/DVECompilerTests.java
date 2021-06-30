package DVE.compiler.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import DVE.compiler.DVECompiler;
import DVE.extractions.DVEConfigurationSize;
import org.junit.Test;
import DVE.model.System;

public class DVECompilerTests {

	public DVECompilerTests() throws IOException {
	}

	@Test
	public void testExamples() throws IOException {
		File testDir = new File("../beem-benchmark/original-benchmark");
		
		list(testDir);

//		myWriter.close();
	}
	public void list(File file) {
	    File[] children = file.listFiles();
	    for (File child : children) {
	    	if (child.isDirectory()) {
	    		list(child);
	    	}
	    	else {
	    		if (child.getName().endsWith(".dve")) {
	    			java.lang.System.out.print(child.getName());
	    			assertFile(child.getName(), child.getPath());
	    		}
	    	}
	    }
	}

	@Test
	public void testPeterson2() {
		File testDir = new File("../beem-benchmark/original-benchmark/peterson/generated_files/peterson.2.dve");

		assertFile("peterson.2", testDir.getPath());
	}
//	FileWriter myWriter = new FileWriter("filename.txt");

	public void assertFile(String name, String filename) {
		System sys = null;
		try {
			sys = DVECompiler.compile(new File(filename));
			java.lang.System.out.println("; " + DVEConfigurationSize.get(sys));
//			myWriter.write(name+ "; "+ DVEConfigurationSize.get(sys)+"\n");
		} catch (Exception e) {
			java.lang.System.err.println("testing "+ filename);
			e.printStackTrace();
		}
		assertTrue("testing "+ filename, sys != null);
	}
}

