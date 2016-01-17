package bgu.spl.container;

public class RoundData {
	private String bluffedAnswer = null;
	private String selectedAnswer = null;
	private int roundScore = 0;
	private boolean isCorrect = false;

	public RoundData() {
	}

	public String getBluffedAnswer() {
		return bluffedAnswer;
	}

	public void setBluffedAnswer(String bluffedAnswer) {
		this.bluffedAnswer = bluffedAnswer;
	}

	public String getSelectedAnswer() {
		return selectedAnswer;
	}

	public void setSelectedAnswer(String selectedAnswer) {
		this.selectedAnswer = selectedAnswer;
	}

	public int getRoundScore() {
		return roundScore;
	}

	public void setRoundScore(int roundScore) {
		this.roundScore = roundScore;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
}
