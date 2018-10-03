package mo.bioinf.bmark;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteReaderTest {

    @Test
    public void getNext() {
        ByteReader test = new ByteReader("ERR1539057", "/home/kaden/Documents/dsk/build/bin/",0);
        while(test.hasNext())
        {
            String next = test.getNext();
            if(next.length() == 0)
                System.out.println("zero length");
            System.out.println(next);
        }
    }
}