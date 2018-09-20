package mo.bioinf.bmark;

import android.content.SyncStatusObserver;
import android.os.Parcelable;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;
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
import java.util.Arrays;


public class DnaOutput {

    //private List<String> DNA_sequences = new ArrayList<String>();

    public long line_count = 0;

    private DnaWriter dna_writer = new DnaWriter();

    private List<File> solids = new ArrayList<>();


//    private List<StringBuilder> dna_strings = new ArrayList<>();
//    private List <StringBuilder> abundance_strings = new ArrayList<>();


    //private Map<String, String> dna_map = new HashMap<>();

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

        DnaWriter writer = new DnaWriter();

        ByteReader reader = new ByteReader(fastq_name,basepath);

        StringBuilder current_dna_read = new StringBuilder("");
        StringBuilder current_abundance_read = new StringBuilder("");
        while(reader.hasNext())
        {
            for(int i = 0; i < 4; i++)
            {
                current_dna_read.append(reader.getNext());
                if(i!=3)
                {
                    current_dna_read.append(" ");
                }
            }

            binary2dna(current_dna_read,31);



            for(int i = 0; i < 4; i++)
            {
                current_abundance_read.append(reader.getNext());
                if(i!=3)
                {
                    current_abundance_read.append(" ");
                }
            }

            transformed_hex_to_dec(current_abundance_read);

            writer.write(current_dna_read,current_abundance_read);
            this.line_count++;

            current_abundance_read.setLength(0);
            current_dna_read.setLength(0);

        }

        writer.close();


    }






    /**
     * writes the dna_map file into a txt file.
     */
//    public void write_to_file()
//    {
//        String path = this.basepath + this.fastq_name + "_dna.txt";
//
//        File file = new File(path);
//
//        try{
//
//            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
//
//            for(Map.Entry<String,String> entry : this.dna_map.entrySet())
//            {
//                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
//                //System.out.println("wrote");
//            }
//
//            writer.close();
//
//        }catch(java.io.IOException e)
//        {
//            System.out.println(e.getMessage());
//        }
//
//    }

    private class DnaWriter{
        private BufferedWriter writer;


        DnaWriter(){
            String path = basepath + fastq_name + "_dna.txt";

            File file = new File(path);
            try{
                writer = new BufferedWriter(new FileWriter(path));
            }catch(java.io.IOException e)
            {
                System.out.println("error" + e.getMessage());
            }

        }

        public void write(StringBuilder dna, StringBuilder abundance)
        {
            try{
                writer.write(dna + " " + abundance + "\n");
            }catch(java.io.IOException e){
                System.out.println("write error: " + e.getMessage());
            }

        }

        public void close(){
            try{
                writer.close();
            }catch(java.io.IOException e)
            {
                System.out.println("close error " + e.getMessage());
            }

        }

    }


//    //public Map<String, String> getDna_map() {
//        return dna_map;
//    }


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
        //input.setLength(0);
        return (input.substring(6,input.length()));
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
        List<String> ans = new ArrayList<>(input.size()/2);
        StringBuilder currentStr = new StringBuilder("");
        for(int i = 1; i <= input.size(); i++)
        {
            //currentStr += input.get(i-1);
            currentStr.append(input.get(i-1));
            if(i%2 == 0)
            {
                ans.add(currentStr.toString());
                //currentStr = "";
                currentStr.setLength(0); // clear string builder


                
            }
        }

//        Log.println(Log.INFO, "input size", String.valueOf(input.size()));
//        Log.println(Log.INFO, "output size", String.valueOf(ans.size()));

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

        //List<String> ans = new ArrayList<>();
        List<String> ans = new ArrayList<>(input.length);

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

//        Log.println(Log.INFO, "byte size", String.valueOf(input.length));
//        Log.println(Log.INFO, "list size", String.valueOf(ans.size()));


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

