
package data.param;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlFile {
	private static String typeOfAnalysis;
	private static String BAMFileName;
	private static int windowSize;
	private static boolean randomSampling;
	private static int cpuToUse;
	private static int numberOfSamplings;
	private static int tagExtension;
	private static int resolution;
	private static int strandShift;
	private static String strandToExamine;
	private static String transformation;
	private static int tagCount;
	private static String refFileName;
	private static String parserType;
	private static String outPutType;
	private static String chromosome;
	private static int center;
	private static long chromoSize;
	private static String strandDirection;
	private static int numStdDev;
	private static int stdSize;
	private static int condWindSize;

	// xml file for cross correlation
	public XmlFile(String analysis, String bamFile,int windowS, boolean RandSamp, int numbOfCPUs, int numbOfSamplings){
		typeOfAnalysis = analysis;
		BAMFileName = bamFile;
		windowSize = windowS;
		randomSampling = RandSamp;
		cpuToUse = numbOfCPUs;
		numberOfSamplings = numbOfSamplings;
	}
	
	// xml file for mass data extraction
	public XmlFile(String analysis, String refName, String bamFile, int windowS, int numbOfCPUs, int res, int tagExtens, String strndToExam, int strndShift, String typeTransf, int sizeOfStdDev, int stdDeviation, int condWinSize, int tgCount, String pType, String oType){
		typeOfAnalysis = analysis;
		refFileName= refName;
		BAMFileName = bamFile;
		windowSize = windowS;
		cpuToUse = numbOfCPUs;
		resolution = res;
		tagExtension = tagExtens;
		strandToExamine = strndToExam;
		strandShift = strndShift;
		transformation = typeTransf;
		numStdDev = stdDeviation;
		stdSize = sizeOfStdDev;
		condWindSize = condWinSize;
		tagCount = tgCount;
		parserType = pType;
		outPutType = oType;
	}
	
	// xml file for single site extraction
	public XmlFile(String analysis, String bamFile ,String chr, int cent, long chrSize, String strndDir,int windowS, int numbOfCPUs, int res, int tagExtens, String strndToExam, int strndShift, String typeTransf, int sizeOfStdDev, int stdDeviation, int condWinSize, int tgCount){
		typeOfAnalysis = analysis;
		BAMFileName = bamFile;
		chromosome = chr;
		center = cent;
		chromoSize = chrSize;
		strandDirection = strndDir;
		windowSize = windowS;
		cpuToUse = numbOfCPUs;
		resolution = res;
		tagExtension = tagExtens;
		strandToExamine = strndToExam;
		strandShift = strndShift;
		transformation = typeTransf;
		numStdDev = stdDeviation;
		stdSize = sizeOfStdDev;
		condWindSize = condWinSize;
		tagCount = tgCount;
		
	}
	
	public XmlFile(String analysis, String bamFile, int tagExtens, int resolutn, int strndShift, int numbOfCPUs, String strndToExam, String typeTransf, int sizeOfStdDev, int stdDeviation, int condWinSize, int tgCount){
		typeOfAnalysis = analysis;
		BAMFileName = bamFile;
		tagExtension = tagExtens;
		resolution = resolutn;
		strandShift = strndShift;
		cpuToUse = numbOfCPUs;
		strandToExamine = strndToExam;
		transformation = typeTransf;
		numStdDev = stdDeviation;
		stdSize = sizeOfStdDev;
		condWindSize = condWinSize;
		tagCount = tgCount;

	}

	public void createCorrXML(){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(typeOfAnalysis);
			doc.appendChild(rootElement);

			// parameter elements
			Element param = doc.createElement("parameters");
			rootElement.appendChild(param);

			// set attribute to parameter element
			Attr attr = doc.createAttribute("bamfile");
			attr.setValue(BAMFileName);
			param.setAttributeNode(attr);

			// window size elements
			Element windSize = doc.createElement("windowSize");
			windSize.appendChild(doc.createTextNode(Integer.toString(windowSize)));
			param.appendChild(windSize);

			// correlation type elements
			if(randomSampling){
				Element RandomSample = doc.createElement("correlationType");
				RandomSample.appendChild(doc.createTextNode("Whole Genome"));
				param.appendChild(RandomSample);
			}
			else{
				Element RandomSample = doc.createElement("correclationType");
				RandomSample.appendChild(doc.createTextNode("Random Sampling"));
				param.appendChild(RandomSample);
			}


			// cpus to use elements
			Element cpu = doc.createElement("cpusToUse");
			cpu.appendChild(doc.createTextNode(Integer.toString(cpuToUse)));
			param.appendChild(cpu);

			// number of samplings elements
			Element samplings = doc.createElement("numbOfSamplings");
			samplings.appendChild(doc.createTextNode(Integer.toString(numberOfSamplings)));
			param.appendChild(samplings);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(BAMFileName + typeOfAnalysis + ".xml"));

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public void createMassExtractXML(){
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(typeOfAnalysis);
			doc.appendChild(rootElement);

			// parameter elements
			Element param = doc.createElement("parameters");
			rootElement.appendChild(param);

			// set attribute to parameter element
			Attr attrib = doc.createAttribute("refFile");
			attrib.setValue(refFileName);
			param.setAttributeNode(attrib);
			
			// set attribute to parameter element
			Attr attr = doc.createAttribute("bamfile");
			attr.setValue(BAMFileName);
			param.setAttributeNode(attr);
			
			// window size elements
			Element windSize = doc.createElement("windowSize");
			windSize.appendChild(doc.createTextNode(Integer.toString(windowSize)));
			param.appendChild(windSize);

			// tag extension elements
			Element tagExtens = doc.createElement("tagExtension");
			tagExtens.appendChild(doc.createTextNode(Integer.toString(tagExtension)));
			param.appendChild(tagExtens);

			// resolution elements

			Element reso = doc.createElement("resolution");
			reso.appendChild(doc.createTextNode(Integer.toString(resolution)));
			param.appendChild(reso);

			// strand shift elements
			Element strndShift = doc.createElement("strandShift");
			strndShift.appendChild(doc.createTextNode(Integer.toString(strandShift)));
			param.appendChild(strndShift);

			// cpus to use elements
			Element cpu = doc.createElement("cpusToUse");
			cpu.appendChild(doc.createTextNode(Integer.toString(cpuToUse)));
			param.appendChild(cpu);

			// strand to examine elements
			Element strndToExamine = doc.createElement("strandToExamine");
			strndToExamine.appendChild(doc.createTextNode(strandToExamine));
			param.appendChild(strndToExamine);

			// transformation elements
			Element transform = doc.createElement("transformation");
			transform.appendChild(doc.createTextNode(transformation));
			param.appendChild(transform);
			
			if(transformation.equals("gauss")){
				//standard deviation size element
				Element numOfStdDev = doc.createElement("numberOfStandardDeviations");
				numOfStdDev.appendChild(doc.createTextNode(Integer.toString(numStdDev)));
				param.appendChild(numOfStdDev);
				
				//number of standard deviations element
				Element sizeOfStdDev = doc.createElement("standardDeviationSize");
				sizeOfStdDev.appendChild(doc.createTextNode(Integer.toString(stdSize)));
				param.appendChild(sizeOfStdDev);
				
			}
			if(transformation.equals("standard")){
				// standardize to tag count elements
				Element stdToTagCount = doc.createElement("standardizeToTagCount");
				stdToTagCount.appendChild(doc.createTextNode(Integer.toString(tagCount)));
				param.appendChild(stdToTagCount);
			}
			if(transformation.equals("condpos")){
				//standard deviation size element
				Element numOfStdDev = doc.createElement("numberOfStandardDeviations");
				numOfStdDev.appendChild(doc.createTextNode(Integer.toString(numStdDev)));
				param.appendChild(numOfStdDev);
				
				//number of standard deviations element
				Element sizeOfStdDev = doc.createElement("standardDeviationSize");
				sizeOfStdDev.appendChild(doc.createTextNode(Integer.toString(stdSize)));
				param.appendChild(sizeOfStdDev);
				
				// conditional window size element
				Element condWindowSize = doc.createElement("conditionalWindowSize");
				condWindowSize.appendChild(doc.createTextNode(Integer.toString(condWindSize)));
				param.appendChild(condWindowSize);
			}
			
			
			// correlation type elements
			Element parsType = doc.createElement("parserType");
			parsType.appendChild(doc.createTextNode(parserType));
			param.appendChild(parsType);
			
			// correlation type elements
			Element outType = doc.createElement("outPutType");
			outType.appendChild(doc.createTextNode(outPutType));
			param.appendChild(outType);
		
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(BAMFileName + typeOfAnalysis + ".xml"));

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public void createSingleSiteExtractXML(){
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(typeOfAnalysis);
			doc.appendChild(rootElement);

			// parameter elements
			Element param = doc.createElement("parameters");
			rootElement.appendChild(param);
			
			// set attribute to parameter element
			Attr attr = doc.createAttribute("bamfile");
			attr.setValue(BAMFileName);
			param.setAttributeNode(attr);
	
			// window size elements
			Element chrm = doc.createElement("chromosome");
			chrm.appendChild(doc.createTextNode(chromosome));
			param.appendChild(chrm);
			
			// window size elements
			Element cent = doc.createElement("center");
			cent.appendChild(doc.createTextNode(Integer.toString(center)));
			param.appendChild(cent);
			
			// window size elements
			Element chrmS = doc.createElement("chromosomeSize");
			chrmS.appendChild(doc.createTextNode(Long.toString(chromoSize)));
			param.appendChild(chrmS);
			
			// window size elements
			Element strndDir = doc.createElement("strandDirection");
			strndDir.appendChild(doc.createTextNode(strandDirection));
			param.appendChild(strndDir);
			
			// window size elements
			Element windSize = doc.createElement("windowSize");
			windSize.appendChild(doc.createTextNode(Integer.toString(windowSize)));
			param.appendChild(windSize);

			// tag extension elements
			Element tagExtens = doc.createElement("tagExtension");
			tagExtens.appendChild(doc.createTextNode(Integer.toString(tagExtension)));
			param.appendChild(tagExtens);

			// resolution elements

			Element reso = doc.createElement("resolution");
			reso.appendChild(doc.createTextNode(Integer.toString(resolution)));
			param.appendChild(reso);

			// strand shift elements
			Element strndShift = doc.createElement("strandShift");
			strndShift.appendChild(doc.createTextNode(Integer.toString(strandShift)));
			param.appendChild(strndShift);

			// cpus to use elements
			Element cpu = doc.createElement("cpusToUse");
			cpu.appendChild(doc.createTextNode(Integer.toString(cpuToUse)));
			param.appendChild(cpu);

			// strand to examine elements
			Element strndToExamine = doc.createElement("strandToExamine");
			strndToExamine.appendChild(doc.createTextNode(strandToExamine));
			param.appendChild(strndToExamine);

			// transformation elements
			Element transform = doc.createElement("transformation");
			transform.appendChild(doc.createTextNode(transformation));
			param.appendChild(transform);
			
			if(transformation.equals("gauss")){
				//standard deviation size element
				Element numOfStdDev = doc.createElement("numberOfStandardDeviations");
				numOfStdDev.appendChild(doc.createTextNode(Integer.toString(numStdDev)));
				param.appendChild(numOfStdDev);
				
				//number of standard deviations element
				Element sizeOfStdDev = doc.createElement("standardDeviationSize");
				sizeOfStdDev.appendChild(doc.createTextNode(Integer.toString(stdSize)));
				param.appendChild(sizeOfStdDev);
				
			}
			if(transformation.equals("standard")){
				// standardize to tag count elements
				Element stdToTagCount = doc.createElement("standardizeToTagCount");
				stdToTagCount.appendChild(doc.createTextNode(Integer.toString(tagCount)));
				param.appendChild(stdToTagCount);
			}
			if(transformation.equals("condpos")){
				//standard deviation size element
				Element numOfStdDev = doc.createElement("numberOfStandardDeviations");
				numOfStdDev.appendChild(doc.createTextNode(Integer.toString(numStdDev)));
				param.appendChild(numOfStdDev);
				
				//number of standard deviations element
				Element sizeOfStdDev = doc.createElement("standardDeviationSize");
				sizeOfStdDev.appendChild(doc.createTextNode(Integer.toString(stdSize)));
				param.appendChild(sizeOfStdDev);
				
				// conditional window size element
				Element condWindowSize = doc.createElement("conditionalWindowSize");
				condWindowSize.appendChild(doc.createTextNode(Integer.toString(condWindSize)));
				param.appendChild(condWindowSize);
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(BAMFileName + typeOfAnalysis + ".xml"));

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
	public void createGenomeXML(){
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(typeOfAnalysis);
			doc.appendChild(rootElement);

			// parameter elements
			Element param = doc.createElement("parameters");
			rootElement.appendChild(param);

			// set attribute to parameter element
			Attr attr = doc.createAttribute("bamfile");
			attr.setValue(BAMFileName);
			param.setAttributeNode(attr);

			// tag extension elements
			Element tagExtens = doc.createElement("tagExtension");
			tagExtens.appendChild(doc.createTextNode(Integer.toString(tagExtension)));
			param.appendChild(tagExtens);

			// resolution elements

			Element reso = doc.createElement("resolution");
			reso.appendChild(doc.createTextNode(Integer.toString(resolution)));
			param.appendChild(reso);

			// strand shift elements
			Element strndShift = doc.createElement("strandShift");
			strndShift.appendChild(doc.createTextNode(Integer.toString(strandShift)));
			param.appendChild(strndShift);

			// cpus to use elements
			Element cpu = doc.createElement("cpusToUse");
			cpu.appendChild(doc.createTextNode(Integer.toString(cpuToUse)));
			param.appendChild(cpu);

			// strand to examine elements
			Element strndToExamine = doc.createElement("strandToExamine");
			strndToExamine.appendChild(doc.createTextNode(strandToExamine));
			param.appendChild(strndToExamine);

			// transformation elements
			Element transform = doc.createElement("transformation");
			transform.appendChild(doc.createTextNode(transformation));
			param.appendChild(transform);

			if(transformation.equals("gauss")){
				//standard deviation size element
				Element numOfStdDev = doc.createElement("numberOfStandardDeviations");
				numOfStdDev.appendChild(doc.createTextNode(Integer.toString(numStdDev)));
				param.appendChild(numOfStdDev);
				
				//number of standard deviations element
				Element sizeOfStdDev = doc.createElement("standardDeviationSize");
				sizeOfStdDev.appendChild(doc.createTextNode(Integer.toString(stdSize)));
				param.appendChild(sizeOfStdDev);
				
			}
			if(transformation.equals("standard")){
				// standardize to tag count elements
				Element stdToTagCount = doc.createElement("standardizeToTagCount");
				stdToTagCount.appendChild(doc.createTextNode(Integer.toString(tagCount)));
				param.appendChild(stdToTagCount);
			}
			if(transformation.equals("condpos")){
				//standard deviation size element
				Element numOfStdDev = doc.createElement("numberOfStandardDeviations");
				numOfStdDev.appendChild(doc.createTextNode(Integer.toString(numStdDev)));
				param.appendChild(numOfStdDev);
				
				//number of standard deviations element
				Element sizeOfStdDev = doc.createElement("standardDeviationSize");
				sizeOfStdDev.appendChild(doc.createTextNode(Integer.toString(stdSize)));
				param.appendChild(sizeOfStdDev);
				
				// conditional window size element
				Element condWindowSize = doc.createElement("conditionalWindowSize");
				condWindowSize.appendChild(doc.createTextNode(Integer.toString(condWindSize)));
				param.appendChild(condWindowSize);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(BAMFileName + typeOfAnalysis + ".xml"));

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}



