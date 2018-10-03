package mo.bioinf.bmark;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class DnaOutputTest {

    @Test

    public void working_togeher()
    {
        DnaOutput test = null;

        try{
            test = new DnaOutput("ERR2625614", "/media/kaden/AC8883A98883709E/dsk/build/bin/", true);

        }catch(java.io.FileNotFoundException e)
        {
            System.out.println(e.getMessage());
        }


    }

//    @Test
//    public void firstAndLastTest(){
//        ByteReader test = new ByteReader("ERR1539057", "/home/kaden/Documents/dsk/build/bin/");
//
//        String first = test.getNext();
//
//        String last = "";
//        while(test.hasNext())
//        {
//            last = test.getNext();
//            //System.out.println(last);
//        }
//
//        assertEquals("0000", last);
//
//    }

//    @Test
//    public void unNegativeTest()
//    {
//        ByteReader test = new ByteReader();
//
//        StringBuilder tb = new StringBuilder("FFFFFFAB");
//
//        test.unNegative(tb);
//
//        assertEquals("AB",tb.toString());
//    }
//
//
//    @Test
//    public void get_2_bytes_test()
//    {
//        ByteReader test = new ByteReader("ERR1539057", "/home/kaden/Documents/dsk/build/bin/");
//
//        String tb = test.getNext();
//        //tb = test.getNext();
//
//        assertEquals("023c", tb);
//
//    }

//    @Test
//    public void multipleFilesTest()
//    {
//        try{
//            DnaOutput test = new DnaOutput("ERR1539057","/home/kaden/Documents/dnatest/");
//
//        }catch(java.io.FileNotFoundException e)
//        {
//            System.out.println(e.getMessage());
//        }
//    }

//    @Test
//    public void accuracyTest()
//    {
//        System.out.println("hey");
//        File file = new File("/home/kaden/Documents/dsk/build/bin/ERR1539057_gatb/dsk.solid.2");
//        File dsk2ascii_output = new File("/home/kaden/Documents/dsk/build/bin/output.txt");
//
//        Map<String,String> originalMap = new HashMap<>();
//
//        DnaOutput test = null;
//        Scanner original = null;
//        try{
//            test = new DnaOutput(file);
//            original = new Scanner(dsk2ascii_output);
//        }catch(java.io.FileNotFoundException e){
//            System.out.println(e.getMessage());
//        }
//
//        while(original.hasNextLine())
//        {
//            String[] dna_and_abundance = original.nextLine().split(" ");
//            //System.out.println(dna_and_abundance[0]+ " - " + dna_and_abundance[1]);
//            originalMap.put(dna_and_abundance[0].trim(),dna_and_abundance[1].trim());
//            //System.out.println(dna_and_abundance[0].trim());
//
//        }
//        original.close();
//
//
//
//        int count = 0;
//        for(Map.Entry<String,String> entry : test.dna_map.entrySet())
//        {
//            boolean condition = entry.getValue().equals(originalMap.get(entry.getKey()));
//            //ystem.out.println(entry.getKey());
//            assertEquals(true,condition);
//            //System.out.println("DNA: " + entry.getKey() + " - Abundance: " + entry.getValue());
//            count++;
//        }
//
//
//        System.out.println("hey2");
//
//
//    }

//    @Test
//    public void readfiletest()
//    {
//        File file = new File("/home/kaden/Documents/dsk/build/bin/ERR1539057_gatb/allsolids");
//        try{
//
//            DnaOutput test = new DnaOutput(file);
//        }catch (java.io.FileNotFoundException e)
//        {
//            System.out.println(e.getMessage());
//        }
//
//    }
//
//    @Test
//    public void hex2stringtext()
//    {
//        DnaOutput test = new DnaOutput();
//
//        StringBuilder testb = new StringBuilder("9b2a a1b8 0208 0a00");
//        test.binary2dna(testb,31);
//
//        assertEquals("AAAAATTAATAAAATTGTATTACATTTTCTG",testb.toString());
//    }
//
//    @Test
//    public void transfortest()
//    {
//        DnaOutput test = new DnaOutput();
//
//        StringBuilder testb = new StringBuilder("023c");
//        test.transform(testb);
//
//        assertEquals("3c02", testb.toString());
//    }
//
//    @Test
//    public void basetest()
//    {
//        DnaOutput test = new DnaOutput();
//
//        StringBuilder testb = new StringBuilder("3c02");
//        test.hex2base4(testb);
//
//        assertEquals("3300002", testb.toString());
//    }
//
//    @Test
//    public void extendtest()
//    {
//        DnaOutput test = new DnaOutput();
//
//        StringBuilder testb = new StringBuilder("3300002");
//        test.extend(testb,8);
//
//        assertEquals("03300002", testb.toString());
//    }
//
//    @Test
//    public void base42dnatest()
//    {
//        DnaOutput test = new DnaOutput();
//
//        StringBuilder testb = new StringBuilder("03300002");
//        test.base42dna(testb);
//
//
//        assertEquals("AGGAAAAT", testb.toString());
//    }
}