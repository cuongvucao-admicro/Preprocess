/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package vn.hus.nlp.sd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.soap.Text;

import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.sentdetect.SentenceDetectorME;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import TextUtils.TextUtils;
import vn.hus.nlp.utils.UTF8FileUtility;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jan 15, 2008, 11:50:28 PM
 *         <p>
 *         This is the general sentence detector for texts. It uses a maximum
 *         entropy model pretrained on an ensemble of texts. All the texts are supposed
 *         to be encoded in UTF-8 encoding.
 */
public class SentenceDetector extends SentenceDetectorME {
	
	private static HashMap<String, Integer>  listAbbreviation;
	
	private static String pathAbbreviation = "file/abbreviation.txt";

	/**
	 * Loads a new sentence detector using the model specified by the model
	 * name.
	 * 
	 * @param modelName
	 *            The name of the maxent model trained for sentence detection.
	 * @throws IOException
	 *             If the model specified can not be read.
	 */
	public SentenceDetector(String modelName) throws IOException {
		super((new SuffixSensitiveGISModelReader(new File(modelName)))
				.getModel());
	}

	
	/**
	 * @param properties
	 * @throws IOException
	 */
	
	public void readAbbreviation() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathAbbreviation), "UTF8"));
		String str;
		listAbbreviation = new HashMap<String, Integer>();
		while ((str = in.readLine()) != null) {
			listAbbreviation.put(str, 1);
		}
		in.close();
	}
	
	public SentenceDetector(Properties properties) throws IOException {
		this(properties.getProperty("sentDetectionModel"));
//		System.out.println("Sentence detection model = " + properties.getProperty("sentDetectionModel"));
		
	}

	/**
	 * Performs sentence detection on a reader, returns an array of detected sentences.
	 * @param reader a reader
	 * @return an array of sentences
	 * @throws IOException 
	 */
	public String[] detectSentences(Reader reader) throws IOException {
		BufferedReader bufReader = new BufferedReader(reader);
		List<String> sentences = new ArrayList<String>();
		for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
			if (line.trim().length() > 0) {
				// detect the sentences composing the line
				String[] sents = sentDetect(line);
				// add them to the list of results
				for (String s : sents) {
					sentences.add(s.trim());
				}
			}
		}
		// close the reader
		if (reader != null)
			reader.close();
		return sentences.toArray(new String[sentences.size()]);
	}
	
	// return true if previous of index in sentence is abbreviation 
	// >> tp.HoChiMinh, TP.HOCHIMINH, Ths.ABC, Ts.ABC, Phd.ABC, ...
	public boolean abbreviation(String sentence, int index){
		if (index > 1){
			String abbreWord = sentence.substring(index - 2, index);
			System.out.println(abbreWord);
			System.out.println(listAbbreviation);
			if (listAbbreviation.containsKey(abbreWord)){
				return true;
			}
		}
		if (index > 2){
			String abbreWord = sentence.substring(index - 3, index);
			System.out.println(abbreWord);
			if (listAbbreviation.containsKey(abbreWord)){
				return true;
			}
		}
		if (index > 3){
			String abbreWord = sentence.substring(index - 4, index);
			System.out.println(abbreWord);
			if (listAbbreviation.containsKey(abbreWord)){
				return true;
			}
		}
		return false;
	}
	
	// return true if in case of isNumber: 14.512354
	public boolean inNumber(String sentence, int index){
		TextUtils textUtils = new TextUtils();
		return textUtils.isNumber(sentence.substring(index - 1, index)) &&
				textUtils.isNumber(sentence.substring(index, index + 1));
	}
	
	public List<String> subSentDetect(String sentence){
		TextUtils textUtils = new TextUtils();
		List<String> sents = new ArrayList<String>();
		int index = 0;
		String sent = "";
		sentence.replaceAll("...", " . ");
		sentence.replaceAll("  ", "");
		for (char ch : sentence.toCharArray()){
			if (index == sentence.length() - 1){
				break;
			}

			if (ch == '.' && index > 0 && 
					textUtils.isUpper(sentence.substring(index+1, index + 2)) && 
					!textUtils.isUpper(sentence.substring(index-1, index)) &&
					!abbreviation(sentence, index) &&
					!inNumber(sentence, index)){
				sents.add(sent);
				sent = "";
			} else {
				sent += ch;
			}
			index += 1;
		}
		sents.add(sent);
		return sents;
	}

	public List<String> detectSentences_extra(String document) throws IOException {
		readAbbreviation();
		List<String> new_sents = new ArrayList<String>();
		String[] sents = sentDetect(document);
		TextUtils textUtils = new TextUtils();
		for (int i = 0 ; i < sents.length - 1; i ++) {
			System.out.println("sents[i] : " + sents[i]);
			List<String> subSents = subSentDetect(sents[i]);
			for (int j = 0 ; j < subSents.size() ; j ++){
				new_sents.add(subSents.get(j));
			}
		}
		return new_sents;
	}
	
	/**
	 * Performs sentence detection a text file, returns an array of detected sentences.
	 * @param inputFile input file name
	 * @return an array of sentences
	 * @throws IOException 
	 */
	public String[] detectSentences(String inputFile) throws IOException {
		return detectSentences(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
	}
	
	/**
	 * Detects sentences of a text file, write results to an output file.
	 * @param inputFile an input file
	 * @param outputFile the result of the detection.
	 */
	public void detectSentences(String inputFile, String outputFile) {
		try {
			UTF8FileUtility.createWriter(outputFile);
			String[] sentences = detectSentences(inputFile);
			for (int i = 0; i < sentences.length; i++) {
				String s = sentences[i];
				UTF8FileUtility.write(s + "\n");
			}
			UTF8FileUtility.closeWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	

	public static void main(String[] args) {
		Options options = new Options();
		
		// create obligatory input/output options
		Option inpOpt = new Option("i", true, "Input filename");
		options.addOption(inpOpt);
		
		Option outOpt = new Option("o", true, "Output filename");
		options.addOption(outOpt);

		
		// a help formatter
		HelpFormatter formatter = new HelpFormatter();;
		
		if (args.length < 2) {
			// automatically generate the help statement
			formatter.printHelp("vnSentDetector", options);
			System.exit(1);
		}
		
		CommandLineParser commandLineParser = new PosixParser();
		try {
			CommandLine commandLine = commandLineParser.parse(options, args);
			
			String inputFile = commandLine.getOptionValue("i");
			if (inputFile == null) {
				System.err.println("Input filename is required.");
				formatter.printHelp( "vnSentDetector:", options );
				System.exit(1);
			}
			
			String outputFile = commandLine.getOptionValue("o");
			if (outputFile == null) {
				System.err.println("Output filename is required.");
				formatter.printHelp( "vnSentDetector:", options );
				System.exit(1);
			}
			
			// create the sent detector
			SentenceDetector sDetector = SentenceDetectorFactory.create("vietnamese");
			// detect sentences
			sDetector.detectSentences(inputFile, outputFile);
			
			System.out.println("Done.");
		} catch (ParseException exp) {
			System.err.println( "Parsing failed.  Reason: " + exp.getMessage());
			formatter.printHelp("vnSentDetector", options);
		}
		
	}
}
