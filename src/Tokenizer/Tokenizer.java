package Tokenizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import Constants.Constant;
import TextUtils.TextUtils;
import vn.adtech.nlp.tokenizer.VCTokenizer;

public class Tokenizer {
	private Constant constant = new Constant();
	private String pathPre = constant.data_set  + "/03.sentDetect/";
	private String path = constant.data_set + "/04.tokenizer/";

	private static final String pathExtraTokenizer = "file/new_token.txt";
	private BufferedReader in;
	private static List<String> listExtraTokenizer;
	
	public void readExtraTokenizer() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathExtraTokenizer), "UTF8"));
		String str;
		listExtraTokenizer = new ArrayList<String>();
		while ((str = in.readLine()) != null) {
			listExtraTokenizer.add(str);
		}
		in.close();
	}

	public String extraTokenizer(String str) {
		for (String word : listExtraTokenizer) {
			str = str.replaceAll(word, word.replace(" ", "_"));
		}
		return str;
	}

	public void tokenizerFile(File fileDir, VCTokenizer tokenizer) throws IOException {
		File fileOut = new File(path + fileDir.getName());
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut)));
		String str;
		in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		while ((str = in.readLine()) != null) {
			str = tokenizer.tokenize(str);
			str = str.replace("\n", " ");
			str = str.replaceAll("_\\._", " \\. ");
			str = str.replaceAll("_\\.", " \\.");
			str = str.replaceAll("\\._", "\\. ");
			str = extraTokenizer(str);
			out.append(str).append("\n");
			out.write(str + "\n");
		}
		out.close();
		in.close();
	}

	public void tokenizerAll() throws IOException {
		TextUtils textUtils = new TextUtils();
		textUtils.makeDir(path);
		VCTokenizer tokenizer = new VCTokenizer();
		File folder = new File(pathPre);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				tokenizerFile(file, tokenizer);
			}
		}
	}
}
