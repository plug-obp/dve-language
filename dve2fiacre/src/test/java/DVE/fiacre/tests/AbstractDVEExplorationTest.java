package DVE.fiacre.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import junit.framework.TestCase;
import obp.fiacre.model.Program;
import obp.fiacre.util.FiacrePrinter;
import DVE.compiler.DVECompiler;
import DVE.fiacre.DVE2Fiacre;
//import test.AbstractFiacreToExplorerTest;

public class AbstractDVEExplorationTest {//extends AbstractFiacreToExplorerTest {
//	protected void all(String filePath, int expectedConfigurationCount, int expectedActionCount) throws Exception {
//		all(new File(filePath), expectedConfigurationCount, expectedActionCount);
//	}
//
//	@Override
//	protected void all(File file, int expectedConfigurationCount, int expectedActionCount) throws Exception {
//		System.out.println("[Converting " + file + " to fiacre]");
//
//		if (!file.exists()) {
//			throw new FileNotFoundException(file.toString());
//		}
//
//		String sys = null;
//		try {
//			sys = compile2fiacre(file.getAbsolutePath());
//		} catch (Exception e) {
//			System.err.println("error converting "+ file);
//			e.printStackTrace();
//		}
//		assert(sys != null);
//
//		File f = new File(ltsDestinationFile + "/" + file.getParentFile().getParentFile().getName());
//		if (!f.exists()) f.mkdir();
//
//		File fcrFile = new File(ltsDestinationFile+"/"+ file.getParentFile().getParentFile().getName() + "/" + baseName(file).replaceAll("[-.]", "_") +".fcr");
//		FileOutputStream stream = new FileOutputStream(fcrFile);
//		stream.write(sys.getBytes("UTF-8"));
//		stream.close();
//
//		try {
//			super.all(fcrFile, expectedConfigurationCount, expectedActionCount);
//		} catch (Exception e) {
//			TestCase.fail(e.getMessage());
//		}
//	}
//
//	public String compile2fiacre(String filename) throws IOException {
//		DVE.model_view.System sys = null;
//		try {
//			sys = DVECompiler.compile(new File(filename));
//		} catch (Exception e) {
//			java.lang.System.err.println("testing "+ filename);
//			e.printStackTrace();
//		}
//		assertTrue("testing "+ filename, sys != null);
//
//		Program fcrProgram = new DVE2Fiacre().transform(sys);
//		if (fcrProgram == null) return null;
//		assertTrue(fcrProgram != null);
//
//		return FiacrePrinter.toString(fcrProgram);
//	}
//	@Test
//	public void testPassing() {
//		assertTrue(true);
//	}
}
