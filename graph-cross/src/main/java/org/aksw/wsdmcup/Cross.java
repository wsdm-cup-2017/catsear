package org.aksw.wsdmcup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;


/**
 * @author Andre Valdestilhas <valdestilhas@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Cross {
	
	private static SQLiteManager sql;
	
	public static void main(String args[]) throws IOException, ClassNotFoundException, SQLException {
		
		String scope = args[0];
		switch(scope) {
		case "cross":
			// continue in this class...
			break;
		case "build":
			GraphToSQL.main(new String[] { args[1] });
			return;
		default:
			return;
		}
		
		String wsdmFile = args[1]; // "wsdm.txt";
		String demFile = args[2];
		
		sql = new SQLiteManager("msgraph.db", false);
		
		System.out.println("Loading WSDM set...");
		HashMap<String, HashMap<String, Integer>> hWSDM = loadWSDMHash(wsdmFile);
//		System.out.println("Indexing MS set...");
//		HashMap<String, HashMap<String, Integer>> hMicrosoft = loadMSHash(msFile);
		HashMap<String, HashMap<String, Integer>> hMicrosoft = null;
		System.out.println("Generating predictions-4...");
		HashMap<String, HashMap<String, Integer>> hNewScore1 = crossNewScore(hWSDM, hMicrosoft);
		generateFile(hNewScore1, wsdmFile, "predictions-4.txt");
		System.out.println("Generating predictions-5...");
		HashMap<String, HashMap<String, Integer>> hNewScore2 = crossNewScoreDemonyms(hWSDM, hMicrosoft, demFile);
		generateFile(hNewScore2, wsdmFile, "predictions-5.txt");
	}

	private static void generateFile(HashMap<String, HashMap<String, Integer>> hNewScore, String name1, String outFile) throws FileNotFoundException, UnsupportedEncodingException {

		PrintWriter writer = new PrintWriter(outFile, "UTF-8");
		
		hNewScore.keySet().forEach(subject -> {
			HashMap<String, Integer> hObjScore = hNewScore.get(subject);
			hObjScore.keySet().forEach(obj -> {
				String line = subject + "\t" + obj + "\t" + hObjScore.get(obj);
				writer.println(line);
			});
		});
		writer.close();
	}
	
	private static HashMap<String, HashMap<String, Integer>> crossNewScoreDemonyms(
			HashMap<String, HashMap<String, Integer>> hWSDM, HashMap<String, HashMap<String, Integer>> hMicrosoft, String demFile) throws FileNotFoundException, SQLException {
		
		// map: country to (first) demonym
		HashMap<String, String> dems = new HashMap<String, String>();
		Scanner in = new Scanner(new File(demFile));
		while (in.hasNextLine()) {
			String[] line = in.nextLine().split("\t");
			if (!dems.containsKey(line[0]))
				dems.put(line[0], line[1]);
		}
		in.close();
		
		HashMap<String, HashMap<String, Integer>> ret = new HashMap<String, HashMap<String, Integer>>();
		// hWSDM.keySet().parallelStream().forEach(elem ->
		for(String subject : hWSDM.keySet()) {
			Set<String> keySet = sql.getObjects(subject);
//			if (hMicrosoft.containsKey(subject)) {
			if (!keySet.isEmpty()) {
				HashMap<String, Integer> objScoreWSDM = hWSDM.get(subject);
				// for each target nationality...
				for (String obj : objScoreWSDM.keySet()) {
//					HashMap<String, Integer> objScoreMs = hMicrosoft.get(subject);
					// check if a type contains the demonym
					String dem = dems.get(obj.toLowerCase());
					// for each Microsoft type
//					for (String type : objScoreMs.keySet()) {
					for (String key : keySet) {
						String type = key;
						System.out.println(subject + " => " + dem + " => " + type);
						if (type.toLowerCase().contains(dem)) {
							System.out.println("Hey, for "+subject+", '"+type+"' contains "+dem.toUpperCase());
							if (ret.containsKey(subject)) {
								HashMap<String, Integer> newObjScoreWSDM = ret.get(subject);
								if (newObjScoreWSDM.containsKey(obj)) {
									newObjScoreWSDM.put(obj, newObjScoreWSDM.get(obj) + 1);									
								} else {
									newObjScoreWSDM.put(obj, 1);
								}
							} else {
								HashMap<String, Integer> newObjScoreWSDM = new HashMap<String, Integer>();
								newObjScoreWSDM.put(obj, 1);
								ret.put(subject, newObjScoreWSDM);
							}
						}
					}
				}
			}
		}

		return ret;
	}

	/*
	 * MS: Barack Obama Author 10 (max=44)
	 * WSDM: Barack Obama Author 3
	 * newScore = (10/44*7) = 2
	 * newWSDM: Barack Obama Author 2
	 * 
	 * @
	 */
	private static HashMap<String, HashMap<String, Integer>> crossNewScore(
			HashMap<String, HashMap<String, Integer>> hWSDM, HashMap<String, HashMap<String, Integer>> hMicrosoft) throws SQLException {
				
		HashMap<String, HashMap<String, Integer>> ret = new HashMap<String, HashMap<String, Integer>>();
		// hWSDM.keySet().parallelStream().forEach(elem ->
		for(String subject : hWSDM.keySet()) {
			
//			if (hMicrosoft.containsKey(subject)) {
			//String subMs = hasSimilarSubject(hMicrosoft.keySet(), subject);
			//if(subMs != null){
//				HashMap<String, Integer> objScoreMs = hMicrosoft.get(subject);
				
				SQLObject s = sql.getEverything(subject);
				System.out.println(subject);
				if(!s.isEmpty()) {
					HashMap<String, Integer> objScoreWSDM = hWSDM.get(subject);
					for (String obj : objScoreWSDM.keySet()) {
	//					if (objScoreMs.containsKey(obj)) {
						Integer oldScore = s.getScore(obj);
						if (oldScore != null) {
							HashMap<String, Integer> newObjScoreWSDM = new HashMap<String, Integer>();
	//						int oldScore = objScoreMs.get(obj);
	//						int maxScore = maxScores.get(subject);
							int newScore = Math.round(1 + (float) oldScore / s.getMax() * 6.0f);
							
							if (ret.containsKey(subject))
								ret.get(subject).put(obj, newScore);
							else {
								newObjScoreWSDM.put(obj, newScore);
								ret.put(subject, newObjScoreWSDM);
							}
						}
					}
				}
//			}
		}

		return ret;
	}

	@SuppressWarnings("unused")
	private static String hasSimilarSubject(Set<String> keySet, String subject) {
		String ret = null;
		double score = 0.8d;
		for (String s : keySet) {
			//double scoreSim = JaroWinkler.jaroWinkler(s, subject);
			//double scoreSim = AndreMFKC.sim(s, subject, Math.max(s.length(), subject.length()));
			double scoreSim = Jaccard.jaccard_coeffecient(s, subject);
			if(scoreSim > score){
				ret = s;
				if(scoreSim == 1.0d) break;
				score = scoreSim;
			}
		}
		
		return ret;
	}
	
	/*
	 * 
	 */
	@SuppressWarnings("unused")
	private static HashMap<String, HashMap<String, Integer>> loadMSHash(String msFile)
			throws NumberFormatException, IOException {
		String line;
		HashMap<String, HashMap<String, Integer>> ret = new HashMap<String, HashMap<String, Integer>>();
		// load each line and append it to file.
		BufferedReader br = new BufferedReader(new FileReader(new File(msFile)));

		while ((line = br.readLine()) != null) {
			String[] elems = line.split("\t");
			String subject = elems[1].toLowerCase();
			String object = elems[0].toLowerCase();
			int score = Integer.parseInt(elems[2]);
			HashMap<String, Integer> objectScore = new HashMap<String, Integer>();

			if (ret.containsKey(subject))
				ret.get(subject).put(object, score);
			else {
				objectScore.put(object, score);
				ret.put(subject, objectScore);
			}
			
			
		}
		br.close();

		return ret;
	}

	/*
	 * name1, name2, score
	 */
	private static HashMap<String, HashMap<String, Integer>> loadWSDMHash(String wsdmFile) throws IOException {
		String line;
		HashMap<String, HashMap<String, Integer>> ret = new HashMap<String, HashMap<String, Integer>>();
		// load each line and append it to file.
		BufferedReader br = new BufferedReader(new FileReader(new File(wsdmFile)));

		while ((line = br.readLine()) != null) {
			String[] elems = line.split("\t");
			String subject = elems[0].toLowerCase();
			String object = elems[1].toLowerCase();
			int score = Integer.parseInt(elems[2]);
			HashMap<String, Integer> objectScore = new HashMap<String, Integer>();

			if (ret.containsKey(subject))
				ret.get(subject).put(object, score);
			else {
				objectScore.put(object, score);
				ret.put(subject, objectScore);
			}
		}
		br.close();

		return ret;
	}
}
