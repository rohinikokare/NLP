package NLP;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import NLP.QueryRepresentation.SelectionCriteria;
import NLP.QueryRepresentation.WhereSelection;

public class ProperNounDatabase {

	QueryRepresentation representationStyle = new QueryRepresentation();
	int comparisonnum;

	public class ProperNounReturned {
	SelectionCriteria selection;		
	public ProperNounReturned(SelectionCriteria selection, Integer numWords) {
	super();
	this.selection = selection;
	}
	}
	
	static Set<String> nounPhrases = new HashSet<String>();
	static String S;	
	static ArrayList<String> Nounlist = new ArrayList<String>();
	
public ArrayList<String> lookForProperNouns(String in) 
{
	InputStream modelInParse = null;
	try {
	//load chunking model
	modelInParse = new FileInputStream("C:/Users/921629/Downloads/en-parser-chunking.bin"); //from http://opennlp.sourceforge.net/models-1.5/
	ParserModel model = new ParserModel(modelInParse);	
	//create parse tree
	Parser parser = ParserFactory.create(model);
	Parse topParses[] = ParserTool.parseLine(in, parser, 1);	
	Pattern pattern = Pattern.compile("[^\\w\\s]*([\\w\\s]+)[^\\w\\s]*['s?]*[\\D]");		  
	Matcher matcher = pattern.matcher(in);
	System.out.println("\n Output eliminating punctation marks are : \n");
	while (matcher.find()) {		        	
	// Indicates match is found. Do further processing
	System.out.println(matcher.group(1));		          
	}
	System.out.println("output noun is:");
	for (Parse p : topParses)
	{
		p.show();
		getNounPhrases(p);	
	}	
	}
	catch (IOException e) {
	  e.printStackTrace();
	}
	
	finally {
	  if (modelInParse != null) {
	    try {
	    	modelInParse.close();
	    }
	    catch (IOException e) {
	    }
	  }
	}
	return Nounlist;
}

private void getNounPhrases(Parse p) {
	if (p.getType().equals("NN") || p.getType().equals("NNS")) //NP=noun phrase
    {
		nounPhrases.add(p.getText());
    	System.out.println(p);
    	p.getLabel();
    	S = String.valueOf(p);
    	Nounlist.add(S);
    	//System.out.println(Nounlist);
    }
	if (p.getType().equals("CD"))
		comparisonnum = 1;
   for (Parse child : p.getChildren())
        getNounPhrases(child);
}

}