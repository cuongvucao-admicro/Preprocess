package SplitData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import Constants.Constant;
import TextUtils.TextUtils;

public class SplitData {
	private static Constant constant = new Constant();
	private static String path = constant.data_set + "/08.data/";

	public static String fileBagOfSentences = path + "BagOfSentences.txt";
	public static String fileDocument = path + "Document.txt";
	public static String fileBagOfSentencesTest = path + "BagOfSentences_test.txt";
	public static String fileDocumentTest = path + "Document_test.txt";
	public static String fileBagOfSentencesTrain = path + "BagOfSentences_train.txt";
	public static String fileDocumentTrain = path + "Document_train.txt";

	
	
	public void split() throws NumberFormatException, IOException{
		TextUtils textUtils = new TextUtils();
		textUtils.makeDir(path);
		BufferedReader inBagOfSentence = new BufferedReader(new InputStreamReader(new FileInputStream(fileBagOfSentences), "UTF8"));
		BufferedReader inDocument = new BufferedReader(new InputStreamReader(new FileInputStream(fileDocument), "UTF8"));
		
		BufferedWriter outBagOfSentenceTest = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileBagOfSentencesTest)));
		BufferedWriter outDocumentTest = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDocumentTest)));
		BufferedWriter outBagOfSentenceTrain = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileBagOfSentencesTrain)));
		BufferedWriter outDocumentTrain = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDocumentTrain)));
		
		String str;
		int index = 0;
		int numTest = 0;
		while ((str = inBagOfSentence.readLine()) != null){
			index += 1;
			
			int numSentence = Integer.parseInt(str);
			String doc = inDocument.readLine();
		    Random random = new Random();
			int numRandom = random.nextInt(10);
			//int numRandom = 0;
			//if (numRandom == 0 && numTest < 700 && index > 40000){
			if (numRandom == 0){
				numTest += 1;
				outBagOfSentenceTest.write(numSentence + "\n");
				outDocumentTest.write(doc + "\n");
				for (int i = 0 ; i < numSentence ; i ++){
					str = inBagOfSentence.readLine();
					outBagOfSentenceTest.write(str + "\n");
				}
			} else {
				outBagOfSentenceTrain.write(numSentence + "\n");
				outDocumentTrain.write(doc + "\n");
				for (int i = 0 ; i < numSentence ; i ++){
					str = inBagOfSentence.readLine();
					outBagOfSentenceTrain.write(str + "\n");
				}
			}
			
		}
		outBagOfSentenceTest.close();
		outBagOfSentenceTrain.close();
		outDocumentTest.close();
		outDocumentTrain.close();
		inBagOfSentence.close();
		inDocument.close();
	
	}
}
