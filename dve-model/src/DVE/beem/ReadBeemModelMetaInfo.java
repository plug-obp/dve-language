package DVE.beem;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ReadBeemModelMetaInfo {
	SAXParserFactory factory = SAXParserFactory.newInstance();
	Stack<BeemInstance> instances = new Stack<BeemInstance>();
	boolean inInstance = false;
	
	DefaultHandler handler = new DefaultHandler() {
		
		
		
		public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
			//System.err.println(uri + " " + localName + " " + qName + " " + attributes);
			if (localName.equals("instance")) {
				BeemInstance inst = new BeemInstance();
				instances.push(inst);
				inInstance = true;
				
				inst.id = attributes.getValue("id");
			} else if (localName.equals("parameters")){
				inInstance &= true;
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (instances.size() > 0 && inInstance) {
				String data = (new String(Arrays.copyOfRange(ch, start, start+length))).trim();
				if (data.length() > 0) {
					StringTokenizer toknz = new StringTokenizer(data, "=,");
					BeemInstance inst = instances.peek();
					while (toknz.hasMoreTokens()) {
						inst.parameters.put(toknz.nextToken(), toknz.nextToken());
					}
				}
			}
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (localName.equals("instance")) {
				inInstance = false;
			} else if (localName.equals("parameters")) {
				inInstance = false;
			}
		}
	};
	
	public class DummyEntityResolver implements EntityResolver {
	    public InputSource resolveEntity(String publicID, String systemID)
	        throws SAXException {
	        
	        return new InputSource(new StringReader(""));
	    }
	}
	
	List<BeemInstance> readModel(File modelFile) {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setEntityResolver(new DummyEntityResolver());
			reader.setContentHandler(handler);
			InputSource input = new InputSource(modelFile.toURI().toASCIIString());
			reader.parse(input);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return instances;
	}
	
	public static void main(String[] args) {
		ReadBeemModelMetaInfo reader = new ReadBeemModelMetaInfo();
		reader.readModel(new File("../DVECompiler/examples/BEEM/elevator/elevator.xml"));
		System.err.println(reader.instances);
	}
}
