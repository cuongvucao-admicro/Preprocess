package MakeLibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import Constants.Constant;

import java.util.SortedSet;
import java.util.TreeSet;

import TextUtils.TextUtils;
import jvntagger.MaxentTagger;
import jvntagger.POSTagger;

public class MakeLibrary {
	
	private Constant constant = new Constant();
	private String pathPre = constant.data_set  + "/05.addSwapWord/";
	private String path = constant.data_set + "/08.data/";

	
	private String pathDF_Max = "file/DF_MAX.txt";
	private String pathStopWord = "file/STOPWORDLIST.txt";

	private BufferedReader in;
	private TextUtils textUtils = new TextUtils();
	private HashMap<String, Integer> library = new HashMap<String, Integer>();
	private HashMap<String, Integer> dfMax = new HashMap<String, Integer>();
	private HashMap<String, Integer> listStopWord = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listPosWord = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listNegWord = new HashMap<String, Integer>();

	private static final String pathPosWord = "file/NEW_POS.txt";
	private static final String pathNegWord = "file/NEW_NEG.txt";

	private int DF_min = 5;

	public void readDFMax() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathDF_Max), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			dfMax.put(str, 1);
		}
		in.close();
	}

	public void makeLibraryFile(File fileDir) throws IOException {
		String str;
		in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		while ((str = in.readLine()) != null) {
			String[] listWord = str.split(" ");
			for (int i = 0; i < listWord.length; i++) {
				String word = textUtils.lower(listWord[i]);
				if (textUtils.isWord(word)) {
					if (library.containsKey(word)) {
						int oldValue = library.get(word);
						library.put(word, oldValue + 1);
					} else {
						library.put(word, 1);
					}
				}
			}
		}
		in.close();
	}

	public LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
		List mapKeys = new ArrayList(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap sortedMap = new LinkedHashMap();

		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)) {
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((String) key, (Integer) val);
					break;
				}

			}

		}
		return sortedMap;
	}

	public void writeLibrary() throws IOException {
		File fileOut = new File(path + "WordList.txt");
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut)));
		library = sortHashMapByValuesD(library);

		for (Entry<String, Integer> entry : library.entrySet()) {
			String word = entry.getKey();
			int number = entry.getValue();
			System.out.println(word + " " + String.valueOf(number));
			if (listNegWord.containsKey(word) || listPosWord.containsKey(word)
					|| (number > DF_min && !dfMax.containsKey(word) && !listStopWord.containsKey(word))) {
				out.write(word + "\n");
			}
		}
		out.close();
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

	public void readStopWord() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathStopWord), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			listStopWord.put(str, 1);
		}
		in.close();
	}

	public void makeLibraryAll() throws IOException {
		readNegWord();
		readPosWord();

		readDFMax();
		readStopWord();
		TextUtils textUtils = new TextUtils();
		textUtils.makeDir(path);
		File folder = new File(pathPre);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				makeLibraryFile(file);
			}
		}
		writeLibrary();

	}
}
