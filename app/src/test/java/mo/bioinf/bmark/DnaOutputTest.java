package mo.bioinf.bmark;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class DnaOutputTest {

    @Test
    public void hex2stringtext()
    {
        DnaOutput test = new DnaOutput();


        assertEquals("AAAAATTAATAAAATTGTATTACATTTTCTG",test.binary2dna("9b2a a1b8 0208 0a00",31));
    }

    @Test
    public void transfortest()
    {
        DnaOutput test = new DnaOutput();

        assertEquals("3c02", test.transform("023c"));
    }

    @Test
    public void basetest()
    {
        DnaOutput test = new DnaOutput();

        assertEquals("3300002", test.hex2base4("3c02"));
    }

    @Test
    public void extendtest()
    {
        DnaOutput test = new DnaOutput();

        assertEquals("03300002", test.extend("3300002"));
    }

    @Test
    public void base42dnatest()
    {
        DnaOutput test = new DnaOutput();

        assertEquals("AGGAAAAT", test.base42dna("03300002"));
    }
}