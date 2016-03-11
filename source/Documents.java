
package search_engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


public class Documents {
    
    public Documents() throws IOException {
        docNum=0;
    }

    public int docNum;
    public BufferedReader reader;
    public ArrayList words = new ArrayList();
    public ArrayList<String> docNames = new ArrayList();
    public ArrayList<Integer> docIDs = new ArrayList();
    public String path;
    
    public boolean addFiles() {
        try {
            path = JOptionPane.showInputDialog("please give the folder path \n eg C:\\Users\\Desktop\\Data");
            if (path == null) {
                System.out.println("Cancel is pressed");
                return false;
            } else {
                selectTXT(path);
                return true;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Documents.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "No documents were found.");
            return false;
        }
    }
    
     private void parseFile(BufferedReader fileName) {
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
                    }
                    //if the word is word list
                    if(!words.contains(pieces[i])){
                        words.add(pieces[i]);
                    }
                }
                //go to the next line
                line = fileReader.readLine();
            }
            //the file has now been read
        } catch (IOException ex) {
            Logger.getLogger(Documents.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        }
    }

    
    public void showFiles(){
        System.out.println();
        System.out.println("Files that have been inserted:");
        for(int i=0;i<docNames.size();i++){
            System.out.println(docNames.get(i));
        }
        System.out.println("Total number of files: "+docNames.size());
    }
    
    public void showWords(){
        System.out.println();
        System.out.println("Words in the dictionary:");
        for(int i=0;i<words.size();i++){
            System.out.println(words.get(i));
        }
        System.out.println("Total number of different words: "+words.size());
        
    }

    private void selectTXT(String path) throws FileNotFoundException {
        
                //add files from folder
                //should only take .txt files
                File folder = new File(path);
                File[] listOfFiles = folder.listFiles();
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        if (file.getName().endsWith(".txt")){
                            if(docNames.contains(file.getName())){ //if the file has already been read then skip it
                                continue;
                            }
                            reader = new BufferedReader(new FileReader(path + "\\"+ file.getName()));
                            docNames.add(file.getName());
                            docIDs.add(docNum);
                            parseFile(reader);
                            docNum++;
                        }
                    } else {
                        File test = new File (path+"\\"+file.getName());
                        if (test.exists() & test.isDirectory())
                            selectTXT(path+"\\"+file.getName());
                    }
                }
    }
    
}
