package DVE.vhdl.test;

import DVE.compiler.DVECompiler;
import DVE.extractions.DVEConfigurationSize;
import DVE.model.System;
import DVE.transformations.PrettyPrinter;
import DVE.vhdl.DVE2VHDL;
import DVE.vhdl.DVEFrameBuilder;
import DVE.vhdl.StaticSimplifier;
import org.junit.Test;

import java.io.File;


public class DVE2VHDLTest {
    @Test
    public void test1() {
        File testFile = new File("../beem-benchmark/original-benchmark/anderson/generated_files/anderson.2.dve");
        try {
            System sys = DVECompiler.compile(testFile);
            java.lang.System.out.println(DVE2VHDL.transform(sys));
        } catch (Exception e) {
            java.lang.System.err.println("testing "+ testFile);
            e.printStackTrace();
        }
    }

    @Test
    public void testSimp() {
        File testFile = new File("../beem-benchmark/original-benchmark/peg_solitaire/generated_files/peg_solitaire.5.dve");
        try {
            System sys = DVECompiler.compile(testFile);
            System sys1 = StaticSimplifier.simplify(sys);
            java.lang.System.out.println(PrettyPrinter.toString(sys1));
        } catch (Exception e) {
            java.lang.System.err.println("testing "+ testFile);
            e.printStackTrace();
        }
    }
}
