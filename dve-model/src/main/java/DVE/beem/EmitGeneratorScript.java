package DVE.beem;

import java.io.File;
import java.util.List;
import java.util.Map;

public class EmitGeneratorScript {
	String divine(String mdveFile, Map<String, String> parameters) {
		StringBuilder strB = new StringBuilder();
		strB.append("divine combine -o ");
		strB.append(mdveFile);
		strB.append(" ");
		for (Map.Entry<String, String> me : parameters.entrySet()) {
			strB.append(me.getKey());
			strB.append("=");
			strB.append(me.getValue());
			strB.append(" ");
		}
		return strB.toString();
	}
	
	public void list(File file, StringBuilder builder) throws Exception {
	    File[] children = file.listFiles();
	    for (File child : children) {
	    	if (child.isDirectory()) {
	    		list(child, builder);
	    	}
	    	else {
	    		if (child.getName().endsWith(".dve")) return;
	    		if (child.getName().endsWith(".mdve")) {
	    			String basename = child.getName().substring(0, child.getName().length()-5);
	    			File xmlFile = new File(child.getParent(), basename + ".xml");
	    			
	    			//System.out.println("\t-" + child.getName());
	    			
	    			if (xmlFile.exists()) {
	    				ReadBeemModelMetaInfo reader = new ReadBeemModelMetaInfo();
	    				List<BeemInstance> instances = reader.readModel(xmlFile);
	    				File genPath = new File(child.getParentFile().getParentFile(), "generated/");
	    				for (BeemInstance bI : instances) {
	    					File generatedFile = new File(genPath,  basename +"." + bI.id + ".dve");
	    					builder.append(divine(child.getPath(), bI.parameters) + " > " + generatedFile.getPath() + "\n");
	    				}
	    			}

	    		}
	    	}
	    }
	}
	
	public String emitScriptFor(File dir) {
		StringBuilder sB = new StringBuilder();
		try {
			list(dir, sB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sB.toString();
	}
	public static void main(String[] args) {
		EmitGeneratorScript emitter = new EmitGeneratorScript();
		String script = emitter.emitScriptFor(new File("../DVECompiler/examples/BEEM1"));
		System.err.println(script);
	}
}
