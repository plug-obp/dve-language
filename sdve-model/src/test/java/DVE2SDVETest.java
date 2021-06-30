import DVE.compiler.DVECompiler;
import DVE.model.System;
import DVE.transformations.StaticSimplifier;
import org.junit.Test;
import sdve.transformations.*;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class DVE2SDVETest {
    @Test
    public void test1() {
        File testFile = new File("../beem-benchmark/original-benchmark/needham/generated_files/needham.4.dve");
        transform(testFile);
    }
    @Test
    public void testFlattening() {
        File testFile = new File("../beem-benchmark/original-benchmark/needham/generated_files/needham.1.dve");
        transform(testFile);
    }

    @Test
    public void testTypedSynchronousChannel() {
        File testFile = new File("src/test/resources/typed_synchronous_channel.dve");
        transform(testFile);
    }

    @Test
    public void testTypedSynchronousAndBufferedChannel() {
        File testFile = new File("src/test/resources/typed_synchronous_and_buffered_channel.dve");
        transform(testFile);
    }

    @Test
    public void testTypedTupleSync() {
        File testFile = new File("src/test/resources/typed_tuple_sync.dve");
        transform(testFile);
    }

    @Test
    public void testTypedTupleBuffer() {
        File testFile = new File("src/test/resources/typed_tuple_buffer.dve");
        transform(testFile);
    }

    @Test
    public void testProcessReferenceNPE() {
        File testFile = new File("../beem-benchmark/original-benchmark/brp/generated_files/brp.5.prop2.dve");
        transform(testFile);
    }

    @Test
    public void testProcessStateReferenceClassCastE() {
        File testFile = new File("../beem-benchmark/original-benchmark/lup/generated_files/lup.2.prop2.dve");
        transform(testFile);
    }


    @Test
    public void testBig() {
        File testFile = new File("../beem-benchmark/original-benchmark/plc/generated_files/plc.4.dve");
        transform(testFile);
    }

    @Test
    public void testPeterson2() {
        File testFile = new File("../beem-benchmark/original-benchmark/peterson/generated_files/peterson.2.dve");
        transform(testFile);
    }

    @Test
    public void testBridge1() {
        File testFile = new File("../beem-benchmark/original-benchmark/bridge/generated_files/bridge.1.dve");
        transform(testFile);
    }

    @Test
    public void testBenchmark() {
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
                    transform(child);
                }
            }
        }
    }

    public void transform(File testFile) {
        try {
            //compile DVE
            System dveSystem = DVECompiler.compile(testFile);
            assertTrue("compiled  "+ testFile, dveSystem != null);

//            java.lang.System.out.println("\n-------DVE--------->\n");
//            java.lang.System.out.println(PrettyPrinter.toString(dveSystem));

            //Simplify
            dveSystem = StaticSimplifier.simplify(dveSystem);
            assertTrue("simplified  "+ testFile, dveSystem != null);

            //tranform to SDVE
            SDVE.model.System sdveSystem = DVE2SDVE.transform(dveSystem);
            assertTrue("dve2sdve  "+ testFile, sdveSystem != null);


//            java.lang.System.out.println("\n-------SDVE--------->\n");
//            java.lang.System.out.println(PrettyPrinter.toString(sdveSystem));

            //All guards are set, if no guard then suppose true
            Normalizer.normalize(sdveSystem);
            assertTrue("normalizing  "+ testFile, sdveSystem != null);

            //Extract buffered channels
            SDVE.model.System noBufferedChannels = BufferedChannelFlattening.flatten(sdveSystem);
//            java.lang.System.out.println("\n-------NO BUFFERED CHANNEL SDVE--------->\n");
//            java.lang.System.out.println(PrettyPrinter.toString(noBufferedChannels));
            assertTrue("removed buffered channels  "+ testFile, noBufferedChannels != null);

            //Flatten synchronous channels
            SDVE.model.System flatSystem = SynchronizationFlattening.flatten(noBufferedChannels);
            assertTrue("removed synchronous channels  "+ testFile, flatSystem != null);

//            java.lang.System.out.println("\n-------FLAT SDVE--------->\n");
//            java.lang.System.out.println(PrettyPrinter.toString(flatSystem));

            //renormalize after adding new variables
            Normalizer.normalize(flatSystem);
//            java.lang.System.out.println("\n-------Normalized FLAT SDVE--------->\n");
//            java.lang.System.out.println(PrettyPrinter.toString(flatSystem));

            SDVE.model.System flatSystem2 = FlattenTransitions.flatten(flatSystem);
            Normalizer.normalize(flatSystem2);
            assertTrue("flatten expressions  "+ testFile, flatSystem2 != null);
            java.lang.System.out.println("\n-------Normalized FLAT SDVE 2--------->\n");
            java.lang.System.out.println(PrettyPrinter.toString(flatSystem2));
            PrettyPrinter.toString(flatSystem2);
        } catch (Exception e) {
            java.lang.System.err.println("testing "+ testFile);
            e.printStackTrace();
        }
    }
}
