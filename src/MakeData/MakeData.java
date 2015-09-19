package MakeData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import Constants.Constant;
import TextUtils.TextUtils;
import riso.numerical.LBFGS;

public class MakeData {
	private static Constant constant = new Constant();
	private String pathPre = constant.data_set + "/07.postagger/";
	private static String path = constant.data_set + "/08.data/";
	private static final String pathLibrary = path + "WordList.txt";

	private TextUtils textUtils = new TextUtils();
	private BufferedReader in;
	private static HashMap<String, Integer> listPosWord = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listNegWord = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listPosWord_local = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listNegWord_local = new HashMap<String, Integer>();
	private static HashMap<String, Integer> library = new HashMap<String, Integer>();

	private static final String pathPosWord = "./file/NEW_POS.txt";
	private static final String pathNegWord = "./file/NEW_NEG.txt";

	public void readLibrary() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathLibrary), "UTF8"));
		String str;
		int index = 0;
		while ((str = in.readLine()) != null) {
			if (str.split(" ")[0] != null) {
				library.put(str.split(" ")[0], index);
				index += 1;
			}
		}
		in.close();
	}

	public void readPosWord() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathPosWord), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			listPosWord.put(str, 1);
		}
		in.close();
	}

	public void readNegWord() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathNegWord), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			listNegWord.put(str, 1);
		}
		in.close();
	}

	// skip NVA and word has in library
	public String skipNVA(String str) {
		String new_str = "";
		String[] listWord = str.split(" ");
		for (int i = 0; i < listWord.length; i++) {
			String word = listWord[i];
			if (textUtils.isA(word) || textUtils.isN(word) || textUtils.isV(word)) {
				word = textUtils.lower(textUtils.rmTag(word));
				if (library.containsKey(word)) {
					new_str += word + " ";
				}
			}
		}
		return new_str;
	}

	public String rmTagSentence(String sentence) {
		String new_str = "";
		String[] listWord = sentence.split(" ");
		for (int i = 0; i < listWord.length; i++) {
			String word = listWord[i];
			new_str += textUtils.rmTag(word) + " ";
		}
		new_str.replaceAll("\\.", "");
		return new_str;
	}

	public void writeBagOfSentence(BufferedWriter outBagOfSentence, String new_doc_standard) throws IOException {
		String[] listSentence = new_doc_standard.split(" \\. ");
		int numSentence = 0;
		for (int i = 0; i < listSentence.length; i++) {
			if (textUtils.okSentence(listSentence[i])) {
				numSentence += 1;
			}
		}
		if (numSentence == 0) {
			return;
		}
		outBagOfSentence.write(String.valueOf(numSentence) + "\n");
		for (int i = 0; i < listSentence.length; i++) {
			String sentence = listSentence[i];
			String[] listWord = sentence.split(" ");
			boolean ok = false;
			for (int j = 0; j < listWord.length; j++) {
				String word = listWord[j];
				if (library.containsKey(word)) {
					if (listPosWord.containsKey(word) && !listPosWord_local.containsKey(word)) {
						listPosWord_local.put(word, 1);
					}
					if (listNegWord.containsKey(word) && !listNegWord_local.containsKey(word)) {
						listNegWord_local.put(word, 1);
					}
					outBagOfSentence.write(library.get(word) + " ");
					ok = true;
				}
			}
			if (ok) {
				outBagOfSentence.write("\n");
			}
		}
	}

	public void makeDataFile(File fileDir, BufferedWriter out_raw, BufferedWriter out_standard,
			BufferedWriter outBagOfSentence) throws IOException {
		String doc;
		String new_doc_raw = "";
		String new_doc_standard = "";

		in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		while ((doc = in.readLine()) != null) {
			String[] listSentence = doc.split("\\./[\\.Ny]");
			new_doc_raw = "";
			new_doc_standard = "";
			for (int j = 0; j < listSentence.length; j++) {
				String sentence = listSentence[j];
				// System.out.println(sentence);
				// sentence : "Dù/C không/R nắm_giữ/V bất_cứ/R chức_vụ/N"
				String new_sentence_raw = rmTagSentence(sentence);
				String new_sentence_standard = skipNVA(sentence);
				// new_sentence : "nắm_giữ chức_vụ"
				if (textUtils.okSentence(new_sentence_standard)) {
					new_doc_raw += new_sentence_raw + " . ";
					new_doc_standard += new_sentence_standard + " . ";
				}
			}
			if (new_doc_standard != "") {
				out_raw.write(new_doc_raw + "\n");
				out_standard.write(new_doc_standard + "\n");
				writeBagOfSentence(outBagOfSentence, new_doc_standard);
			}

		}
		in.close();

	}

	public void write_list(String file, HashMap<String, Integer> list) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		for (Entry<String, Integer> entry : list.entrySet()) {
			String word = entry.getKey();
			out.write(word + "\n");
		}
		out.close();
	}

	public void makeDataAll() throws IOException {
		readNegWord();
		readPosWord();
		readLibrary();
		TextUtils textUtils = new TextUtils();
		textUtils.makeDir(path);
		File folder = new File(pathPre);
		String fileOutRaw = path + "Document.txt";
		String fileOutStandard = path + "document_standard.txt";
		String fileOutBagOfSentences = path + "BagOfSentences.txt";
		String fileOutSentiWordPos = path + "SentiWords-0.txt";
		String fileOutSentiWordNeg = path + "SentiWords-1.txt";
		String fileOutSentidocTrain = path + "sentidoc_train.txt";
		String fileOutSentidocTest = path + "sentidoc_test.txt";

		BufferedWriter out_raw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutRaw)));
		BufferedWriter out_standard = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutStandard)));
		BufferedWriter outBagOfSentence = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(fileOutBagOfSentences)));
		BufferedWriter outSentidocTest = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(fileOutSentidocTest)));
		BufferedWriter outSentidocTrain = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(fileOutSentidocTrain)));

		Comparator<File> comparator = new Comparator<File>() {
			public int compare(File o1, File o2) {
				return (o1.getName()).compareTo(o2.getName());
			}
		};

		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles, comparator);
		for (File file : listOfFiles) {
			if (file.isFile()) {
				System.out.println(file.getName());
				makeDataFile(file, out_raw, out_standard, outBagOfSentence);
			}
		}
		outSentidocTrain.close();
		outSentidocTest.close();
		outBagOfSentence.close();
		write_list(fileOutSentiWordPos, listPosWord_local);
		write_list(fileOutSentiWordNeg, listNegWord_local);
		out_raw.close();
		out_standard.close();

	}
}
