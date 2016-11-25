import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Scanner;

public class Cross {

	public static void main(String args[]) throws IOException {
		String wsdmFile = args[0]; // "wsdm.txt";
		//String msFile = "msLess.txt";
		String msFile = args[1]; // "ms.txt";
		String demFile = args[2];
		HashMap<String, HashMap<String, Integer>> hWSDM = loadWSDMHash(wsdmFile);
		HashMap<String, HashMap<String, Integer>> hMicrosoft = loadMSHash(msFile);
		HashMap<String, HashMap<String, Integer>> hNewScore1 = crossNewScore(hWSDM, hMicrosoft);
		generateFile(hNewScore1, wsdmFile, msFile, "__1");
		HashMap<String, HashMap<String, Integer>> hNewScore2 = crossNewScoreDemonyms(hWSDM, hMicrosoft, demFile);
		generateFile(hNewScore2, wsdmFile, msFile, "__2");
	}

	private static void generateFile(HashMap<String, HashMap<String, Integer>> hNewScore, String name1, String name2, String suffix) throws FileNotFoundException, UnsupportedEncodingException {

		String fileName =  name1 + "__" + name2 + suffix;
		fileName = fileName.replaceAll(".txt", "");
		PrintWriter writer = new PrintWriter(fileName + ".txt", "UTF-8");
		
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
			HashMap<String, HashMap<String, Integer>> hWSDM, HashMap<String, HashMap<String, Integer>> hMicrosoft, String demFile) throws FileNotFoundException {
		
		// map: country to (first) demonym
		HashMap<String, String> dems = new HashMap<String, String>();
		Scanner in = new Scanner(new File(demFile));
		while (in.hasNextLine()) {
			String[] line = in.nextLine().split("\t");
			if (!dems.containsKey(line[0]))
				dems.put(line[0], line[1]);
		}
		in.close();
		
		// HashMap<String, Integer> maxScores = new HashMap<String, Integer>();
		// hMicrosoft.keySet().forEach(subject -> {
		// 	// get maxima
		// 	HashMap<String, Integer> objScoreMs = hMicrosoft.get(subject);
		// 	int max = Integer.MIN_VALUE;
		// 	for(String object : objScoreMs.keySet()) {
		// 		int score = objScoreMs.get(object);
		// 		if (score > max)
		// 			max = score;
		// 	}
		// 	maxScores.put(subject, max);
		// });
		
		HashMap<String, HashMap<String, Integer>> ret = new HashMap<String, HashMap<String, Integer>>();
		// hWSDM.keySet().parallelStream().forEach(elem ->
		hWSDM.keySet().forEach(subject -> {
			if (hMicrosoft.containsKey(subject)) {
				HashMap<String, Integer> objScoreMs = hMicrosoft.get(subject);
				HashMap<String, Integer> objScoreWSDM = hWSDM.get(subject);
				// for each target nationality...
				objScoreWSDM.keySet().forEach(obj -> {
					// check if a type contains the demonym
					String dem = dems.get(obj.toLowerCase());
					// for each Microsoft type
					for (String type : objScoreMs.keySet()) {
						if (type.toLowerCase().contains(dem)) {
							System.out.println("Hey, for "+subject+", '"+type+"' contains "+dem.toUpperCase());
							// HashMap<String, Integer> newObjScoreWSDM = new HashMap<String, Integer>();
							// int oldScore = objScoreMs.get(obj);
							// int maxScore = maxScores.get(subject);
							// int newScore = Math.round(1 + (float) oldScore / maxScore * 6.0f);
							//
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
				});
			}
		});

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
			HashMap<String, HashMap<String, Integer>> hWSDM, HashMap<String, HashMap<String, Integer>> hMicrosoft) {
		
		HashMap<String, Integer> maxScores = new HashMap<String, Integer>();
		hMicrosoft.keySet().forEach(subject -> {
			// get maxima
			HashMap<String, Integer> objScoreMs = hMicrosoft.get(subject);
			int max = Integer.MIN_VALUE;
			for(String object : objScoreMs.keySet()) {
				int score = objScoreMs.get(object);
				if (score > max)
					max = score;
			}
			maxScores.put(subject, max);
		});
		
		HashMap<String, HashMap<String, Integer>> ret = new HashMap<String, HashMap<String, Integer>>();
		// hWSDM.keySet().parallelStream().forEach(elem ->
		hWSDM.keySet().forEach(subject -> {
			if (hMicrosoft.containsKey(subject)) {
				HashMap<String, Integer> objScoreMs = hMicrosoft.get(subject);
				HashMap<String, Integer> objScoreWSDM = hWSDM.get(subject);
				objScoreWSDM.keySet().forEach(obj -> {
					if (objScoreMs.containsKey(obj)) {
						HashMap<String, Integer> newObjScoreWSDM = new HashMap<String, Integer>();
						int oldScore = objScoreMs.get(obj);
						int maxScore = maxScores.get(subject);
						int newScore = Math.round(1 + (float) oldScore / maxScore * 6.0f);
						
						if (ret.containsKey(subject))
							ret.get(subject).put(obj, newScore);
						else {
							newObjScoreWSDM.put(obj, newScore);
							ret.put(subject, newObjScoreWSDM);
						}
					}
				});
			}
		});

		return ret;
	}

	/*
	 * 
	 */
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
