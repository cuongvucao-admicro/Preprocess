package Tagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import Constants.Constant;
import TextUtils.TextUtils;
import jvntagger.MaxentTagger;
import jvntagger.POSTagger;

public class Tagger {
	private Constant constant = new Constant();
	private String pathPre = constant.data_set  + "/05.addSwapWord/";
	private String path = constant.data_set + "/07.postagger/";

	private BufferedReader in;
	public static String modelDir = "model/maxent";
	public static POSTagger tagger = null;
	
	public void taggerFile(File fileDir, POSTagger tagger) throws IOException {
		File fileOut = new File(path + fileDir.getName());
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut)));
		String str;
		in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		while ((str = in.readLine()) != null) {
			str = tagger.tagging(str);
			str = str.replace("\n", " ");
			out.write(str + "\n");
		}
		out.close();
		in.close();
	}

	public void taggerAll() throws IOException {
		TextUtils textUtils = new TextUtils();
		textUtils.makeDir(path);
		tagger = new MaxentTagger(modelDir);
		
		File folder = new File(pathPre);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				taggerFile(file, tagger);
			}
		}
	}
}
