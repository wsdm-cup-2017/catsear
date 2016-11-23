import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Cross {

	public static void main(String args[]) throws IOException {
		String wsdmFile = "wsdm.txt";
		//String msFile = "msLess.txt";
		String msFile = "ms.txt";
		HashMap<String, HashMap<String, Integer>> hWSDM = loadWSDMHash(wsdmFile);
		HashMap<String, HashMap<String, Integer>> hMicrosoft = loadMSHash(msFile);
		HashMap<String, HashMap<String, Integer>> hNewScore = crossNewScore(hWSDM, hMicrosoft);
		generateFile(hNewScore, wsdmFile, msFile);
	}

	private static void generateFile(HashMap<String, HashMap<String, Integer>> hNewScore, String name1, String name2) throws FileNotFoundException, UnsupportedEncodingException {

		String fileName =  name1 + "__" + name2;
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

	/*
	 * MS: Barack Obama Author 44 WSDM: Barack Obama Author 3 newScore =
	 * 2(10/44*7) newWSDM: Barack Obama Author 3
	 * 
	 * @
	 */
	private static HashMap<String, HashMap<String, Integer>> crossNewScore(
			HashMap<String, HashMap<String, Integer>> hWSDM, HashMap<String, HashMap<String, Integer>> hMicrosoft) {

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
						int newScore = Math.round(2.0f * (10.0f / oldScore * 7.0f));
						
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
			String[] elems = line.split("\\t");
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

	/*
	 * name1, name2, score
	 */
	private static HashMap<String, HashMap<String, Integer>> loadWSDMHash(String wsdmFile) throws IOException {
		String line;
		HashMap<String, HashMap<String, Integer>> ret = new HashMap<String, HashMap<String, Integer>>();
		// load each line and append it to file.
		BufferedReader br = new BufferedReader(new FileReader(new File(wsdmFile)));

		while ((line = br.readLine()) != null) {
			String[] elems = line.split("\\t");
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
