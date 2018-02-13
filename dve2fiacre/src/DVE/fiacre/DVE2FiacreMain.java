package DVE.fiacre;

import DVE.compiler.DVECompiler;
import obp.fiacre.model.Program;
import obp.fiacre.util.FiacrePrinter;
import org.kohsuke.args4j.*;
import org.kohsuke.args4j.spi.Messages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.stream.Collectors;


public class DVE2FiacreMain {
	
	@Option(name="-o", metaVar="<fiacre-filename>", usage="FIACRE output filename")
	private String outFiacre;
	
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
		DVE2FiacreMain instance = new DVE2FiacreMain();
		instance.inDVE = inDVE;
		instance.outFiacre = outFiacre;
		instance.convertDVE2Fiacre();
	}

	void convertDVE2Fiacre() {
		try {
			DVE.model.System sys = DVECompiler.compile(inDVE);
			Program fcrProgram = new DVE2Fiacre().transform(sys);
			FiacrePrinter fcrPrinter = new FiacrePrinter();
			String programString = fcrPrinter.print(fcrProgram);

			if (outFiacre == null) {
				outFiacre = inDVE.getAbsolutePath() + ".fcr";
			}

			BufferedWriter os = new BufferedWriter(new FileWriter(outFiacre));
			os.write(programString);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new DVE2FiacreMain().doMain(args);
	}
}
