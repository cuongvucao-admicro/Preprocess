package ReadData;

import TextUtils.TextUtils;

public class Record {
	public String content;
	public String comment;
	public Integer keywordId;
	public String category;
	public Integer sourceId;
	
	public Record(String content, String comment, int i, String category, int sourceId) {
		super();
		this.content = content;
		System.out.println(content);
		this.comment = comment;
		this.keywordId = i;
		this.category = category;
		this.sourceId = sourceId;
	}
}
