/*
 * 	Detected sentence
 *  Removed question
 *  Lower Seq of words
 * 
 */
package SentDetect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mysql.fabric.xmlrpc.base.Array;

import Constants.Constant;
import TextUtils.TextUtils;

public class SentDetect {

	private Constant constant = new Constant();
	private String pathPre = constant.data_set + "/02.removeNoisy/";
	private String path = constant.data_set + "/03.sentDetect/";
	private static HashMap<String, Integer> listAbbreviation;

	private static String pathAbbreviation = "file/abbreviation.txt";

	private static TextUtils textUtils;

	private BufferedReader in;

	public void readAbbreviation() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathAbbreviation), "UTF8"));
		String str;
		listAbbreviation = new HashMap<String, Integer>();
		while ((str = in.readLine()) != null) {
			listAbbreviation.put(str, 1);
		}
		in.close();
	}

	public String processSentence(String str) throws IOException {
		int numWord = 0;
		str = str.replace("  ", "");
		str = str.replace("..", ".");
		while (str.length() > 0 && str.charAt(str.length() - 1) == ' ') {
			str = str.substring(0, str.length() - 1);
		}
		while (str.length() > 0 && str.charAt(str.length() - 1) == '.') {
			str = str.substring(0, str.length() - 1);
		}
		while (str.length() > 0 && str.charAt(str.length() - 1) == '!') {
			str = str.substring(0, str.length() - 1);
		}
		if (str.length() == 0) {
			return str;
		}

		// Remove sentence is question
		if (str.contains("?")) {
			return "";
		}
		// Remove very short sentence ( < 2 words)
		String[] listWord = str.split(" ");
		
		for (int i = 0 ;  i < listWord.length ; i ++){
			if (textUtils.isWord(listWord[i])){
				numWord += 1;
			}
		}
		if (numWord < 2) {
			return "";
		}
		// Lower seq Upper words
		textUtils = new TextUtils();
		String[] newListword = new String[listWord.length];
		for (int i = 0; i < listWord.length; i++) {
			newListword[i] = listWord[i];
		}
		for (int i = 0; i < listWord.length - 2; i++) {
			String word1 = listWord[i];
			String word2 = listWord[i + 1];
			String word3 = listWord[i + 2];

			if (textUtils.isUpper(word1) && textUtils.isUpper(word2) && textUtils.isUpper(word3)) {
				newListword[i] = textUtils.lower(word1);
				newListword[i + 1] = textUtils.lower(word2);
				newListword[i + 2] = textUtils.lower(word3);
			}
			
		}

		// link words to sentence
		String sentence = "";
		for (int i = 0; i < listWord.length; i++) {
			sentence += newListword[i] + " ";
		}
		sentence = sentence.replaceAll("\\.", "");
		sentence += " . ";
		return sentence;

	}

	// ===== DETECT WITH TOOL SentDetect ========

//	public String detectDoc(String document, BufferedWriter writer) throws IOException {
//		String new_document = "";
//		SentenceDetector sDetector = SentenceDetectorFactory.create("vietnamese");
//		List<String> listSent = sDetector.detectSentences_extra(document);
//		for (String sentence : listSent) {
//			sentence = add_end_digit(sentence);
//			new_document += sentence;
//			writer.write(sentence + "\n");
//		}
//		return new_document;
//	}

	// ===== DETECT WITH MY CODE ========

	
	// return true if previous of index in sentence is abbreviation
	// >> tp.HoChiMinh, TP.HOCHIMINH, Ths.ABC, Ts.ABC, Phd.ABC, ...
	public boolean abbreviation(String sentence, int index) {
		if (index > 1) {
			String abbreWord = sentence.substring(index - 2, index);
			if (listAbbreviation.containsKey(abbreWord)) {
				return true;
			}
		}
		if (index > 2) {
			String abbreWord = sentence.substring(index - 3, index);
			if (listAbbreviation.containsKey(abbreWord)) {
				return true;
			}
		}
		if (index > 3) {
			String abbreWord = sentence.substring(index - 4, index);
			if (listAbbreviation.containsKey(abbreWord)) {
				return true;
			}
		}
		return false;
	}

	// return true if in case of isNumber: 14.512354
	public boolean inNumber(String sentence, int index) {
		textUtils = new TextUtils();
		return textUtils.isNumber(sentence.substring(index - 1, index))
				&& textUtils.isNumber(sentence.substring(index + 1, index + 2));
	}

	public List<String> detectSentences(String document) {
		textUtils = new TextUtils();
		List<String> sents = new ArrayList<String>();
		int index = 0;
		String sent = "";
		document.replaceAll("...", " . ");
		document.replaceAll("  ", "");
		for (char ch : document.toCharArray()) {
			if (index == document.length() - 1) {
				break;
			}

			if ((ch == '.' || ch == '!' || ch == '?') && index > 0 &&
					// next word is UpperWord
					// (textUtils.isUpper(document.substring(index+1, index +
					// 2))
					// || document.substring(index+1, index + 2).equals(' ')) &&
					// previous word is not UpperWord (L.anh is a name)
					!(textUtils.isUpper(document.substring(index - 1, index)) &&
							textUtils.isUpper(document.substring(index+1, index +2))) &&
					// not abbreviation (Tp.HoChiMinh)
					!abbreviation(document, index) &&
					// not in a number (15.000)
					!inNumber(document, index)) {
				// remove question
				if (ch != '?'){
					sents.add(sent);
				}
				sent = "";
			} else {
				sent += ch;
			}
			index += 1;
		}
		sents.add(sent);
		return sents;
	}

	public String detectDoc(String document, BufferedWriter writer) throws IOException {
		String new_document = "";
		// detect sentences
		List<String> listSent = detectSentences(document);
		for (String sentence : listSent) {
			// process a single sentence
			sentence = processSentence(sentence);
			new_document += sentence;
			writer.write(sentence + "\n");
		}
		return new_document;
	}

	public void detectFile(File fileDir, BufferedWriter writer) throws IOException {
		textUtils = new TextUtils();
		in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		File fileOut = new File(path + fileDir.getName());
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut)));
		String str;
		while ((str = in.readLine()) != null) {
			str = detectDoc(str, writer);
			if (str.length() != 0) {
				out.write(str + "\n");
			}
		}
		out.close();
		in.close();
	}

	public void detectAll() throws IOException {
		readAbbreviation();
		textUtils = new TextUtils();
		textUtils.makeDir(path);
		File folder = new File(pathPre);
		File[] listOfFiles = folder.listFiles();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("sentence.txt")));
		for (File file : listOfFiles) {
			if (file.isFile()) {
				detectFile(file, writer);
			}
		}
		writer.close();
	}
}
