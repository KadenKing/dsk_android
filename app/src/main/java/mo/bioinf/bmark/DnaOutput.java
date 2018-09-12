package mo.bioinf.bmark;

import android.content.SyncStatusObserver;
import android.os.Parcelable;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DnaOutput {

    private List<String> DNA_sequences = new ArrayList<String>();

    Map<String,String> hex_map = new HashMap<>();
    Map<String, String> dna_map = new HashMap<>();


    public void printDNA(){

        if(DNA_sequences.size() == 0)
        {
            System.out.println("sequences empty");
        }

        for(String sequence : DNA_sequences){
            System.out.println(sequence);
        }
    }

    private byte[] readBytes(File input){
        byte[] ans;
        try{

            long filesize = input.length();
            InputStream inputstream = new FileInputStream(input);
            byte[] allBytes = new byte[(int) filesize];



            inputstream.read(allBytes);


            inputstream.close();

            return allBytes;

        }catch(java.io.IOException e){
            System.out.println(e.getMessage());
        }

        return null;


    }

    private String unNegative(String input)
    {
        return input.substring(6,input.length());
    }

    private List<String> create_blocks_of_4(List<String> input){
        List<String> ans = new ArrayList<>();
        String currentStr = "";
        for(int i = 1; i <= input.size(); i++)
        {
            currentStr += input.get(i-1);
            if(i%2 == 0)
            {
                ans.add(currentStr);
                currentStr = "";
            }
        }

        return ans;
    }

    private List<String> toStringBytes(byte[] input){

        List<String> ans = new ArrayList<>();

        for(int i = 0; i < input.length; i++){
            int temp = input[i];
            String hex = Integer.toHexString(temp);
            //int parsed = (int) Long.parseLong(hex,16);

            if(hex.length() > 2){
                //System.out.println(unNegative(hex));
                ans.add(unNegative(hex));
            }else{
                //System.out.println(extend(hex,2));
                ans.add(extend(hex,2));
            }

        }

        return ans;

    }

    private void pair_hex_to_abundances(List<String> input){

        String currentDnaHex = "";
        String currentAbundanceHex = "";
        for(int i = 1; i <= input.size(); i++)
        {
            int place_in_line = i%8;

            if(place_in_line <= 4 && place_in_line != 0)
            {
                currentDnaHex += input.get(i-1) + " ";



            }else{
                currentAbundanceHex += input.get(i-1) + " ";
            }

            if(place_in_line == 0) // finish a line
            {
               this.hex_map.put(currentDnaHex,currentAbundanceHex);

                currentDnaHex = "";
                currentAbundanceHex = "";
            }


        }


    }

    private String transformed_hex_to_dec(String input)
    {
        String[] split = input.split(" ");

        String[] transformed = {transform(split[3]), transform(split[2]),transform(split[1]),transform(split[0])};

        String combined = "";

        for(int i = 0; i < 4; i++)
        {
            combined += transformed[i];
        }
        //System.out.println(combined);
        return String.valueOf(Integer.parseInt(combined,16));

    }

    private void write_to_file()
    {
        String path = "/home/kaden/Documents/dsk/build/bin/output2.txt";

        File file = new File(path);

        try{

            BufferedWriter writer = new BufferedWriter(new FileWriter(path));

            for(Map.Entry<String,String> entry : this.dna_map.entrySet())
            {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
                //System.out.println("wrote");
            }

            writer.close();

        }catch(java.io.IOException e)
        {
            System.out.println(e.getMessage());
        }







    }

    public DnaOutput(File input) throws java.io.FileNotFoundException{

        if(input.exists() && input.canRead()) {

            System.out.println("exists and can read");


            byte[] bytes = readBytes(input);
            List<String> stringBytes = toStringBytes(bytes);

           List<String> blocks = create_blocks_of_4(stringBytes); // put into groups of 4 nibbles to make it look like hex view

         pair_hex_to_abundances(blocks);






        for(Map.Entry<String,String> entry : this.hex_map.entrySet())
        {
            //System.out.println("DNA: " + binary2dna(entry.getKey(),31) + " - Abundance: " + transformed_hex_to_dec(entry.getValue()));
            dna_map.put(binary2dna(entry.getKey(),31),transformed_hex_to_dec(entry.getValue()));
        }


        for(Map.Entry<String,String> entry : this.dna_map.entrySet())
        {
            System.out.println("DNA: " + entry.getKey() + " - Abundance: " + entry.getValue());
            //dna_map.put(binary2dna(entry.getKey(),31),transformed_hex_to_dec(entry.getValue()));
        }

        write_to_file();





        }

    }

    public DnaOutput(){

    }



    public static String binary2dna(String input, int kmersize)
    {
        String[] split = input.split(" ");

        if(split.length != 4)
        {
            return "error";
        }

        String[] hex = {transform(split[3]), transform(split[2]), transform(split[1]), transform(split[0])};

        String[] base4 = {hex2base4(hex[0]),hex2base4(hex[1]),hex2base4(hex[2]),hex2base4(hex[3])};

        String[] extended = {extend(base4[0],8),extend(base4[1],8),extend(base4[2],8),extend(base4[3],8)};

        String[] DNA = {base42dna(extended[0]),base42dna(extended[1]),base42dna(extended[2]),base42dna(extended[3])};

        String ans = "";
        for(int i = 0; i < 4; i++)
        {
            ans += DNA[i];
        }

        if(ans.length() > kmersize)
        {
            int difference = ans.length() - kmersize;
            ans = ans.substring(difference,ans.length());
        }


        return ans;
    }

    public static String transform(String input)
    {
        char[] inputChars = input.toCharArray();
        char[] old = input.toCharArray();

        inputChars[0] = old[2];
        inputChars[1] = old[3];
        inputChars[2] = old[0];
        inputChars[3] = old[1];

        return String.valueOf(inputChars);

    }

    public static String hex2base4(String input)
    {
        return Integer.toString(Integer.parseInt(input,16),4);

    }

    public static String extend(String input, int goalSize){
        int length = input.length();

        int difference = goalSize - length;

        String substr = "";
        for(int i = 0; i < difference; i++)
        {
            substr += "0";
        }

        return substr + input;


    }

    public static String base42dna(String input)
    {
        char[] inputChars = input.toCharArray();

        for(int i = 0; i < input.length(); i++)
        {
            char currentChar = inputChars[i];

            if(currentChar == '0')
                inputChars[i] = 'A';
            else if(currentChar == '1')
                inputChars[i] = 'C';
            else if(currentChar == '2')
                inputChars[i] = 'T';
            else if(currentChar == '3')
                inputChars[i] = 'G';
            else
                return "error";
        }

        return String.valueOf(inputChars);
    }


}
