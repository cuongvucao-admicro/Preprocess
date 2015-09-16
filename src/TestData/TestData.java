package TestData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;

import Constants.Constant;
import TextUtils.TextUtils;

public class TestData {
	private static Constant constant = new Constant();
	private String pathPre = constant.data_set  + "/07.postagger/";
	private static String path = constant.data_set + "/08.data/";

	private TextUtils textUtils = new TextUtils();
	private BufferedReader in;
	private static HashMap<String, Integer> listPosWord = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listNegWord = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listPosWord_local = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listNegWord_local = new HashMap<String, Integer>();
	private static HashMap<Integer, String> library = new HashMap<Integer, String>();
	
	private static final String pathBagOfSentence = path + "BagOfSentences.txt";
	private static final String pathDocument = path + "Document.txt";
	private static final String pathLibrary = path + "WordList.txt";
	private static final String pathTestData = path + "testData.txt";
	public void readLibrary() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathLibrary), "UTF8"));
		String str;
		int index = 0;
		while ((str = in.readLine()) != null) {
			library.put(index, str.split(" ")[0]);
			index += 1;
		}
		in.close();
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public void readAndWriteBagOfSentence() throws NumberFormatException, IOException{
		BufferedReader in_BagOfSentence = new BufferedReader(new InputStreamReader(new FileInputStream(pathBagOfSentence), "UTF8"));
		BufferedReader in_Document = new BufferedReader(new InputStreamReader(new FileInputStream(pathDocument), "UTF8"));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathTestData), "UTF8"));
		String str;
		int index = 0;
		while ((str = in_BagOfSentence.readLine()) != null){
			index += 1;
			int numSentence = Integer.parseInt(str);
			String doc = in_Document.readLine();
			String[] listSentence = doc.split(" \\. ");
			for (int i = 0 ; i < numSentence ; i ++){
				str = in_BagOfSentence.readLine();
				index += 1;
				String[] listWord_standard = str.split(" ");
				for (int j = 0 ; j < listWord_standard.length ; j ++){
					if (isNumeric(listWord_standard[j])){
						out.write(library.get(Integer.parseInt(listWord_standard[j])) + " ");
					}
				}
			
				out.write("\n" + listSentence[i] + "\n\n");
			}

			out.write("\n");
		}
		out.close();
	}


	public void test() throws IOException {
		readLibrary();
		readAndWriteBagOfSentence();
	}
}
