package DVE.vhdl;

import DVE.compiler.DVECompiler;
import org.kohsuke.args4j.*;
import org.kohsuke.args4j.spi.Messages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.stream.Collectors;

public class DVE2VHDLMain {

    @Option(name="-o", metaVar="<vhdl-filename>", usage="VHDL output filename")
    private String outVHDL;

    @Argument(metaVar="<dve-filename>", usage="DVE input filename")
    private File inDVE;

    void doMain(String args[]) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);

            if (inDVE == null) {
                throw new CmdLineException(parser, Messages.ILLEGAL_PATH, "Missing DVE input file");
            }

            convertDVE2Fiacre();
        } catch (CmdLineException e) {
            System.err.println();
            System.err.println(e.getMessage());
            System.err.println(this.getClass().getSimpleName() + " [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some times
            System.err.println("Example: \n\tjava <classpath> " + this.getClass().getName() + parser.printExample(OptionHandlerFilter.ALL) + " " + parser.getArguments().stream().map((t)->t.getMetaVariable(null)).collect(Collectors.joining(" ")));
        }
    }

    public static void convert(File inDVE, String outFiacre) {
        DVE2VHDLMain instance = new DVE2VHDLMain();
        instance.inDVE = inDVE;
        instance.outVHDL = outFiacre;
        instance.convertDVE2Fiacre();
    }

    void convertDVE2Fiacre() {
        try {
            DVE.model.System sys = DVECompiler.compile(inDVE);
            String vhdlCode = new DVE2VHDL().transform(sys);

            if (vhdlCode == null) {
                System.err.println("The VHDL program was not generated for: '" + inDVE.getName() + "'");
                return;
            }

            if (outVHDL == null) {
                outVHDL = inDVE.getAbsolutePath() + ".vhd";
            }

            BufferedWriter os = new BufferedWriter(new FileWriter(vhdlCode));
            os.write(vhdlCode);
            os.close();

            System.out.println("The VHDL code was generated in: " + outVHDL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DVE2VHDLMain().doMain(args);
    }
}
