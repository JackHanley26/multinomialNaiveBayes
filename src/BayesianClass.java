/**
 * Created by jackhanley on 05/11/2015.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BayesianClass {
    double posDocs = 0, negDocs = 0, totalDocs = 0;

    public static void main(String[] args) {


        BayesianClass bayesianClass = new BayesianClass();


        //Maps of words with values of sum of existence in files
        Map<String, Integer> posMap = new HashMap<String, Integer>();
        Map<String, Integer> negMap = new HashMap<String, Integer>();


        // Set will contain all unique words from the data set
        Set<String> vocabulary = new HashSet<String>();
        List<String> allWords = new ArrayList<String>();


        // Access all files in the dataset and store the words in the set
        bayesianClass.loadFilesFromDirectory(false, "LargeIMDB/pos", vocabulary, posMap, allWords);
        bayesianClass.loadFilesFromDirectory(false, "LargeIMDB/neg", vocabulary, negMap, allWords);



        // bayesianClass.printOut(vocabulary);
        // bayesianClass.printMap(negMap, allWords);
        // bayesianClass.printMap(posMap, allWords);

        System.out.println("Total words " + bayesianClass.getTotalWordsIn(negMap));
        System.out.println("Total over all words " + allWords.size());

        //Testing data
        Set<String> testVocab = new HashSet<String>();
        Map<String, Integer> testMap = new HashMap<String, Integer>();
        List<String> testAllWords = new ArrayList<String>();

        bayesianClass.loadFilesFromDirectory(true, "smallTest/pos", testVocab, testMap, testAllWords);

        System.out.println("Testing words total " + testVocab.size());

        double prob = StrictMath.log(bayesianClass.posDocs / bayesianClass.totalDocs);

        for (String word : testAllWords) {
            //This is the count of how many times "word" occurs within a class e.g "Bad" 60 times etc.
            double value1 = posMap.get(word);
            double value2 = bayesianClass.posDocs + vocabulary.size();
            prob += Math.log(value1 / value2);
        }

        System.out.println("Probability of " + prob);

        // pos =  -380.1992710512408

    }

    public void printOut(Set<String> vocabulary) {

        int numberOfUniqueWords = 0;
        for (String word : vocabulary) {
            System.out.println(word);
            numberOfUniqueWords++;
        }


    }

    public void printMap(Map<String, Integer> map, List<String> allWords) {
        for (Map.Entry<String, Integer> oneSet : map.entrySet()) {
            String key = oneSet.getKey();
            Integer value = oneSet.getValue();
            System.out.println(key + " : " + value);

        }
        System.out.println("Number of Unique Words Stored => " + map.size());
        System.out.println("total number of words counted => " + allWords.size());

        System.out.println("P(c) = " + posDocs + " / " + totalDocs);


    }

    public void addWordToMap(String word, Map<String, Integer> map) {
        if (map.containsKey(word)) {
            map.put(word, map.get(word) + 1);
        } else {
            //Adds a new word and sets its occurrence to 1
            map.put(word, 1);
        }
    }

    public void captureFileContents(String fileName, Set<String> vocabulary, Map<String, Integer> map, List<String> allWords) {

        try {
            Scanner reader = new Scanner(new File(fileName));

            // Read each string from the file and add to the set
            while (reader.hasNext()) {
                //Removes all he special characters and converts all characters in word to lower case // Might add this later replaceAll("\\s+", "+")
                String words = ((reader.next()).replaceAll("<br", "").replaceAll("-", " ").replaceAll("[^\\dA-Za-z]", " ")).toLowerCase();
                for (String word : words.split("\\s+")) {
                    word = word.replaceAll("\\s+", "");

                    if (word.length() > 3) {
                        vocabulary.add(word);
                        addWordToMap(word, map);
                        allWords.add(word);
                    }
                }

            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public int getTotalWordsIn(Map<String, Integer> map) {
        int total = 0;
        for (Map.Entry<String, Integer> map1 : map.entrySet()) {
            total += map1.getValue();
        }
        return total;
    }


    public void loadFilesFromDirectory(boolean testing, String dirName, Set<String> vocabulary, Map<String, Integer> map, List<String> allWords) {

        String path = "/Users/jackhanley/Documents/College/Artiicial Intelligence/workspace/AIAssignment2/data/" + dirName + "/";

        String fileName;
        File folder = new File(path);

        // Creates an array of File from the specified path and directory
        File[] listOfFiles = folder.listFiles();

        // Iterates through all the files in the directory
        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                fileName = listOfFiles[i].getName();
                // Open the specified file and adds it's contents into the set
                captureFileContents(path + fileName, vocabulary, map, allWords);
                if (!testing) {
                    if (dirName.equals("LargeIMDB/pos")) {
                        this.posDocs++;
                    } else {
                        this.negDocs++;
                    }
                    this.totalDocs++;
                } else {
                    //Break after one record, if in testing mode
                    break;
                }
            }
        }
    }
}
