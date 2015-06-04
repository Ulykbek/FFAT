package app;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import com.sun.media.sound.InvalidFormatException;
import java.io.File;
import java.util.ArrayList;

public class NLP{
    private String s1 = null;
    private String s2 = null;
    private ArrayList<String> namesList;
                
    public NLP(File dir,String fileName)throws InvalidFormatException, IOException{
        System.out.println(dir+"/"+fileName);
        String s = dir+"/"+fileName;
        namesList = new ArrayList<String>() ;
        FileReader fileReader = new FileReader(s);
	    
		  String fileContents = "";
		 
		  int i ;
		 
		  while((i =  fileReader.read())!=-1){
		   char ch = (char)i;
		 
		   fileContents = fileContents + ch;
		  }
		 
		InputStream is = new FileInputStream("en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
	 
		String sentences[] = sdetector.sentDetect(fileContents);
		
//		for (int j = 0; j < sentences.length; j++){
//			System.out.println(sentences[j]);
//		}
//		
		InputStream is1 = new FileInputStream("en-token.bin");
		 
		TokenizerModel model1 = new TokenizerModel(is1);
	 
		Tokenizer tokenizer = new TokenizerME(model1);
	 
		String tokens[] = tokenizer.tokenize(fileContents);
                
      
		
		
		InputStream is2 = new FileInputStream("en-ner-person.bin");
		 
		TokenNameFinderModel model2 = new TokenNameFinderModel(is2);
		is.close();
	 
		NameFinderME nameFinder = new NameFinderME(model2);
		
		Span nameSpans[] = nameFinder.find(tokens);
 
//		for(Span s: nameSpans)
//			System.out.println(s.toString());	
//		

		
                for(int k = 0; k < nameSpans.length; k++){
			//System.out.println(nameSpans[k]);
			s1 = new Utils2().getNumberOne(nameSpans[k].toString());
                        //System.out.println("!!!!!:"+s1);
                        s2 = new Utils2().getNumberTwo(nameSpans[k].toString());
                        //System.out.println("!!!!!:"+s2);
		
                int n1 = Integer.parseInt(s1);
                //System.out.println("n1:"+n1);
                
                int n2 = Integer.parseInt(s2);
                //System.out.println("n2:"+n2);
                
                int n3 = (n2-n1)+n1;
                
                
                int counter = 0;
		for (String a : tokens){
			counter++;
                        if(counter==n3-1 | counter==n3){
                            System.out.print(a);
                            System.out.print(" ");
                            /** add two ifs **/
                            
                        }
                        
                        
                }
                
                }
                

		is1.close();
    }
    public ArrayList<String> getNamesList(){
        return namesList;
    }
}