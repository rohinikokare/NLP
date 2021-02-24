package NLP;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.StringTokenizer;
import NLP.QueryRepresentation.ComparisonTypes;
import NLP.QueryRepresentation.SelectionConjunction;
import NLP.QueryRepresentation.SelectionCriteria;
import NLP.QueryRepresentation.SelectorInterrogative;

public class NLParser {


	final static String REGEX = "\\s"; // a single digit
	ResultSet res = null;
	ResultSet res1 = null;
	Connection con;
	ArrayList<String> criteria1;
	QueryRepresentation out = new QueryRepresentation();
	ArrayList<String> columns = new ArrayList<String>();
	Integer criteriaCounter = new Integer(0);  //  how many criteria are you searching on
	SelectionCriteria criteria = out.new SelectionCriteria();
	ArrayList<String> Attr = new ArrayList<String>();
		
	int column=0;
	public NLParser() {
	}
	
	public QueryRepresentation translate(String in) {
	
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con= DriverManager.getConnection("jdbc:mysql://localhost:3306/MySQL","root","root");
			if(con != null)
			{
				System.out.println("Connection Successful !!!");
				System.out.println("Close when done !!!");
			}
			
		}
		catch(Exception ex){
			System.out.println(ex);
		}
		
		

		out.originalString = in;

		//  strip punctuation from string or move it to new word
		in = StringCleanser.cleanse(in);
		in = in.toLowerCase();

		out.cleanString = in;

		String[] words = new String[in.split(REGEX).length];
		words = in.split(REGEX);
		
		StringTokenizer st = new StringTokenizer(in);
	                    System.out.println("Token are:");
		while (st.hasMoreTokens())
		{
			System.out.println(st.nextToken());        
		}   
		

		//  Only convert word to data if the period is in the middle like in 3.5 GPA
		for (int i=0; i>words.length; i++) {
			if (words[i].contains(".") && words[i].charAt(words[i].length()-1) == '.') {
				words[i] = words[i].substring(0, words[i].length()-2);
			}
		}

		ProperNounDatabase nounDatabase = new ProperNounDatabase();
		
		criteria1 = nounDatabase.lookForProperNouns(in);
		System.out.println("Criteria :"+criteria1);
		//  fill in selection criteria
		ListAllTables();
		SelectionCriteria criteria = out.new SelectionCriteria();
		
