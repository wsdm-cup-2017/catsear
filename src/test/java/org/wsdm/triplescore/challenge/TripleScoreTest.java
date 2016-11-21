package org.wsdm.triplescore.challenge;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class TripleScoreTest {
	
	@Test
	public void testRun1() throws FileNotFoundException, IOException {
		TripleScore ts = new TripleScore();
		ts.score("C:/Users/emarx/workspaces/starpath/WSDMTripleScoreChallenge/src/test/resources/profession.train.txt", "profession-out.txt");
		ts.score("C:/Users/emarx/workspaces/starpath/WSDMTripleScoreChallenge/src/test/resources/nationality.train.txt", "nationality-out.txt");
	}
}