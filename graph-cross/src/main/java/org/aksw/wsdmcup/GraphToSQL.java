package org.aksw.wsdmcup;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class GraphToSQL {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, SQLException {
		
		String msFile = args[0];
		
		SQLiteManager sql = new SQLiteManager("msgraph.db", true);
		
		Scanner in = new Scanner(new File(msFile));
		for(int i=1; in.hasNextLine(); i++) {
			String[] line = in.nextLine().split("\t");
			String obj = line[0], sub = line[1], score = line[2];
			
			sql.insertTriple(sub, obj, score);
			if(i%100000 == 0) {
				sql.commit();
				System.out.println("i="+i);
			}
			
		}
		in.close();
		
		sql.close();

	}

}
