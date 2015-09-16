package AddSwapWord;

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

import Constants.Constant;
import TextUtils.TextUtils;
import vn.adtech.nlp.tokenizer.VCTokenizer;

public class AddSwapWord {
	private Constant constant = new Constant();
	private String pathPre = constant.data_set  + "/04.tokenizer/";
	private String path = constant.data_set + "/05.addSwapWord/";
	private static final String pathSwapWord = "file/swapword.txt";
	private static final String pathDownWord = "file/downword.txt";
	private static final String pathPosWord = "file/NEW_POS.txt";
	private static final String pathNegWord = "file/NEW_NEG.txt";
	private BufferedReader in;
	private static HashMap<String, Integer> listSwapWord = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listDownWord = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listPosWord = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listNegWord = new HashMap<String, Integer>();
	
	public AddSwapWord() throws IOException{
		TextUtils textUtils = new TextUtils();
		textUtils.makeDir(path);
		readSwapWord();
		readDownWord();
		readPosWord();
		readNegWord();
		System.out.println(listPosWord.size());
		System.out.println(listNegWord.size());
	}
	
	public void readSwapWord() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathSwapWord), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			listSwapWord.put(str, 1);
		}
		in.close();
	}

	public void readDownWord() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathDownWord), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			listDownWord.put(str, 1);
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
	

	private String addSwapWord(String str){
		String[] listWord = str.split(" ");
		String new_str = listWord[0];
		TextUtils textUtils = new TextUtils();
		for (int i = 1 ; i  < listWord.length; i ++){
			String word = textUtils.lower(listWord[i-1]) + "_" + textUtils.lower(listWord[i]);
			if (listPosWord.containsKey(word) || listNegWord.containsKey(word)){
				new_str += "_" + listWord[i];
			} else {
				new_str += " " + listWord[i];
			}
		}
		return new_str;
	}
	
	private void addSwapWordFile(File fileDir) throws IOException {
		File fileOut = new File(path + fileDir.getName());
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut)));
		String str;
		in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		while ((str = in.readLine()) != null) {
			str = addSwapWord(str);
			out.write(str + "\n");
		}
		out.close();
		in.close();
	}

	public void addSwapWordAll() throws IOException {

		File folder = new File(pathPre);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				addSwapWordFile(file);
			}
		}
	}
}
