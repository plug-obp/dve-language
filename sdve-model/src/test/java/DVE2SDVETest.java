import DVE.compiler.DVECompiler;
import DVE.model.System;
import sdve.transformations.DVE2SDVE;

import org.junit.Test;
import sdve.transformations.PrettyPrinter;

import java.io.File;

public class DVE2SDVETest {
    @Test
    public void test1() {
        File testFile = new File("../beem-benchmark/original-benchmark/needham/generated_files/needham.4.dve");
        try {
            System sys = DVECompiler.compile(testFile);
            java.lang.System.out.println(PrettyPrinter.toString(sys));

            java.lang.System.out.println("\n---------------->\n");

            SDVE.model.System ssystem = DVE2SDVE.transform(sys);
            java.lang.System.out.println(PrettyPrinter.toString(ssystem));
        } catch (Exception e) {
            java.lang.System.err.println("testing "+ testFile);
            e.printStackTrace();
        }
    }
}
