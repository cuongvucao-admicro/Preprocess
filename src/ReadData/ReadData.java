package ReadData;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

import com.mysql.jdbc.PreparedStatement;

import Constants.Constant;
import TextUtils.TextUtils;

public class ReadData {
	private static Constant constant = new Constant();
	private static String path = constant.data_set + "/01.raw/";
	public InputStream fileRmCate = getClass().getResourceAsStream("./file/removedCategory.txt");
	public InputStream fileRmWord = getClass().getResourceAsStream("./file/removedWord.txt");
	private static HashMap<String, Integer> listCateRemove = new HashMap<String, Integer>();
	private static HashMap<String, Integer> listWordRemove = new HashMap<String, Integer>();
	private TextUtils textUtils;

	public boolean containRmWord(String content) {
		for (Entry<String, Integer> entry : listWordRemove.entrySet()) {
			String word = entry.getKey();
			if (content.contains(word)) {
				return true;
			}
		}
		return false;
	}

	public void readData() throws IOException {
		textUtils = new TextUtils();
		textUtils.makeDir(path);
		readRmCate();
		readRmWord();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String userName = "cuongvc";
			String password = "sJyl1sGMZlUyYn9";
			String url = "jdbc:mysql://192.168.23.43/adtech.sentiment_new";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			System.out.println("Database connection established");
			HashMap<Integer, String> listKeyword = new HashMap<Integer, String>();
			pst = (PreparedStatement) conn.prepareStatement("select Id, Name from keyword utf8 ");
			rs = pst.executeQuery();
			while (rs.next()) {
				listKeyword.put(rs.getInt(1), rs.getString(2));
			}

			int start = 0;
			int limit = 2000;
			while (true) {
				String selectString = String.format(
						"select Subject, Summary, Content, Comments, KeywordId, Category, SourceId from contents utf8 limit %s, %s",
						start, limit);
				start += limit;

				pst = (PreparedStatement) conn.prepareStatement(selectString);
				rs = pst.executeQuery();
				ArrayList<Record> object = new ArrayList<Record>();
				while (rs.next()) {
					object.add(
							new Record(rs.getString(3), rs.getString(4), rs.getInt(5), rs.getString(6), rs.getInt(7)));
				}
				Iterator<Integer> keySetIterator = listKeyword.keySet().iterator();
				while (keySetIterator.hasNext()) {
					// key = keywordId
					Integer key = keySetIterator.next();
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(new File(path + listKeyword.get(key) + ".txt"), true));
					for (Record record : object) {
						if (record.keywordId == key && record.content != "") {
							if (!containRmWord(record.content) && !listCateRemove.containsKey(record.category)) {
								writer.write(record.content + '.');

								// if (record.comment != null && record.comment
								// != ""){
								// record.comment =
								// textUtils.processComment(record.comment);
								// writer.write(record.comment);
								// }
								writer.write("\n");
							}
						}
					}
					writer.close();
				}

			}
		} catch (Exception e) {
			System.err.println("Cannot connect to database server" + e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
					System.out.println("Database connection terminated");
				} catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}

	public void readRmCate() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(fileRmCate, "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			listCateRemove.put(str, 1);
		}
		in.close();
	}

	public void readRmWord() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(fileRmWord, "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			listWordRemove.put(str, 1);
		}
		in.close();
	}

	public static void main(String[] argv) throws IOException {

		ReadData data = new ReadData();
		data.readData();
	}
}
