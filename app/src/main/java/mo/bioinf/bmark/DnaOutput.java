package mo.bioinf.bmark;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;


public class DnaOutput {

    final int BUFFER = 1024*100;

  /*** multithreading objects ****/
  BlockingQueue bqueue = new ArrayBlockingQueue(BUFFER);
  boolean reading_done = false;


  /*******************************/

    //private List<String> DNA_sequences = new ArrayList<String>();
    public long line_count = 0;

    private DnaWriter dna_writer = new DnaWriter();




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


        writer.close();


    }

    /**
     * multithreaded version of dna output
     *
     *
     * @param fastq_name
     * @param basepath
     * @throws java.io.FileNotFoundException
     */
    public DnaOutput(String fastq_name, String basepath, boolean multithreaded) throws java.io.FileNotFoundException{
        this.fastq_name = fastq_name;
        this.basepath = basepath;



        DnaWriter writer = new DnaWriter();

        Consumer consumer = new Consumer(this.bqueue,writer);

        Thread consumer_thread = new Thread(consumer);

        consumer_thread.start();

        List<Thread> producer_list = new ArrayList<>();
        for(int i = 0; i < 8; i++)
        {
            Thread newthread = new Thread(new Producer(this.bqueue,i));
            newthread.start();
            producer_list.add(newthread);
        }

        for(int i = 0; i < producer_list.size(); i++)
        {
            try{
                producer_list.get(i).join();
            }catch(java.lang.InterruptedException e)
            {
                System.out.println("join interrupted?");
            }
        }

        while(!this.bqueue.isEmpty()){ //busy wait until queue is empty
            System.out.println("waiting on queue to empty, size: "+ bqueue.size());
        }

        consumer_thread.interrupt();

//        try{
//            consumer_thread.join();
//        }catch(java.lang.InterruptedException e)
//        {
//
//        }


        writer.close();


    }

    public class Producer implements Runnable{

        protected BlockingQueue queue = null;

        private int file_num = 0;

        public Producer(BlockingQueue queue, int file_num) {
            this.queue = queue;
            this.file_num = file_num;
        }

        public void run() {


            ByteReader reader = new ByteReader(fastq_name,basepath,file_num);

            StringBuilder current_dna_read = new StringBuilder("");
            StringBuilder current_abundance_read = new StringBuilder("");
            while(reader.hasNext())
            {
                //System.out.println("Thread " + file_num + " going.");
                do_dna_conversion(current_dna_read,current_abundance_read,reader,this.queue);

            }



        }
    }

    public class Consumer implements Runnable{

        protected BlockingQueue queue = null;
        DnaWriter writer = null;

        public Consumer(BlockingQueue queue, DnaWriter writer) {
            this.queue = queue;
            this.writer = writer;
        }

        public void run() {

            while(!Thread.currentThread().isInterrupted())
            {
                try{
                    writer.write(queue.take());
                }catch(java.lang.InterruptedException e)
                {
                    System.out.println("consumer interrupted?");
                    System.out.println("queue remaining: " + this.queue.size());
                }
            }

            System.out.println("can get here");



        }
    }

    private void do_dna_conversion(StringBuilder current_dna_read, StringBuilder current_abundance_read, ByteReader reader, BlockingQueue queue)
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

        try{
            queue.put(current_dna_read.append(" ").append(current_abundance_read).toString());

        }catch(java.lang.InterruptedException e)
        {
            System.out.println("interrupted?");
        }
        this.line_count++;

        current_abundance_read.setLength(0);
        current_dna_read.setLength(0);
    }


    private class DnaWriter{
        private BufferedWriter writer = null;


        DnaWriter(){
            String path = basepath + fastq_name + "_dna.txt";
            Log.println(Log.INFO, "dna writer path", path);
            File file = new File(path);
            try{
                writer = new BufferedWriter(new FileWriter(path),BUFFER);
            }catch(java.io.IOException e)
            {
                System.out.println("io exception writer" + e.getMessage());
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

        public void write(Object input)
        {
            try{
                writer.write(input.toString() + "\n");
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


        StringBuilder combined = new StringBuilder("");

        for(int i = 0; i < 4; i++)
        {
            combined.append(transformed[i]);
        }

        input.setLength(0);
        input.append(String.valueOf((Long.parseLong(combined.toString(),16))));


    }


    public DnaOutput(){

    }



    public static void binary2dna(StringBuilder input, int kmersize)
    {
        String[] splitStr = input.toString().split(" ");

        StringBuilder[] split = new StringBuilder[4];
        for(int i = 0; i < 4; i++)
        {
            split[i] = new StringBuilder(splitStr[3-i]);
            transform(split[i]);
            hex2base4(split[i]);
            extend(split[i],8);
            base42dna(split[i]);
        }


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

        StringBuilder old = new StringBuilder(input);


        input.setCharAt(0,old.charAt(2));
        input.setCharAt(1,old.charAt(3));
        input.setCharAt(2,old.charAt(0));
        input.setCharAt(3,old.charAt(1));


    }

    public static void hex2base4(StringBuilder input)
    {
        String old = input.toString();
        input.setLength(0);
        input.append(Integer.toString(Integer.parseInt(old,16),4));


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



        for(int i = 0; i < difference; i++)
        {
            input.insert(0,'0');
        }




    }

    public static void base42dna(StringBuilder input)
    {


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
