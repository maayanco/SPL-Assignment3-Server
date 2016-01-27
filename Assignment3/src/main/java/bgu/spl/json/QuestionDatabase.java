package bgu.spl.json;

/**
 * Represents an array of QuestionInput objects
 * Assists in reading content from json file
 */
public class QuestionDatabase {
	private QuestionData[] questions;

	/**
	 * @return array of QuestionInput objects
	 */
	public QuestionData[] getQuestions() {
		return questions;
	}
}
