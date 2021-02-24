package NLP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;

public class NLParserRunner {

	static ResultSet res;
	public static void main(String[] args) {
		NLParser parser = new NLParser();
		QueryRepresentation query = new QueryRepresentation();
		String in = new String();

		//  get string
		InputStreamReader reader = new InputStreamReader (System.in);
		BufferedReader buf_in = new BufferedReader (reader);

		try {
			System.out.println("Enter the query: ");
			in = buf_in.readLine();
		} catch (Exception e) {
			System.err.println("Error occured in text entry: " + e);
		}

		query = parser.translate(in);

		System.out.println(query);
		
		System.out.println(query.generateSQL());


	}

}
