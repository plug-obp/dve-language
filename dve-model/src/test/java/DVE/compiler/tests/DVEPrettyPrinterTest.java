package DVE.compiler.tests;

import DVE.compiler.DVECompiler;
import DVE.model.System;
import DVE.transformations.PrettyPrinter;
import org.junit.Test;

import java.io.File;

public class DVEPrettyPrinterTest {
    @Test
    public void test1() {
        File testFile = new File("../beem-benchmark/original-benchmark/bopdp/generated_files/bopdp.1.prop2.dve");
        try {
            System sys = DVECompiler.compile(testFile);

            java.lang.System.out.println(PrettyPrinter.toString(sys));
        } catch (Exception e) {
            java.lang.System.err.println("testing "+ testFile);
            e.printStackTrace();
        }
    }
}
