import java.awt.List;
import java.io.IOException;

import vn.adtech.nlp.tokenizer.VCTokenizer;
import AddSwapWord.AddSwapWord;
import List_Adj_V.List_Adj_V;
import MakeData.MakeData;
import MakeLibrary.MakeLibrary;
import ReadData.ReadData;
import RemoveNoisy.RemoveNoisy;
import SentDetect.SentDetect;
import SplitData.SplitData;
import Tagger.Tagger;
import TestData.TestData;
import TextUtils.TextUtils;
import Tokenizer.Tokenizer;

public class Main {
	
	public static void _ReadData() throws IOException{
		System.out.println("ReadData");
		ReadData readData = new ReadData();
		readData.readData();
	}
	
	public static void _RemoveNoisy() throws IOException{
		RemoveNoisy removeNoisy = new RemoveNoisy();
		removeNoisy.rmAll();
	}
	
	public static void _SentDetect() throws IOException{
		SentDetect detect = new SentDetect();
		detect.detectAll();

	}
	
	public static void _Tokenizer() throws IOException{
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.readExtraTokenizer();
		tokenizer.tokenizerAll();

	}
	
	public static void _AddSwapWord() throws IOException{
		AddSwapWord addSwapWord = new AddSwapWord();
		addSwapWord.addSwapWordAll();
	}
	
	public static void _MakeLibrary() throws IOException{
		MakeLibrary makeLibrary = new MakeLibrary();
		makeLibrary.makeLibraryAll();
	}
	
	public static void _Tagger() throws IOException{
		Tagger tagger = new Tagger();
		tagger.taggerAll();
	}
	
	public static void _MakeData() throws IOException{
		MakeData makeData = new MakeData();
		makeData.makeDataAll();
	}
	
	public static void _TestData() throws IOException{
		TestData testData = new TestData();
		testData.test();
	}
	
	public static void _SplitData() throws NumberFormatException, IOException{
		SplitData splitData = new SplitData();
		splitData.split();
	}
	
	public static void _List_Adj_V() throws IOException{
		List_Adj_V list_Adj_V = new List_Adj_V();
		list_Adj_V.listAll();
	}
	
	public static void main(String[] argv) throws IOException{
		long startTime = System.currentTimeMillis();
//		_ReadData();
		System.out.println("Remove Noisy");
		_RemoveNoisy();
		System.out.println("Detect sentence");
		_SentDetect();
		System.out.println("Tokenizer");
		_Tokenizer();
		System.out.println("Add Swap Word");
		_AddSwapWord();
		System.out.println("Make library");
		_MakeLibrary();
		System.out.println("Tagger");
		_Tagger();
		System.out.println("List Adj, V");
//		_List_Adj_V();
		System.out.println("Make Data");
		_MakeData();
		System.out.println("Test Data");
		_TestData();
		System.out.println("Split data");
		_SplitData();
		System.out.println(System.currentTimeMillis() - startTime);
	}
}
