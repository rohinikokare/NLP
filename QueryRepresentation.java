package NLP;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import NLP.NLParser;

public class QueryRepresentation {

	// who, where, when, why, how, how many/count, how much, average, max, min 
	public enum SelectorInterrogative {
		COUNT, AVERAGE, SUM, SELECT_ONE,STAR, MIN, MAX
	}

	public enum ComparisonTypes {
		// not equals, 
		GREATER_THAN, LESS_THAN, EQUALS
	}
	
	private String printComparison(ComparisonTypes comparison) {
		switch (comparison){
		case EQUALS: 
			return "=";
		case GREATER_THAN:
			return ">";
		case LESS_THAN:
			return "<";
		}
		return "Error Code 101: There was no comparison between the attribute to the left and the value to the right.";}
	
	public enum SelectionConjunction {
		AND, OR, AND_NOT, OR_NOT
	}

	//  original query for printing
	String originalString;
	String cleanString;
	SelectorInterrogative question;
	String record;
	//  AttributeTypes attribute;
	ArrayList<String> Attribute = new ArrayList<String>();;
	SelectionCriteria[] selection = new SelectionCriteria[10];
	ComparisonTypes[] selectionComparison = new ComparisonTypes[10];
	SelectionConjunction[] conjunctions = new SelectionConjunction[9];
	boolean firstConjunctionNot = new Boolean(false);
	private String Attr = "";

	public class SelectionCriteria {		
	
		int numWords;
		ArrayList<String> Attribute = new ArrayList<String>();
		String value;   // this will be blank for all but the WhereSelection ones
		
		
	}

	public class WhereSelection extends SelectionCriteria {
		ComparisonTypes compare;

		@Override
		public String toString() {
			return "WhereSelection [attribute=" + Attribute + ", value=" + value + ", numWords=" + numWords + "]";
		}
		public WhereSelection(ArrayList<String> string, String value, int numWords) {
			this.Attribute = string;
			this.value = value;
			this.numWords = numWords;
		}		
	}

	public class GroupBySelection extends SelectionCriteria {

		public GroupBySelection(ArrayList<String> column) {
			this.Attribute = column;
		}

		@Override
		public String toString() {
			return "GroupBySelection [attribute=" + Attribute + "]";
		}
	}

	public class UnionSelection extends SelectionCriteria {
		String column;
	}

	public class OrderBySelection extends SelectionCriteria {
		String column;
	}

	@Override
	public String toString() {
		return "QueryRepresentation [originalString=" + originalString
		+ ", cleanString=" + cleanString + ", question=" + question
		+ ", record=" + record + ", firstConjunctionNot="
		+ firstConjunctionNot  + ", selection="
		+ Arrays.toString(selection) + ", selectionComparison="
		+ Arrays.toString(selectionComparison) + ", conjunctions="
		+ Arrays.toString(conjunctions) + "]";
	}

	public String generateSQL() {
		Integer criteriaCounter = new Integer(0);  //  how many criteria are you searching on
		String out = new String();
		boolean selectOneFlag = false;
		boolean haventPrintedWhereYet = true;
		
		out = "SELECT ";
		
		if(this.selection[criteriaCounter] != null && this.selectionComparison[criteriaCounter] == null)
		{
			int cnt = this.selection[criteriaCounter].Attribute.size();
			for (String s : this.selection[criteriaCounter].Attribute)
			{
					Attr += s;
					if(cnt>1)
					{
						Attr += ",";
						cnt--;
					}
			}			
			if (this.question == SelectorInterrogative.MAX) 
				out = out + "MAX(";
			else if (this.question == SelectorInterrogative.MIN) 
				out = out + "MIN(";
			else if (this.question == SelectorInterrogative.AVERAGE) 
				out = out + "AVG(";
			else if (this.question == SelectorInterrogative.SUM) 
				out = out + "SUM(";
			else if (this.question == SelectorInterrogative.COUNT)
				out = out + "COUNT(";
				
			out = out + Attr;
			
			if (this.question != null)
			{
				out = out + ")";
				criteriaCounter++;
			}
			
		}
		else 
			out = out + "*";

		
		
		out = out + " FROM ";
		out = out + this.record;
		if(this.selectionComparison[criteriaCounter] != null)
		{
			out = out + " WHERE ";
			for (String s : this.selection[criteriaCounter].Attribute)
			{
					Attr += s;
			}
			out = out + Attr;
			out = out + printComparison(this.selectionComparison[criteriaCounter]);
			out = out + this.selection[criteriaCounter].value;
		}
		//System.out.println(this.selectionComparison[criteriaCounter]);
		//  if there are criteria to be printed
		if (this.selection[criteriaCounter] != null && Attribute.size() > 1) {
			//  print criteria loop
			while (this.selection[criteriaCounter] != null) {
				//  only print criteria if they have a value to compare against
				if (this.selection[criteriaCounter].value != null) {
					if (haventPrintedWhereYet) {
						out = out + "WHERE ";
						haventPrintedWhereYet = false;
					}
					if (firstConjunctionNot) {
						out = out + "NOT ";
					}
					out = out + this.selection[criteriaCounter].Attribute + " ";
					out = out + printComparison(this.selectionComparison[criteriaCounter]) + " ";
					out = out + this.selection[criteriaCounter].value;
					if (this.conjunctions[criteriaCounter] != null) {
						switch (this.conjunctions[criteriaCounter]) {
						case AND:
							out = out + " AND ";
							break;
						case AND_NOT:
							out = out + " AND NOT ";
							break;
						case OR:
							out = out + " OR ";
							break;
						case OR_NOT:
							out = out + " OR NOT ";
							break;
						}

					}

				}
				criteriaCounter++;

			}
		}
		return out;
	}

}
