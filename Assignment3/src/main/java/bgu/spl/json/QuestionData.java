package bgu.spl.json;

/**
 * Class that represents an input object, contains the questionText 
 * and the real answer.
 * Assists in reading content from json file
 */
public class QuestionData {
	
	private String questionText;
	private String realAnswer;

	/**
	 * @return the questionText string
	 */
	public String getQuestionText() {
		return questionText;
	}

	/**
	 * @return the realAnswer string
	 */
	public String getRealAnswer() {
		return realAnswer;
	}
}
