package TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * some regexs for normalizing text
 * 
 *
 */
public class TextUtils {
	public void makeDir(String directoryName) {

		File theDir = new File(directoryName);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + directoryName);
			boolean result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				System.out.println("DIR created");
			}
		}
	}

	private static String lowerVN = "abcdefghijklmnopqrstvwxyzáàảãạăắặằẳẵâấầẩẫậđéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợuúùủũụưứừửữựýỳỷỹỵ";
	private static String upperVN = "ABCDEFGHIJKLMNOPQRSTVWXYZÁÀẢÃẠĂẮẶẰẲẴÂẤẦẨẪẬĐÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢUÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ";
	private static String number = "0123456789";
	private static String skipDigit = ",.\"\'\\“\\”\\‘\\’\\!\\?\\:\\-\\;\\(\\)\\/";
	private static String undexpectedDigitRegex = "[\"\'“”‘’]+";
	private static final String pathUnexpectedWord = "file/unexpected.txt";
	private static final String pathSpecialChar = "file/specialChar.txt";
	
	// REMOVE UNICODE CHAR: u0027, u0032, ...
	private static final String unicodeRegex = "u00[\\w\\d]{2}";
	private static final String findNameRegex = "^(.*?):";
	private static final String urlPattern = "((https?|ftp|gopher|telnet|file):[/]+[\\w:#@%/;$~_?\\+-=\\.&…]*)";
	private static final String hashtagRegex = " [#]+[\\s]?([\\S^(\\(\\))]+)";
	private static final String squareBracketRegex = "\\[(^])\\]";
	private static final String roundBracketRegex = "\\((^])\\)";
	private static final String domainRegex = "[a-z0-9]*(\\.net|\\.com|\\.org|\\.vn|\\.info|\\.biz|\\.co|\\.uk)+";
	private static final String emoticonRegex = "[a-z0-9]* emoticon";
	private static final Pattern invalidCharacter = Pattern.compile("• ");
	private static final Pattern quoteRegex = Pattern.compile("(by|By)(.*?)$");
	private static List<String> listUnexpectedWords, listSpecialWord;

	/**
	 * remove urls from a text
	 * 
	 * @param text
	 * @return String
	 * @throws IOException
	 */
	
	public void readUnexpectedWords() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathUnexpectedWord), "UTF8"));
		String str;
		listUnexpectedWords = new ArrayList<String>();
		while ((str = in.readLine()) != null) {
			listUnexpectedWords.add(str);
		}
		in.close();
	}
	
	public void readSpecialDigit() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathSpecialChar), "UTF8"));
		listSpecialWord = new ArrayList<String>();
		String digit;
		while ((digit = in.readLine()) != null) {
			listSpecialWord.add(digit);
		}
		in.close();
	}

	public String Upper(String word) {
		for (int i = 0; i < upperVN.length(); i++) {
			word = word.replaceAll(String.valueOf(lowerVN.charAt(i)), String.valueOf(upperVN.charAt(i)));
		}
		return word;
	}

	public String lower(String word) {
		for (int i = 0; i < upperVN.length(); i++) {
			word = word.replaceAll(String.valueOf(upperVN.charAt(i)), String.valueOf(lowerVN.charAt(i)));
		}
		return word;
	}

	public boolean isUpper(String word) {
		for (char digit : word.toCharArray()) {
			if (upperVN.indexOf(String.valueOf(digit)) == -1) {
				return false;
			}
		}
		return true;
	}

	public boolean isNumber(String word) {
		for (char digit : word.toCharArray()) {
			if (number.indexOf(String.valueOf(digit)) == -1) {
				return false;
			}
		}
		return true;
	}

	public boolean isLower(String word) {
		for (char digit : word.toCharArray()) {
			if (lowerVN.indexOf(String.valueOf(digit)) == -1) {
				return false;
			}
		}
		return true;
	}

	public boolean isWord(String word) {
		if (word == null || word == "") {
			return false;
		}
		for (char digit : word.toCharArray()) {
			if (!isLower(String.valueOf(digit)) && !isUpper(String.valueOf(digit)) && digit != '_') {
				return false;
			}
		}
		return true;
	}

	public boolean isN(String word) {
		return word.contains("/N");
	}

	public boolean isA(String word) {
		return word.contains("/A");
	}

	public boolean isV(String word) {
		return word.contains("/V");
	}

	public String rmTag(String word) {
		if (word.indexOf("/") == -1) {
			return word;
		}
		while (word.charAt(word.length() - 1) != '/') {
			word = word.substring(0, word.length() - 1);
		}
		word = word.substring(0, word.length() - 1);
		return word;
	}
	
	public String removeUnicode(String text){
		String newText = text;
		Pattern p = Pattern.compile(unicodeRegex, Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(newText);
		while (m.find()) {
			newText = newText.replaceAll(m.group(), "").trim();
		}
		return newText;	
	}

	public String removeUrls(String text) {
		String newText = text;
		Pattern p = Pattern.compile(urlPattern, Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(newText);
		while (m.find()) {
			newText = newText.replaceAll(m.group(), "").trim();
		}
		return newText;
	}

	public String removeEmoticon(String text) {
		String newText = text;
		Pattern p = Pattern.compile(emoticonRegex, Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(newText);
		while (m.find()) {
			newText = newText.replaceAll(m.group(), "").trim();
		}
		return newText;
	}

	/**
	 * Detecting a sentence which is a question sentence
	 * 
	 * @param sentence
	 * @return true if is a question sentence
	 */
	public boolean isQuestionSentence(String sentence) {
		if (sentence.contains("?")) {
			return true;
		}
		return false;
	}

	/**
	 * get name of person who commented
	 * 
	 * @param comment
	 * @return
	 */
	public String getNameFromComment(String comment) {
		String newText = comment;
		Pattern p = Pattern.compile(findNameRegex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(comment);
		int i = 0;
		while (m.find()) {
			newText = newText.replaceAll(m.group(i), "").trim();
			i++;
		}
		return newText;
	}

	public String removeNameFromComment(String comment) {
		String newText = comment;
		Pattern p = Pattern.compile(findNameRegex, Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(comment);
		while (m.find()) {
			newText = newText.replaceAll(m.group(), "").trim();
		}
		return newText;
	}

	/**
	 * get list hashtag from content
	 * 
	 * @param content
	 * @return list of hashtag
	 */
	public List<String> getHashTagFromContent(String content) {
		List<String> listOfHashTag = new ArrayList<String>();

		Pattern p = Pattern.compile(hashtagRegex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			listOfHashTag.add(m.group(0));
		}
		return listOfHashTag;
	}

	public String removeHashTagFromContent(String content) {
		String newText = content;
		Pattern p = Pattern.compile(hashtagRegex, Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(newText);
		while (m.find()) {
			newText = newText.replaceAll(m.group(), "").trim();
		}
		return newText;
	}

	/**
	 * get list of square brackets
	 * 
	 * @param content
	 * @return
	 */
	public List<String> getSquareBracket(String content) {
		List<String> listOFSquareBracket = new ArrayList<String>();

		Pattern p = Pattern.compile(squareBracketRegex);
		Matcher m = p.matcher(content);
		while (m.find()) {

			listOFSquareBracket.add(m.group());
		}
		return listOFSquareBracket;
	}

	public String removeSquareBracket(String content) {
		String newText = content;
		Pattern p = Pattern.compile(squareBracketRegex,
				Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(newText);
		while (m.find()) {
			newText = newText.replaceAll(m.group(), "").trim();
		}
		return newText;
	}

	public String removeRoundBracket(String content) {
		String newText = content;
		Pattern p = Pattern.compile(roundBracketRegex,
				Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(newText);
		while (m.find()) {
			newText = newText.replaceAll(m.group(), "").trim();
		}
		return newText;
	}

	/**
	 * get list domain in content
	 * 
	 * @param content
	 * @return
	 */
	public List<String> getDomains(String content) {
		List<String> listOfDomain = new ArrayList<String>();

		Pattern p = Pattern.compile(domainRegex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			listOfDomain.add(m.group());
		}
		return listOfDomain;
	}

	public String removeDomains(String content) {
		String newText = content;
		Pattern p = Pattern.compile(domainRegex, Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(newText);
		while (m.find()) {
			newText = newText.replaceAll(m.group(), "").trim();
		}
		return newText;
	}

	/**
	 * remove some invalid characters
	 * 
	 * @param content
	 * @return
	 */
	public String removeInvalidCharacter(String content) {
		content = invalidCharacter.matcher(content).replaceAll("");
		return content;
	}

	/**
	 * remove quote
	 * 
	 * @param content
	 * @return
	 */
	public String removeQuoteString(String content) {
		content = quoteRegex.matcher(content).replaceAll("");
		return content;
	}

	/**
	 * remove unexpected words as emotion and "See Translation".....
	 * 
	 * @param listUnexpectedWords
	 * @param content
	 * @return
	 */
	public String removeUnexpectedWord(String content) {
		for (String word : listUnexpectedWords) {
			content = content.replace(word, " ");
		}
		return content;
	}

	public String removeUnexpectedDigit(String content) {
		String newText = content;
		Pattern p = Pattern.compile(undexpectedDigitRegex,
				Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher m = p.matcher(newText);
		while (m.find()) {
			newText = newText.replaceAll(m.group(), "").trim();
		}
		return newText;
	}

	public boolean inList(String listCharacter, char digit) {
		for (char character : listCharacter.toCharArray()) {
			if (character == digit) {
				return true;
			}
		}
		return false;
	}

	public String removeNotWord(String content) {
		content = content.replace("…", " ");
		String[] words = content.split(" ");
		String new_content = "";
		for (String word : words) {
			boolean ok = true;
			for (char digit : word.toCharArray()) {
				if (!inList(lowerVN, digit) && !inList(upperVN, digit) && !inList(skipDigit, digit)
						&& !inList(number, digit)) {
					ok = false;
				}
			}
			if (!ok && word.contains(".")) {
				ok = true;
				word = " . ";
			}
			if (ok) {
				new_content += word + " ";
			}
		}
		return new_content;
	}
	
	public String removeSpecialChar(String str) throws IOException{
		for (String word : listSpecialWord) {
			str = str.replace(word, " ");
		}
		return str;
	}

	public boolean okSentence(String sentence) {
		String[] listWord = sentence.split(" ");
		int numWord = 0;
		for (int i = 0; i < listWord.length; i++) {
			if (isWord(listWord[i])) {
				numWord += 1;
			}
		}
		if (numWord > 1) {
			return true;
		}
		return false;
	}

	public String processComment(String comment) {
		String newComment = "";

		// CASE
		// [{"Name":"Nguyễn Lê Nam Phong","Comment":"Nguyễn Lê Nam Phong Haizzzz"}]
		Pattern pattern1 = Pattern.compile("\"Name\":\"(.*?)\",\"Comment\":\"(.*?)\"");
		Matcher matcher1 = pattern1.matcher(comment);
		while (matcher1.find()) {
			String name = matcher1.group(1);
			String cm_content = matcher1.group(2);
			newComment += cm_content.replace(name, "");
//			System.out.println(name + " ---- " + cm_content);
			if (newComment.length() != 0 && !".?!".contains(String.valueOf(newComment.charAt(newComment.length() - 1)))) {
				newComment += '.';
			}
		}

		// CASE
		// ["Khôi Hoàng: a ta ở nước ngoài","Tai Anh Nguyen: ở Vn chứ nc ngoài quái gì ^"]
		Pattern pattern2 = Pattern.compile("\"(.*?):(.*?)\"");
		Matcher matcher2 = pattern2.matcher(comment);
		while (matcher2.find()) {
			String name = matcher2.group(1);
			String cm_content = matcher2.group(2);
			newComment += cm_content;
//			System.out.println(name + " ---- " + cm_content);
			if (newComment.length() != 0 && !".?!".contains(String.valueOf(newComment.charAt(newComment.length() - 1)))) {
				newComment += '.';
			}
		}
		return newComment;
	}
}
