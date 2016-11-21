package org.wsdm.triplescore.challenge;

/**
 * 
 * @author emarx
 *
 */
public class Entry {
	private String subject;
	private String object;
	private Integer score;
	
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * @return the object
	 */
	public String getObject() {
		return object;
	}
	
	/**
	 * @param object the object to set
	 */
	public void setObject(String object) {
		this.object = object;
	}

	/**
	 * @return the score
	 */
	public Integer getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Integer score) {
		this.score = score;
	}
	
	@Override
	public String toString() {	
		return subject + "\t" + object + "\t" + (score!=null?Integer.toString(score):"0");
	}

	public String getURLSubject() {
		return subject.replace(" ", "_");
	}
	
}
