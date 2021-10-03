package com.luoboanalytics.study_chinese_with_twitter.parse;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.luoboanalytics.study_chinese_with_twitter.MainActivity;
import com.luoboanalytics.study_chinese_with_twitter.analysis.Word;
import com.luoboanalytics.study_chinese_with_twitter.db.DataBaseHelper;
import com.twitter.sdk.android.core.models.Search;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dictionary {
    // Helpful guide to accessing file from assets
    // https://stackoverflow.com/questions/13814503/reading-a-json-file-in-android/13814551#13814551

    private static String DictTreePath = "dictionary_tree.json";
    private static String DictMapPath  = "dictionary_map.json";

    private static JSONObject tree;
    private static JSONObject dict;

    // Methods to load JSON directly from ASSETS folder
    public static String getFilesPath(Context c) {
        return Environment.getDataDirectory() +
                File.separator + "data" +
                File.separator + c.getPackageName() +
                File.separator + "files";
    }
    public static String loadJSONFromAsset(Context context, String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // Load the JSON files from assets
    public static JSONObject loadDictMap(Context context, String filepath){
        String jsonString = loadJSONFromAsset(context, filepath);
        JSONObject dict = new JSONObject();

        try {
            dict = new JSONObject(jsonString);
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("DICT", "Failed to load dict map from file");
        }
        return dict;
    }
    public static JSONObject loadDictTree(Context context, String filepath){
        String jsonString = loadJSONFromAsset(context, filepath);
        JSONObject tree = new JSONObject();

        try {
            tree = new JSONObject(jsonString);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("DICT", "Failed to load JSON");
        }
        return tree;

    }

    // Methods allowing this class to search the Dictionary Tree given a search string
    public static void BuildAll(Context context){
        tree = loadDictTree(context, DictTreePath);
        dict = loadDictMap(context, DictMapPath);

        //Test that treejson loaded correctly by searching for: (:富 (:大 dictmap))
        try {
            JSONObject entry1 = tree.getJSONObject("大");
            Log.i("TREE", "Successfully built tree");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("DICTTREE", "JSON tree failed test");
        }
    }
    public static String matchRegex(String regexString, String searchString){
        Pattern p = Pattern.compile(regexString);
        Matcher m = p.matcher(searchString);
        String result = "";
        if(m.find()) {
            result = m.group();
        }

        return result;
    }

    // Search a branch
    // Returns a JSON Object that is either a new branch, a Result: Fail, or an ID: <word id>
    public static JSONObject SearchBranch (JSONObject tree, String character) throws JSONException {

        // If not match is found, returns result: fail
        JSONObject returnJSON = new JSONObject("{\"ID\": \"FAIL\"}");

        // Search current TREE for CHARACTER
        try
        {
            // Found a subtree for this next character - return the tree
            JSONObject branch = tree.getJSONObject(character);
            returnJSON = branch;
        }
        catch (JSONException e)
        {

            // Could not find a tree for the next character
            // First, see if this is a stopping point - if this tree has a 'DONE' key then it can be done
            // Else return ID: FAIL
            try
            {
                String id = tree.getString("DONE");
                returnJSON = new JSONObject("{\"ID\": \"" + id + "\"}");


            }
            catch (JSONException e2){
                // Failed to find a branch with 'character' key - return default FAIL
            }

        }
        //Log.i("BRANCH", "Returning JSON: " + returnJSON.toString());
        return returnJSON;
    }

    public static Word ParseSentenceNext(String sentence, JSONObject TreeSearch) throws JSONException {
        Word outWord = null;

        Integer count = 0;

        // First test for English/Numbers/Hashtags/@Persons/URLs and return entire segment
        //String nonHanMatch = matchRegex("^[@#./:a-zA-Z0-9]*", sentence);
        String nonHanMatch = matchRegex("^[^\u4e00-\u9FFF]*", sentence);

        // The first character of the test string will be used if everything fails
        String firstCharacter = Character.toString(sentence.charAt(0));


        if (!nonHanMatch.equals("")) // Only start searching dictionary tree if this is chinese characters
        {
            // Build a Word using this english string
            outWord = new Word(nonHanMatch);
        }

        // Search chinese dictionaries
        else {

            JSONObject currentBranch = TreeSearch;

            // Split the test sentence into characters to test one at a time
            for (char ch : sentence.toCharArray()) {

                // Character that will be searched for
                String character = Character.toString(ch);
                count ++;

                // Search branches for the first character
                // If nothing is returned that is considered a match, move to exception
                try{
                    JSONObject resultJSON = SearchBranch(currentBranch, character);

                    // If result has "ID" key, then it is either definitely done (and has an id) or it has failed
                    try {
                        String id = resultJSON.getString("ID");
                        if (id.equals("FAIL"))
                        {
                            // Character doesn't exist in map
                            // This means that we must return the FIRST character in the test string only,
                            // EVEN IF there is already more than one match. This is to prevent partial matches like
                            // 让人 which matches 让人羡慕
                            outWord = new Word(firstCharacter);
                            break;

                        }

                        // Found a word! Used the returned ID to search the dictionary map
                        else
                        {
                            try{
                                JSONObject entry = dict.getJSONObject(id);
                                outWord = new Word(entry);
                            }
                            catch (Exception e){
                                Log.e("DICT", "Found id " + id + " but id is not in dictionary map");
                            }
                            break;

                        }

                    }

                    // Branch at this character found
                    catch (Exception e)
                    {

                        // If this is the last character of the sentence to test, and a full branch is returned, check if it can be 'done'
                        if (sentence.length() == count){
                            try {
                                String id = resultJSON.getString("DONE");
                                try{
                                    JSONObject entry = dict.getJSONObject(id);
                                    outWord = new Word(entry);
                                }
                                catch (Exception e2){
                                    Log.e("DICT", "Found id " + id + " but id is not in dictionary map");
                                }
                                break;
                            }
                            catch (Exception e3){
                                outWord = new Word(firstCharacter);
                                break;
                            }
                        }
                        else
                        {
                            // Set the tree to this branch, then continue
                            currentBranch = resultJSON;
                        }

                    }


                }
                // Current character not in Tree - must not be chinese character, or too rare
                catch (Exception e){

                    outWord = new Word(firstCharacter);
                    break;
                }

            }


        }


        //Log.d("ParseSentenceNext", "Returning word: " + outString);
        return outWord;
    }

    // Main function to parse a chinese sentence - Uses Dictionary Tree to find largest word at each point, and regex to ignore english/numbers
    public static ArrayList ParseSentence(String sentence, JSONObject TreeSearch) throws JSONException {
        ArrayList ParsedWords = new ArrayList();
        StringBuilder parsedSentence = new StringBuilder(); // Debug string which hold a string example of parse like "a/bce/def/"
        String restSentence = sentence;

        //Log.d("Parse Sentence", "Parsing sentence: " + sentence);

        Boolean test = true;
        Integer count = 0;
        while (test)
        {
            if (count > 500)
            {
                test = false;
            }
            else
            {
                count = count + 1;
               Word nextWord = ParseSentenceNext(restSentence, TreeSearch);
               //Log.d("Parse Sentence", "Next word: " + nextWord);

                Integer wordLength = 1;

               // Cannot parse the string, assume that first character is strange (like @)
               if (nextWord == null){
                   Log.e("DICT", "nextword is null");
               }

               else
               {
                    wordLength = nextWord.wordLength();
                    ParsedWords.add(nextWord);
                    parsedSentence.append("/" + nextWord.getDisplayText());

               }

               // Subtract this found word from beginning of sentence, then repeat
               restSentence = restSentence.substring(wordLength);
               //Log.d("Parse Sentence", "New next sentence: " + restSentence);
               if (restSentence.length() == 0)
               {
                   test = false;
               }

            }
        }

        Log.i("Parse", "Parsed sentence: " + parsedSentence.toString());
        return ParsedWords;
    }


    public static ArrayList Parse(String sentence) throws JSONException {
        ArrayList parsed = ParseSentence(sentence, tree);
        return parsed;
    }

}
