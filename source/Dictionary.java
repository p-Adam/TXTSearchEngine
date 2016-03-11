

package search_engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


public class Dictionary {
    
    public Dictionary(String filesPath, ArrayList<String> wordList){
        path=filesPath;
        words=wordList;
        docNum=0;
        dict = new Hashtable();
        for(int i=0;i<words.size();i++){
            dict.put(words.get(i), new ArrayList<Integer>());
        }
        goThroughFiles();
        System.out.println();
    }
    
    public Hashtable dict;
    private String path;
    private ArrayList<String> words;
    private ArrayList<Integer> temp;
    private BufferedReader reader;
    private int docNum;
    
    //go through the files again to fill in the inverted indexes
    //copied from the Documents class
    private void goThroughFiles(){
        try {
            if (path == null) {
                System.out.println("Error");
            } else {
                //add files from folder
                //should only take .txt files
                File folder = new File(path);
                File[] listOfFiles = folder.listFiles();
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        if (file.getName().endsWith(".txt")){
                            reader = new BufferedReader(new FileReader(path + "\\"+ file.getName()));
                            goThroughWords(reader , docNum);
                            docNum++;
                        }
                    } else if(new File (path+"\\"+file.getName()).isDirectory()){
                        selectTXT(path+"\\"+file.getName());
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Documents.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("No documents were found.");
            JOptionPane.showMessageDialog(null, "No documents were found.");
            
        }
    }
    
    //go through all the words in the line adds the document ID to the inverted index of each word
    //a large part is copied from the documents class
    private void goThroughWords(BufferedReader fileName, int index){
        String line;
        try {
            BufferedReader fileReader = fileName;
            line = fileReader.readLine();
            while (line != null) {
                //if the line is empty then skip it
                if (line.equals("")) {
                    line = fileReader.readLine();
                    continue;
                }
                //remove extra characters from line
                line = line.trim();
                line = line.replace(".", "");
                line = line.replace(",", "");
                line = line.replace("+", "");
                line = line.replace("-", "");
                line = line.replace("/", "");
                line = line.replace("\\", "");
                line = line.replace("*", "");
                line = line.replace("(", "");
                line = line.replace(")", "");
                line = line.replace("{", "");
                line = line.replace("}", "");
                line = line.replace("[", "");
                line = line.replace("]", "");
                line = line.replace("|", "");
                line = line.replace("<", "");
                line = line.replace(">", "");
                line = line.replace("~", "");
                line = line.replace("?", "");
                line = line.replace("=", "");
                line = line.replace("^", "");
                //make everything lowercase
                line = line.toLowerCase();
                //split line into different words
                String[] pieces = line.split(" ");
                for (int i = 0; i < pieces.length; i++) {
                    //if piece is empty skip it
                    if (pieces[i].equals("")){
                        continue;
                    } else {
                        //take out the inv index of the current word and place into a temp list in order to be able to alter it
                        //it is restored later regardless of if it has been changed or not
                        temp = new ArrayList((Collection<? extends Integer>) dict.get(pieces[i]));
                        //if there are no document IDs in the inverted index then add the first one
                        if(temp.isEmpty()){
                            temp.add(index);
                        } else{
                            //if there are document IDs then check if this one has already been added
                            if (temp.contains(index)){  //if it has been added then continue with the next loop
                                continue;
                            } else{                     //if it hasn't been added then 
                                temp.add(index);
                            }
                        }
                        //put the changes into the hashtable
                        dict.put(pieces[i], temp);
                        System.out.println(pieces[i]+": ");
                        System.out.println(dict.get(pieces[i]));
                    }
                }
                //read the next line
                line = fileReader.readLine();
            }
            //the file has now been read
        } catch (IOException ex) {
            Logger.getLogger(Documents.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        }
    }
    
    private void selectTXT(String path) throws FileNotFoundException {
        //add files from folder
        //should only take .txt files
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().endsWith(".txt")) {
                    reader = new BufferedReader(new FileReader(path + "\\" + file.getName()));
                    goThroughWords(reader, docNum);
                    docNum++;
                }
            } else {
                File test = new File(path + "\\" + file.getName());
                if (test.exists() & test.isDirectory()) {
                    selectTXT(path + "\\" + file.getName());
                }
            }
        }
    }
    
    //search function
    public void search(String item, ArrayList<String> documents){
        item = reshapeQuery(item);
        System.out.println(item);
        boolean or=false;
        boolean timeToCompare = false;
        // check for OR statement. the OR statement is symbolised with the special char |
        if(item.contains("|")){
            if(item.endsWith("|") || item.startsWith("|")){
                System.out.println();
                JOptionPane.showMessageDialog(null, "your query may not start or end with this symbol: |");
                return;
            }
            or=true;
        }
        String[] query=item.split(" ");
        ArrayList<Integer> a  = new ArrayList<Integer>();
        ArrayList<Integer> b  = new ArrayList<Integer>();
        ArrayList<Integer> c  = new ArrayList<Integer>();
        //if there is only 1 word then just find that word
        if(query.length==1){
            getDocuments(find(item),documents);
        }
        //if there are 2 or more words in the query then compare results from all words with each other
        else {
            if(or==false){ //we are not working with the boolean OR statment
                a = find(query[0]);
                for(int i=1;i<query.length;i++){
                    b= find(query[i]);
                    a = compareResults(a, b); //compare a and b
                }
                if(!a.isEmpty()){
                    getDocuments(a,documents);
                }else
                    JOptionPane.showMessageDialog(null, "Your Search has failed.\nNo documents found");
            } else {
                //if the or stament is included
                a = find(query[0]);
                for(int i=1;i<query.length;i++){
                    if(!"|".equals(query[i])){
                        b = find(query[i]);
                        a = compareResults(a, b); //compare a and b
                    } else {
                        i++;//skip the |
                        if(timeToCompare==false) {  //when you first reach OR in a A OR B OR... sequence
                            c = a;                  //there is nothing to compare A with
                            a = find(query[i]);
                            timeToCompare = true;   //when you reach the second OR then you can start comparing
                        } else {                    //e.g. A OR B ,    (A OR B) OR C etc.. 
                            c = compareResultsOR(c,a);
                            a=find(query[i]);
                        }
                    }
                }
                a = compareResultsOR(a,c);//compare the final or
                if(!a.isEmpty()){
                    getDocuments(a,documents);
                }else{
                    JOptionPane.showMessageDialog(null, "your search has failed");
                }
            }
        }
    }
    
    //a AND b
    private ArrayList<Integer> compareResults(ArrayList<Integer> a,ArrayList<Integer> b){
        ArrayList<Integer> finalResult= new ArrayList<Integer>();
        for(int i=0;i<a.size();i++){
            if(b.contains(a.get(i))){
                finalResult.add(a.get(i));
            }
        }
        return finalResult;
    }
    
    //a OR c
    private ArrayList<Integer> compareResultsOR(ArrayList<Integer> x, ArrayList<Integer> y){
        ArrayList<Integer> finalResult= new ArrayList<Integer>();
        for(int i=0;i<x.size();i++){
            if(!finalResult.contains(x.get(i))){
                finalResult.add(x.get(i));
            }
        }
        for(int i=0;i<y.size();i++){
            if(!finalResult.contains(y.get(i))){
                finalResult.add(y.get(i));
            }
        }
        return finalResult;
    }
    
    private ArrayList<Integer> find(String x){
        Boolean flag;
        if(x.startsWith("!")){ //check if we are looking for an NOT statement  ("!"=NOT)
            flag = true;
        } else {
            flag = false;
        }
        x=x.replace("!", ""); //remove the ! if it exists
        if(dict.containsKey(x)){
                System.out.println(dict.get(x));
                ArrayList<Integer> result = new ArrayList((Collection<? extends Integer>) dict.get(x));
                if(flag){
                    result = reverse(result); // if we are looking for a not stament reverse the resulting inv index result=NOT(result)
                }
                return result;
            } else{
                JOptionPane.showMessageDialog(null, "your query has not been found");
                return null;
            }
    }
    
    //reverse index to contain all documents except those that contain the selected word
    public ArrayList<Integer> reverse(ArrayList<Integer> toReverse){
        ArrayList<Integer> reversedIndex = new ArrayList<Integer>();
        for(int i=0;i<docNum;i++){
            if (!toReverse.contains(i)){
                reversedIndex.add(i);
            }
        }
        return reversedIndex;
    }
    
    //get all documents that matched the query
    private void getDocuments(ArrayList<Integer> documentID, ArrayList<String> documents){
        try{
            String text = "";
            for(int i=0;i<documentID.size();i++){
                text = text + documents.get(documentID.get(i)) + "\n";
            }
            JOptionPane.showMessageDialog(null, text);
        } catch(Exception e){
            System.out.println(e);
        }
    }
    
    //remove all special characters except !
    private String reshapeQuery(String oldQuery){
        String newQuery = oldQuery;
        newQuery = newQuery.trim();
        newQuery = newQuery.replace(".", "");
        newQuery = newQuery.replace(",", "");
        newQuery = newQuery.replace("@", "");
        newQuery = newQuery.replace("#", "");
        newQuery = newQuery.replace("$", "");
        newQuery = newQuery.replace("%", "");
        newQuery = newQuery.replace("^", "");
        newQuery = newQuery.replace("&", "");
        newQuery = newQuery.replace("*", "");
        newQuery = newQuery.replace("(", "");
        newQuery = newQuery.replace(")", "");
        newQuery = newQuery.replace("-", "");
        newQuery = newQuery.replace("_", "");
        newQuery = newQuery.replace("=", "");
        newQuery = newQuery.replace("+", "");
        newQuery = newQuery.replace("[", "");
        newQuery = newQuery.replace("]", "");
        newQuery = newQuery.replace("{", "");
        newQuery = newQuery.replace("}", "");
        newQuery = newQuery.replace("\\", "");
        newQuery = newQuery.replace("`", "");
        newQuery = newQuery.replace("~", "");
        newQuery = newQuery.toLowerCase();
        return newQuery;
    }
    
}
