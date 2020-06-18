package DVE.compiler;

import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import DVE.grammar.DVELexer;
import DVE.grammar.DVEParser;
//import org.eclipse.emf.common.util.URI;
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.resource.Resource;
//import org.eclipse.emf.ecore.resource.ResourceSet;
//import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
//import org.eclipse.emf.ecore.util.EcoreUtil;
//import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import DVE.model.System;

public class DVECompiler {
	private static final DVECompiler instance = new DVECompiler();
	
	public static System compile(File file) throws Exception {
		ANTLRFileStream fs = new ANTLRFileStream(file.getAbsolutePath());

		return instance.compile(fs);
	}
	
	public static System compile(String dveProgram) throws Exception {
		ANTLRInputStream is = new ANTLRInputStream(dveProgram);
		
		return instance.compile(is);
	}
	
	public System compile(ANTLRInputStream is) throws Exception {
		DVELexer 			lexer 	= new DVELexer(is);
		CommonTokenStream 	tokens 	= new CommonTokenStream(lexer);
		DVEParser 			parser 	= new DVEParser(tokens);
		ParseTree			tree 	= parser.system();
		ParseTreeWalker		walker  = new ParseTreeWalker();
		DVEASTBuilder		builder = new DVEASTBuilder();
		System				system	= null;
		
		try {
			walker.walk(builder, tree);
			system = builder.system;
		}
		catch (Error e) {
			java.lang.System.err.println(e.getMessage());
			return null;
		}

		DVENameResolver resolver = new DVENameResolver();
		resolver.apply(system);
		
		//ResourceSet resourceSet = new ResourceSetImpl();
		//resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		//Resource r = resourceSet.createResource(URI.createFileURI("dveModel.xmi"));
		//r.getContents().add(system);
		//r.save(Collections.emptyMap());
		return system;
	}

//	private static void addDanglingEObjects() {
//		
//	}
}



	
