package org.wsdm.triplescore.challenge;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class TripleScoreTest {
	
	@Test
	public void testRun1() throws FileNotFoundException, IOException {
		TripleScore ts = new TripleScore();
		ts.score("src/test/resources/profession.train.txt", "profession-out.txt");
		ts.score("src/test/resources/nationality.train.txt", "nationality-out.txt");
	}
	
	@Test
	public void testRun2() throws FileNotFoundException, IOException {
		TripleScore ts = new TripleScore();
		ts.score("src/test/resources/profession.train.small.txt", "profession-out.small.txt");
	}
}