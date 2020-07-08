import DVE.compiler.DVECompiler;
import DVE.model.System;
import DVE.transformations.StaticSimplifier;
import org.junit.Test;
import sdve.transformations.*;

import java.io.File;

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
        File testFile = new File("/Users/ciprian/Playfield/repositories/dve-language/sdve-model/src/test/resources/typed_synchronous_channel.dve");
        transform(testFile);
    }

    @Test
    public void testTypedSynchronousAndBufferedChannel() {
        File testFile = new File("/Users/ciprian/Playfield/repositories/dve-language/sdve-model/src/test/resources/typed_synchronous_and_buffered_channel.dve");
        transform(testFile);
    }

    @Test
    public void testTypedTupleSync() {
        File testFile = new File("/Users/ciprian/Playfield/repositories/dve-language/sdve-model/src/test/resources/typed_tuple_sync.dve");
        transform(testFile);
    }

    @Test
    public void testTypedTupleBuffer() {
        File testFile = new File("/Users/ciprian/Playfield/repositories/dve-language/sdve-model/src/test/resources/typed_tuple_buffer.dve");
        transform(testFile);
    }

    public void transform(File testFile) {
        try {
            //compile DVE
            System dveSystem = DVECompiler.compile(testFile);

            java.lang.System.out.println("\n-------DVE--------->\n");
            java.lang.System.out.println(PrettyPrinter.toString(dveSystem));

            //Simplify
            dveSystem = StaticSimplifier.simplify(dveSystem);

            //tranform to SDVE
            SDVE.model.System sdveSystem = DVE2SDVE.transform(dveSystem);

//            java.lang.System.out.println("\n-------SDVE--------->\n");
//            java.lang.System.out.println(PrettyPrinter.toString(sdveSystem));

            //All guards are set, if no guard then suppose true
            Normalizer.normalize(sdveSystem);

            //Extract buffered channels
            SDVE.model.System noBufferedChannels = BufferedChannelFlattening.flatten(sdveSystem);
//            java.lang.System.out.println("\n-------NO BUFFERED CHANNEL SDVE--------->\n");
//            java.lang.System.out.println(PrettyPrinter.toString(noBufferedChannels));

            //Flatten synchronous channels
            SDVE.model.System flatSystem = SynchronizationFlattening.flatten(noBufferedChannels);

//            java.lang.System.out.println("\n-------FLAT SDVE--------->\n");
//            java.lang.System.out.println(PrettyPrinter.toString(flatSystem));

            //renormalize after adding new variables
            Normalizer.normalize(flatSystem);
            java.lang.System.out.println("\n-------Normalized FLAT SDVE--------->\n");
            java.lang.System.out.println(PrettyPrinter.toString(flatSystem));

            SDVE.model.System flatSystem2 = FlattenTransitions.flatten(flatSystem);
            java.lang.System.out.println("\n-------Normalized FLAT SDVE 2--------->\n");
            java.lang.System.out.println(PrettyPrinter.toString(flatSystem2));
        } catch (Exception e) {
            java.lang.System.err.println("testing "+ testFile);
            e.printStackTrace();
        }
    }
}