		while (words.length != 0 & criteriaCounter < 10 ) {
			if (out.selection[0] == null && words[0].equals("not")) {
					out.firstConjunctionNot = true;
					words = removeFirst(words);
			}
			
			if (out.selection[0] != null && out.selection[0].value == "" && words[0].equals("not")) {
					out.firstConjunctionNot = true;
					words = removeFirst(words);
			}
				

			//  fill in selection criteria
			if (criteria.numWords != 0){    //  if the number of words found is > than 0, a noun was found
				// for i .. number found in start of return string, loop and remove first word
				for (int i = 0; i < criteria.numWords; i++) {
					words = removeFirst(words);
				}
				out.selection[criteriaCounter] = criteria;

				//  check for conjunctions
				//  only check if we just found a proper noun and if the string is not empty
				if (words.length != 0)
				 {
					 
						if (in.contains("and not")) {
							out.conjunctions[criteriaCounter] = SelectionConjunction.AND_NOT;
							words = removeFirst(words);
							words = removeFirst(words);
							
						} else if (in.contains("and")) {
							out.conjunctions[criteriaCounter] = SelectionConjunction.AND;
							words = removeFirst(words);
						}
					 else if (in.contains("or")) {
						 out.conjunctions[criteriaCounter] = SelectionConjunction.OR;
						 words = removeFirst(words);
					 }else
						if (in.contains("or not")) {
							out.conjunctions[criteriaCounter] = SelectionConjunction.OR_NOT;
							words = removeFirst(words);
							words = removeFirst(words);
					}

					criteriaCounter++;
				}

			} else if (in.contains("how")) {				// fill in selector interrogative
				if (in.contains("many")) {
					out.question = SelectorInterrogative.COUNT;
					words = removeFirst(words);
					words = removeFirst(words);
				} else if (in.contains("much") || in.contains("sum")) {
					out.question = SelectorInterrogative.SUM;
					words = removeFirst(words);
					words = removeFirst(words);
				} 
			} else if (in.contains("average")){
				out.question = SelectorInterrogative.AVERAGE;
				words = removeFirst(words);
			} else if (in.contains("sum")){
				out.question = SelectorInterrogative.SUM;
				words = removeFirst(words);
			}else if (in.contains("max") || in.contains("maximum")){
				out.question = SelectorInterrogative.MAX;
				words = removeFirst(words);
			}else if (in.contains("min") || in.contains("minimum")){
				out.question = SelectorInterrogative.MIN;
				words = removeFirst(words);
			} /*else if (in.contains("who")){
				out.question = SelectorInterrogative.SELECT_ONE;
				words = removeFirst(words);

			} else if (in.contains("which")) { 
				if (in.contains("one")) {
					out.question = SelectorInterrogative.SELECT_ONE;
					words = removeFirst(words);
				}
			}*/
				else if(in.contains("number") || in.contains("all") || in.contains("list")){
					out.question = SelectorInterrogative.STAR;
					words = removeFirst(words);
				}

			 //  check for grouping criteria
			/*else if (words[0].equals("group") && words[1].equals("by")) {
				if (words[2].equals("major"))
					criteria = out.new GroupBySelection(AttributeTypes.MAJOR_SEEK);
				else if (words[2].equals("department"))
					criteria = out.new GroupBySelection(AttributeTypes.DEPARTMENT_NO);
				else if (words[2].equals("course"))
					criteria = out.new GroupBySelection(AttributeTypes.COURSE_NO);
				else if (words[2].equals("degree"))
					criteria = out.new GroupBySelection(AttributeTypes.DEGREE);
				else if (words[2].equals("ethnicity"))
					criteria = out.new GroupBySelection(AttributeTypes.ETHNICITY);
				out.selection[criteriaCounter] = criteria;
				words = removeFirst(words);
				words = removeFirst(words);
				words = removeFirst(words);
				criteriaCounter++;

			}*/
			//else ListAllTables();

			//  Search for record attributes: GRADE, DEPARTMENT_NO, SECTION_NO, CREDIT_HOURS, CUM_GPA, UNI_CUM_GPA, SESSION_NO, RESIDENCY, MAJOR_SEEK, MINOR_SEEK, 
			//    These are only available if searching by ProperNoun or grouping: COLLEGE, 
			//    These are only available for grouping: DEGREE, COURSE_NO, ETHNICITY
			//    These are only available if searching by ProperNoun: ACADEMIC_CHARACTERISTIC
	/*		 if (words[0].equals("grade") | words[0].equals("score")) {
				out.selection[criteriaCounter] = out.new WhereSelection(AttributeTypes.GRADE, "", 1);
				criteriaCounter++;
				words = removeFirst(words);				
			} else if (words[0].equals("record") | words[0].equals("records")) {
				out.selection[criteriaCounter] = out.new WhereSelection(AttributeTypes.RECORD, "", 1);
				criteriaCounter++;
				words = removeFirst(words);				
			} else if (words[0].equals("department")) {
				out.selection[criteriaCounter] = out.new WhereSelection(AttributeTypes.DEPARTMENT_NO, "", 1);
				criteriaCounter++;
				words = removeFirst(words);				
			} else if (words[0].equals("gpa")) {
				out.selection[criteriaCounter] = out.new WhereSelection(AttributeTypes.CUM_GPA, "", 1);
				criteriaCounter++;
				words = removeFirst(words);				
			} else if (words[0].equals("session")) {
				out.selection[criteriaCounter] = out.new WhereSelection(AttributeTypes.SESSION_NO, "", 1);
				criteriaCounter++;
				words = removeFirst(words);				
			} else if (words[0].equals("resident")) {
				out.selection[criteriaCounter] = out.new WhereSelection(AttributeTypes.RESIDENCY, "", 1);
				criteriaCounter++;
				words = removeFirst(words);				
			} else if (words[0].equals("declaration")) {
				out.selection[criteriaCounter] = out.new WhereSelection(AttributeTypes.MAJOR_SEEK, "", 1);
				criteriaCounter++;
				words = removeFirst(words);				
			} else if (words[0].equals("minor")) {
				out.selection[criteriaCounter] = out.new WhereSelection(AttributeTypes.MINOR_SEEK, "", 1);
				criteriaCounter++;
				words = removeFirst(words);				
			}*/


			else if (in.contains("equal") | in.contains("is")) {
				if (out.selection[criteriaCounter] == null) {
					//  if there is no selection column, then this is the first one.  
					//  otherwise put the in/is in the previous spot
					out.selectionComparison[criteriaCounter] = ComparisonTypes.EQUALS;
					
				} else {
					out.selectionComparison[criteriaCounter-1] = ComparisonTypes.EQUALS;
					
				}

				words = removeFirst(words);
			}
				
			else if (in.contains("greater") || in.contains("more") || in.contains("greater than")) {
				out.selectionComparison[criteriaCounter-1] = ComparisonTypes.GREATER_THAN;
				out.selection[criteriaCounter-1].value = words[2];
				words = removeFirst(words);
				words = removeFirst(words);
				words = removeFirst(words);
			}
			else if (in.contains("less") || in.contains("smaller") || in.contains("less than")) {
				out.selectionComparison[criteriaCounter-1] = ComparisonTypes.LESS_THAN;
				out.selection[criteriaCounter-1].value = words[2];
				words = removeFirst(words);
				words = removeFirst(words);
				words = removeFirst(words);
				
			}

			else {
				words = removeFirst(words);   // the word didn't match any keywords
			}
		}

		return out;
	}


	private void ListAllTables() {
		DatabaseMetaData meta = null;
		ResultSetMetaData rsmd;
		try {
			meta = con.getMetaData();
		
			res = meta.getTables(null, null, "%", null);
	      //System.out.println("List of tables: "); 
	      
			while (res.next()) {
			     //System.out.println(res.getString(3));
					if(criteria1.contains(res.getString(3)))
					{
						out.record = res.getString(3);	
						System.out.println("Table name is : "+out.record);
						break;
				    }
				
			}
			rsmd = res.getMetaData();
			res1 = meta.getColumns(null, null, out.record, null);
			
		     while(res1.next()){
		    	 column = rsmd.getColumnCount();
		    	 String name = res1.getString("COLUMN_NAME");
		    	 columns.add(name );
		    	 //out.selection[criteriaCounter].Attribute.add(name);
		    	 if(criteria1.contains(name))
		    	 {
		    		 Attr.add(name); 
		    		 out.selection[criteriaCounter] = out.new WhereSelection(Attr, "", 1);
		    		 criteriaCounter++;
		    	 }
		    	 
		    	 //criteria.Attribute.add(name);
		    	 //criteriaCounter++;
		     }
		    // System.out.println(column);
		 
		     System.out.println(columns);
		     
		     
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}

	private String[] removeFirst(String[] words) {
		String[] wordsShort = new String[words.length-1];
		for (int i = 1; i<words.length; i++){
			wordsShort[i-1] = words[i]; 
		}

		// garbage collection needed?
		return wordsShort;
	}
}
