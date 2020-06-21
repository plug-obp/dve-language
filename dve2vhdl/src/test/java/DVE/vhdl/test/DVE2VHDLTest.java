package DVE.vhdl.test;

import DVE.compiler.DVECompiler;
import DVE.extractions.DVEConfigurationSize;
import DVE.model.System;
import DVE.vhdl.DVE2VHDL;
import DVE.vhdl.DVEFrameBuilder;
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
}
