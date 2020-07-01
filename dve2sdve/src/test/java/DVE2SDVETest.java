import DVE.compiler.DVECompiler;
import DVE.model.System;
import DVE.sdve.DVE2SDVE;

import org.junit.Test;
import sdve.transformations.PrettyPrinter;

import java.io.File;

public class DVE2SDVETest {
    @Test
    public void test1() {
        File testFile = new File("../beem-benchmark/original-benchmark/bopdp/generated_files/bopdp.1.prop2.dve");
        try {
            System sys = DVECompiler.compile(testFile);

            SDVE.model.System ssystem = DVE2SDVE.transform(sys);
            java.lang.System.out.println(PrettyPrinter.toString(ssystem));
        } catch (Exception e) {
            java.lang.System.err.println("testing "+ testFile);
            e.printStackTrace();
        }
    }
}
