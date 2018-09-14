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

    //private List<String> DNA_sequences = new ArrayList<String>();


    private List<File> solids = new ArrayList<>();

    private Map<String,String> hex_map = new HashMap<>();
    private Map<String, String> dna_map = new HashMap<>();

    private String fastq_name = "";
    private String basepath = "";


    /**
     * For now, the constructor automatically converts each dsk.solid.* it can find into a single
     * filename_dna.txt file.
     *
     *
     * @param fastq_name
     * @param basepath
     * @throws java.io.FileNotFoundException
     */
    public DnaOutput(String fastq_name, String basepath) throws java.io.FileNotFoundException{

        this.fastq_name = fastq_name;
        this.basepath = basepath;

        find_solids();

        for(File file : this.solids)
        {
            this.hex_map.clear(); // clears hashmap for new read


            if(file.exists() && file.canRead()) {

                System.out.println("exists and can read");


                byte[] bytes = readBytes(file);
                List<String> stringBytes = toStringBytes(bytes);

                List<String> blocks = create_blocks_of_4(stringBytes); // put into groups of 4 nibbles to make it look like hex view
                pair_hex_to_abundances(blocks);


                for(Map.Entry<String,String> entry : this.hex_map.entrySet())
                {
                    //System.out.println("DNA: " + binary2dna(entry.getKey(),31) + " - Abundance: " + transformed_hex_to_dec(entry.getValue()));
                    dna_map.put(binary2dna(entry.getKey(),31),transformed_hex_to_dec(entry.getValue()));
                }


            }


        }

        write_to_file();

    }

    /**
     * based on based path and filename, it finds all of the dsk solids.
     * It puts them into a list of files that will later be read and parsed
     */
    private void find_solids()
    {
        int count = 0;


        while(true)
        {
            String path = this.basepath + this.fastq_name + "_gatb/dsk.solid." + count;
            File file = new File(path);
            if(file.exists() && file.canRead())
            {
                this.solids.add(file);
                count++;
            }else{
                System.out.println("could not find " + path);
                break;
            }

        }

    }


    /**
     * writes the dna_map file into a txt file.
     */
    public void write_to_file()
    {
        String path = this.basepath + this.fastq_name + "_dna.txt";

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

    public Map<String, String> getHex_map() {
        return hex_map;
    }

    public Map<String, String> getDna_map() {
        return dna_map;
    }


    /**
     * Reads the binary file and puts all its values into a byte array.
     * This byte array is what is eventually manipulated into dna sequences and abundances
     *
     * @param input
     * @return
     */
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

    /**
     * Java assumes all bytes read are in 2's complement, so if the binary form of any byte starts with a 1,
     * Java treats it as a negative number. DSK wrote the binaries as unsigned binary numbers, so this needs to be undone.
     *
     * Java sign extends the bytes, so they are all strings of 8 length when this happens, so we can
     * just remove the first 6 characters from the string to remove the sign extension.
     *
     * @param input
     * @return
     */
    private String unNegative(String input)
    {
        return input.substring(6,input.length());
    }


    /**
     * When reading the original binary files they are in the form:
     * 023c c400 3203 0400 0200 0000 0000 0000
     * 0404 0404 0404 0400 0200 0000 0000 0000
     * a442 530f 2c94 0400 0400 0000 0000 0000
     * c001 b4a1 0827 0500 0200 0000 0000 0000
     * ...
     *
     * but java reads 1 byte at a time.
     *
     * This method puts them into chunks of 4 so that it is easier to understand what is going on later.
     *
     * @param input
     * @return
     */
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

    /**
     * Puts each byte into a list of strings.
     * Also makes sure to remove any "negative" extended numbers from showing up.
     * Ensures that every string is a 2 character string.
     *
     * @param input
     * @return
     */
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

    /**
     * An example line that this method would read takes this form:
     * 023c c400 3203 0400 0200 0000 0000 0000
     *
     * The first 4 chunks are the hexadecimal representation of the dna sequence, and the
     * second 4 chunks are abundance.
     *
     * This method pairs the dna chunk to the abundance chunk for processing later
     * @param input
     */
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

    /**
     *This method converts the abundance hexadecimal into a decimal number.
     *
     * DSK outputs the binaries in an odd format that needs to be changed for it to be easier to work with.
     *
     * 0200 0000 0000 0000
     *  ^               ^
     *  |               |
     *  least           most          significant digits
     *
     *
     * @param input
     * @return
     */
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
        return String.valueOf((Long.parseLong(combined,16)));

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
