package DVE.fiacre.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import obp.fiacre.model.Program;
import obp.fiacre.util.FiacrePrinter;

import DVE.compiler.DVECompiler;
import DVE.fiacre.DVE2Fiacre;
import org.junit.Test;

import DVE.model.System;

public class DVE2FiacreTest extends AbstractDVEExplorationTest {
	
	@Test
	public void testBSystem() throws Exception {		
		completeCompile(new File("../DVE/examples/myexpl/bsystem.dve"));
	}
	
	@Test
	public void testExamples() throws Exception {
		File testDir = new File("../DVE/examples/BEEM");
		
		list(testDir);
	}

	public void list(File file) throws Exception {
	    File[] children = file.listFiles();
	    for (File child : children) {
	    	if (child.isDirectory()) {
	    		list(child);
	    	}
	    	else {
	    		if (child.getName().contains(".prop")) return;
	    		
	    		if (	child.getName().endsWith(".dve") 
	    			&& 	!child.getName().startsWith("at.") 				// binary or problem
	    			&& 	!child.getName().startsWith("bridge.") 			// channel with multiple sources problem
	    			&& 	!child.getName().startsWith("brp.") 			// binary and problem
	    			&& 	!child.getName().startsWith("brp2.") 			// binary and problem & state expression problem
	    			&& 	!child.getName().startsWith("collision.") 		// state expression
	    			&& 	!child.getName().startsWith("extinction.1.") 	// channel with multiple sources problem
	    			&& 	!child.getName().startsWith("extinction.2.") 	// channel with multiple sources problem
	    			&& 	!child.getName().startsWith("extinction.3.") 	// channel with multiple sources problem
	    			&& 	!child.getName().startsWith("extinction.4.") 	// channel with multiple sources problem
	    			&& 	!child.getName().startsWith("firewire_link.1.") // Port has no producer.
	    			&& 	!child.getName().startsWith("firewire_link.2.") // channel with multiple sources problem
	    			&& 	!child.getName().startsWith("firewire_link.4.") // Port has no producer.
	    			&& 	!child.getName().startsWith("firewire_link.7.") // Port has no producer.
	    			&& 	!child.getName().startsWith("fischer.") 		// binary or problem
	    			&& 	!child.getName().startsWith("gear.") 			// binary or problem
	    			&& 	!child.getName().startsWith("lup.") 			// state expression problem
	    			&& 	!child.getName().startsWith("needham.") 		// in out channel problem
	    			&& 	!child.getName().startsWith("pgm_protocol.") 	// state expression
	    			&& 	!child.getName().startsWith("pouring.") 		// in out channel problem
	    			&& 	!child.getName().startsWith("production_cell.") // channel with multiple sources problem
	    			&& 	!child.getName().startsWith("protocols.1") 		// Port has no producer.
	    			&& 	!child.getName().startsWith("public_subscribe.")// in out channel problem
	    			&& 	!child.getName().startsWith("rether.") 			// channel with multiple sources problem
	    			&& 	!child.getName().startsWith("synapse.") 		// binary and & << operator problem
	    		) {
	    			completeCompile(child);
	    		}
	    	}
	    }
	}
	
	@Override
	protected void completeCompile(File file) throws Exception {
		java.lang.System.out.println("[Converting " + file + " to fiacre]");
		
		if (!file.exists()) {
			throw new FileNotFoundException(file.toString());
		}
		
		String sys = null;
		try {
			sys = compile2fiacre(file.getAbsolutePath());
		} catch (Exception e) {
			java.lang.System.err.println("error converting "+ file);
			e.printStackTrace();
		}
		if (sys == null) return;
		assert(sys != null);

		File f = new File(ltsDestinationFile + "/" + file.getParentFile().getParentFile().getName());
		if (!f.exists()) f.mkdir();
		
		File fcrFile = new File(ltsDestinationFile+"/"+ file.getParentFile().getParentFile().getName() + "/" + baseName(file).replaceAll("[-.]", "_") +".fcr");
		FileOutputStream stream = new FileOutputStream(fcrFile);
		stream.write(sys.getBytes("UTF-8"));
		stream.close();
		
		
		java.lang.System.out.println("------------------------------------------------------------------");
		java.lang.System.out.println("[Compiling " + fcrFile + "]");
		String rootName = compileFiacreFile(fcrFile);
		compileJavaPackage(packageFile(rootName));
	}
	
	public void assertFile(String filename) throws IOException {
		System sys = null;
		try {
			sys = DVECompiler.compile(new File(filename));
		} catch (Exception e) {
			java.lang.System.err.println("testing "+ filename);
			e.printStackTrace();
		}
		assertTrue("testing "+ filename, sys != null);
		
		Program fcrProgram = new DVE2Fiacre().transform(sys);
		assertTrue(fcrProgram != null);
		FiacrePrinter fcrPrinter = new FiacrePrinter();
		String programString = fcrPrinter.print(fcrProgram);
		java.lang.System.out.println(programString);
		
		BufferedWriter os = new BufferedWriter(new FileWriter("./plc.fcr"));
		os.write(programString);
		os.close();
	}
}
