package DVE.grammar.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import DVE.grammar.DVELexer;
import DVE.grammar.DVEParser;
import org.junit.Test;

public class DVEGrammarTest {
	private String currentRule;
	
	@Test	
	public void testVariableDecl() {
		setCurrentRule("type");
		assertParse("int");
		assertParse("byte");
	}
	
	@Test
	public void testExamples() {
		File testDir = new File("../beem-benchmark/original-benchmark");
		System.out.println(new File(".").getAbsolutePath());
		
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
	    			System.out.println(child.getName());
	    			assertFile(child.getPath());
	    		}
	    	}
	    }
	}
	
	public ParseTree assertFile(String filename) {
		ParseTree result = parseFile(filename);
		assertTrue("testing "+ filename, result != null);
		return result;
	}
	
	public ParseTree parseFile(String filename) {
		ANTLRFileStream fs;
		try {
			fs = new ANTLRFileStream(filename);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return parse(fs, "system");
	}
	
	public ParseTree parse(CharStream cs, String rule) {
		DVELexer 			lexer 	= new DVELexer(cs);
		CommonTokenStream 	tokens 	= new CommonTokenStream(lexer);
		Parser 				parser 	= new DVEParser(tokens);
		NoErrorsForTest  	errorL  = new NoErrorsForTest();

		parser.removeErrorListeners();
		parser.addErrorListener(errorL);
		try {
			Method mtd = parser.getClass().getMethod(rule);
			ParseTree pt = (ParseTree)mtd.invoke(parser);
			if (errorL.hasErrors()) return null;
			return pt; 
		} catch (Exception e) {
			System.err.println("no matching method for rule: " + rule);
			return null;
		}
	}
	public static class NoErrorsForTest extends BaseErrorListener {
		private Boolean hasErrors = false;
		@Override
		public void syntaxError(Recognizer<?, ?> rec, Object offendingSymbol, int line, int column, String msg, RecognitionException e) {
			hasErrors = true;
		}
		public Boolean hasErrors() {
			return hasErrors;
		}
	}
	class DVEFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith(".dve");
		}
	}
	public void setCurrentRule(String currentRule) {
		this.currentRule = currentRule;
	}
	public ParseTree parse(String input, String rule) {
		ANTLRInputStream 	is		= new ANTLRInputStream(input);
		return parse(is, rule);
	}
	public ParseTree assertParse(String input) {
		ParseTree result = parse(input, currentRule);
		assertTrue(result != null);
		return result;
	}
}