//        DnaWriter dna_writer = null;
//        try{
//            dna_writer = new DnaWriter();
//        }catch(java.io.IOException e)
//        {
//            System.out.println("error: " + e.getMessage());
//        }

        StringBuilder currentDnaHex = new StringBuilder("");
        StringBuilder currentAbundanceHex = new StringBuilder("");
        //Log.println(Log.INFO, "input", input.get(0));
        for(int i = 1; i <= input.size(); i++)
        {
            int place_in_line = i%8;

            if(place_in_line <= 4 && place_in_line != 0)
            {
                //currentDnaHex += input.get(i-1) + " ";
                currentDnaHex.append(input.get(i-1) + " ");



            }else{
                //currentAbundanceHex += input.get(i-1) + " ";
                currentAbundanceHex.append(input.get(i-1) + " ");
            }

            if(place_in_line == 0) // finish a line
            {
               //this.hex_map.put(currentDnaHex.toString(),currentAbundanceHex.toString());

//                Log.println(Log.INFO, "pairing ", "DNA: " + currentDnaHex);
//                Log.println(Log.INFO, "pairing ", "ABUNDANCE: " + currentAbundanceHex);

                binary2dna(currentDnaHex,31);
                transformed_hex_to_dec(currentAbundanceHex);

                dna_writer.write(currentDnaHex,currentAbundanceHex);

//                this.dna_strings.add(new StringBuilder(currentDnaHex));
//                this.abundance_strings.add(new StringBuilder(currentAbundanceHex));


                //currentDnaHex = "";
                currentDnaHex.setLength(0);
                currentAbundanceHex.setLength(0); // clear the buffers
                //currentAbundanceHex = "";
            }


        }

        //dna_writer.close();


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
    private void transformed_hex_to_dec(StringBuilder input)
    {
        String[] split = input.toString().split(" ");

        StringBuilder[] transformed = new StringBuilder[4];

        for(int i = 0; i < 4; i++)
        {
            transformed[i] = new StringBuilder(split[3-i]);
            transform(transformed[i]);
        }

        //String[] transformed = {transform(split[3]), transform(split[2]),transform(split[1]),transform(split[0])};

        StringBuilder combined = new StringBuilder("");

        for(int i = 0; i < 4; i++)
        {
            combined.append(transformed[i]);
        }
        //System.out.println(combined);
        input.setLength(0);
        input.append(String.valueOf((Long.parseLong(combined.toString(),16))));
        //return String.valueOf((Long.parseLong(combined.toString(),16)));

    }


    public DnaOutput(){

    }



    public static void binary2dna(StringBuilder input, int kmersize)
    {
        String[] splitStr = input.toString().split(" ");

//        if(splitStr.length != 4)
//        {
//            Log.println(Log.INFO, "binary2dna", "out: " + input.toString());
//        }

        StringBuilder[] split = new StringBuilder[4];
        for(int i = 0; i < 4; i++)
        {
            split[i] = new StringBuilder(splitStr[3-i]);
            transform(split[i]);
            hex2base4(split[i]);
            extend(split[i],8);
            base42dna(split[i]);
        }





//        String[] hex = {transform(split[3]), transform(split[2]), transform(split[1]), transform(split[0])};
//
//        String[] base4 = {hex2base4(hex[0]),hex2base4(hex[1]),hex2base4(hex[2]),hex2base4(hex[3])};
//
//        String[] extended = {extend(base4[0],8),extend(base4[1],8),extend(base4[2],8),extend(base4[3],8)};
//
//        String[] DNA = {base42dna(extended[0]),base42dna(extended[1]),base42dna(extended[2]),base42dna(extended[3])};

        String ans = "";
        for(int i = 0; i < 4; i++)
        {
            ans += split[i].toString();
        }

        if(ans.length() > kmersize)
        {
            int difference = ans.length() - kmersize;
            ans = ans.substring(difference,ans.length());
        }

        input.setLength(0);
        input.append(ans);
        //return ans;
    }

    public static void transform(StringBuilder input)
    {
//        char[] inputChars = input.toCharArray();
//        char[] old = input.toCharArray();
        StringBuilder old = new StringBuilder(input);


        input.setCharAt(0,old.charAt(2));
        input.setCharAt(1,old.charAt(3));
        input.setCharAt(2,old.charAt(0));
        input.setCharAt(3,old.charAt(1));


//        inputChars[0] = old[2];
//        inputChars[1] = old[3];
//        inputChars[2] = old[0];
//        inputChars[3] = old[1];

        //return input;

    }

    public static void hex2base4(StringBuilder input)
    {
        String old = input.toString();
        input.setLength(0);
        input.append(Integer.toString(Integer.parseInt(old,16),4));
        //return Integer.toString(Integer.parseInt(input,16),4);

    }

    public static String extend(String input, int goalSize){
        int length = input.length();

        int difference = goalSize - length;

        String original = input.toString();

        StringBuilder substr = new StringBuilder("");
        for(int i = 0; i < difference; i++)
        {
            substr.append("0");
        }

        return substr.append(input).toString();


    }

    public static void extend(StringBuilder input, int goalSize){
        int length = input.length();

        int difference = goalSize - length;

        //String original = input.toString();

        //StringBuilder substr = new StringBuilder("");

        for(int i = 0; i < difference; i++)
        {
            input.insert(0,'0');
        }

        //return substr.append(input).toString();


    }

    public static void base42dna(StringBuilder input)
    {
        //char[] inputChars = input.toCharArray();

//        for(int i = 0; i < input.length(); i++)
//        {
//            char currentChar = inputChars[i];
//
//            if(currentChar == '0')
//                inputChars[i] = 'A';
//            else if(currentChar == '1')
//                inputChars[i] = 'C';
//            else if(currentChar == '2')
//                inputChars[i] = 'T';
//            else if(currentChar == '3')
//                inputChars[i] = 'G';
//            else
//                return "error";
//        }

        for(int i = 0; i < input.length(); i++)
        {
            char currentChar = input.charAt(i);

            if(currentChar == '0')
                input.setCharAt(i,'A');
            else if(currentChar == '1')
                input.setCharAt(i,'C');
            else if(currentChar == '2')
                input.setCharAt(i,'T');
            else if(currentChar == '3')
                input.setCharAt(i,'G');


        }

        //return String.valueOf(inputChars);
    }


}
