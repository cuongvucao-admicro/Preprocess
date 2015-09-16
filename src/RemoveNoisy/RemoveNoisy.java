package RemoveNoisy;

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

public class RemoveNoisy {
	private BufferedReader in;
	private Constant constant = new Constant();
	private String pathPre = constant.data_set  + "/01.raw/";
	private String path = constant.data_set + "/02.removeNoisy/";
	private TextUtils textUtils;

	public void rmFile(File fileDir) throws IOException{
		textUtils.readUnexpectedWords();
		textUtils.readSpecialDigit();
		in = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileDir), "UTF8"));
		File fileOut = new File(path + fileDir.getName());
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileOut)));
		String str;
		while ((str = in.readLine()) != null){
			// remove char " " >< space
			str = textUtils.removeUnicode(str);
			str = textUtils.removeSpecialChar(str);
			str = textUtils.removeUrls(str);
			str = textUtils.removeInvalidCharacter(str);
			str = textUtils.removeQuoteString(str);
			str = textUtils.removeUnexpectedWord(str);
			str = textUtils.removeDomains(str);
			str = textUtils.removeSquareBracket(str);
			str = textUtils.removeRoundBracket(str);
//			str = textUtils.removeHashTagFromContent(str);
			str = textUtils.removeEmoticon(str);
			str = textUtils.removeNotWord(str);
			str = textUtils.removeUnexpectedDigit(str);
			out.write(str + "\n");
		}
		out.close();
		in.close();
	}
	
	public void rmAll() throws IOException{
		textUtils = new TextUtils();
		textUtils.makeDir(path);
		File folder = new File(pathPre);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        rmFile(file);
		    }
		}
	}
}
