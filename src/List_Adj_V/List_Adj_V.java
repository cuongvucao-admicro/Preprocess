package List_Adj_V;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import Constants.Constant;
import TextUtils.TextUtils;
import riso.numerical.LBFGS;

public class List_Adj_V {
	private static Constant constant = new Constant();
	private String pathPre = constant.data_set  + "/07.postagger/";
	private static String path = constant.data_set + "/09.Adj_V/";

	private TextUtils textUtils = new TextUtils();
	private BufferedReader in;
	private static HashMap<String, Integer> libraryAdj = new HashMap<String, Integer>();
	private static HashMap<String, Integer> libraryV = new HashMap<String, Integer>();	
	private static HashMap<String, Integer> libraryN = new HashMap<String, Integer>();

	public String rmTagSentence(String sentence){
		String new_str = "";
		String[] listWord = sentence.split(" ");
		for (int i = 0 ; i < listWord.length ; i ++){
			String word = listWord[i];
			new_str += textUtils.rmTag(word) + " ";
		}
		new_str.replaceAll("\\.", "");
		return new_str;
	}
	
	public void listFile(File fileDir) throws IOException {
		String doc;
		in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		while ((doc = in.readLine()) != null) {
			String[] listWord = doc.split(" ");
			for (int i = 0 ; i < listWord.length ; i ++){
				String word = textUtils.lower(textUtils.rmTag(listWord[i]));
				if (textUtils.isA(listWord[i])){
					if (!libraryAdj.containsKey(word)){
						libraryAdj.put(word, 1);
					} else {
						libraryAdj.replace(word, libraryAdj.get(word) + 1);
					}
				}
				if (textUtils.isV(listWord[i])){
					if (!libraryV.containsKey(word)){
						libraryV.put(word, 1);
					} else {
						libraryV.replace(word, libraryV.get(word) + 1);
					}
				}
				if (textUtils.isN(listWord[i])){
					if (!libraryN.containsKey(word)){
						libraryN.put(word, 1);
					} else {
						libraryN.replace(word, libraryN.get(word) + 1);
					}
				}
			}
		}
	}
	
	  private static HashMap sortByValues(HashMap map) { 
	       List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return -((Comparable) ((HashMap.Entry) (o1)).getValue())
	                  .compareTo(((HashMap.Entry) (o2)).getValue());
	            }
	       });

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (HashMap.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	 }
	
	public void write_list(BufferedWriter out, HashMap<String, Integer> list) throws IOException {
		list = sortByValues(list);
		for (Entry<String, Integer> entry : list.entrySet()) {
			String word = entry.getKey();
			if (list.get(word) >= 10){
				out.write(word + " " + list.get(word) + "\n");
			}
		}
		out.close();
	}
	
	public void listAll() throws IOException {
		TextUtils textUtils = new TextUtils();
		textUtils.makeDir(path);
		File folder = new File(pathPre);
		String fileOutAjd = path + "Adj.txt";
		String fileOutV = path + "V.txt";
		String fileOutN = path + "N.txt";

		BufferedWriter outAdj = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutAjd)));
		BufferedWriter outV = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutV)));
		BufferedWriter outN = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutN)));
		
        Comparator<File> comparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return (o1.getName()).compareTo(o2.getName());
            }
        };
		
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles, comparator);
		for (File file : listOfFiles) {
			if (file.isFile()) {
				listFile(file);
			}
		}
		write_list(outN, libraryN);
		write_list(outAdj, libraryAdj);
		write_list(outV, libraryV);
		outAdj.close();
		outV.close();
	}
}
