package mq.uag.apachelogviewer;



import org.junit.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by DaKi on 05/12/2016.
 */



public class LogScannerBaseTests  {

    String[] testData;
    String errData;

    @Before
    public void setup(){
        testData = new String[]{
                "64.242.88.10 - - [07/Mar/2004:21:07:24 -0800] \"GET /twiki/bin/rdiff/TWiki/AppendixFileSystem?rev1=1.11&rev2=1.10 HTTP/1.1\" 200 40578",
                "64.242.88.10 - - [07/Mar/2004:21:14:32 -0800] \"GET /twiki/bin/rdiff/TWiki/FileAttribute HTTP/1.1\" 200 5846",
                "h24-70-56-49.ca.shawcable.net - - [07/Mar/2004:21:16:17 -0800] \"GET /twiki/view/Main/WebHome HTTP/1.1\" 404 300",
                "h24-70-56-49.ca.shawcable.net - - [07/Mar/2004:21:16:18 -0800] \"GET /favicon.ico HTTP/1.1\" 200 1078"
        };

        errData = "64.242.88.10 -x- [07/Mar|2004:21:07:24 -0800] \"GaT /twiki/bin/rdiff/TWiki/AppendixFileSystem?rev1=1.11&rev2=1.10 HTTP/1.1\" 200 40578";
    }


    @Test
    public  void Test_rawLogLineToEntry_BaseFunc_numIP()
    {

        LogEntry le = LogManager.rawLogLineToEntry(testData[0]);
        assertThat("Ne retourne pas un LogEntry !!",le,instanceOf(LogEntry.class));

        assertEquals("Pb lors du parsing de l'hote (ip)","64.242.88.10",le._remoteHost);

        assertEquals("Pb lors du parsing de l'url","/twiki/bin/rdiff/TWiki/AppendixFileSystem?rev1=1.11&rev2=1.10",le._url);
        assertEquals("Pb lors du parsing du code HTTP (code)",200,le._http_code);
        assertEquals("Pb lors du parsing dde la longueur du contenu servi (code)",40578,le._content_length);
    }

    @Test
    public void Test_rawLogLineToEntry_BaseFunc_hostName()
    {
        LogEntry le = LogManager.rawLogLineToEntry(testData[2]);
        assertThat("Ne retourne pas un LogEntry !!",le,instanceOf(LogEntry.class));

        assertEquals("Pb lors du parsing de l'hote (hostname)","h24-70-56-49.ca.shawcable.net",le._remoteHost);
        assertEquals("Pb lors du parsing de l'url","/twiki/view/Main/WebHome",le._url);
        assertEquals("Pb lors du parsing du code HTTP (code)",404,le._http_code);
        assertEquals("Pb lors du parsing dde la longueur du contenu servi (code)",300,le._content_length);
    }

    @Test

    public void Test_rawLogLineToEntry_BaseFunc_nullOnRegexFail()
    {
        LogEntry le = LogManager.rawLogLineToEntry(errData);
        assertEquals("Ne renvoie pas null sur mauvais match",le,null);
    }

    @Test
    public void Test_rawLogLineToEntry_BaseFunc_DateParsing() {

        Date dtst = new Date();
        try
        {
            dtst = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US).parse("07/Mar/2004:21:07:24 -0800 ");
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
        }

        LogEntry le = LogManager.rawLogLineToEntry(testData[0]);

        assertEquals("Problème concernant la date parsée", dtst ,le.entryDate );
    }

  /*  @Test
    public void Test_rawLogLineToEntry_BaseFunc_ArrayOfLogEntryToArrayOfStrings() {
        List<LogEntry> testList = new ArrayList<>();
        List<String> endTestList ;

        for(String s : testData)
        {
            testList.add(LogManager.rawLogLineToEntry(s));
        }

        endTestList = LogManager.getDisplayList(testList);
        assertThat("Erreur la valeur retournée n'est pas un List<String>",endTestList,instanceOf(List.class));
        assertEquals("Erreur le format attendu n'est pas le bon","Mon Mar 08 01:07:24 BOT 2004 64.242.88.10 200",endTestList.get(0));
        assertEquals("Erreur la taille de la liste n'est pas bonne" , 4 , endTestList.size());


    }*/
}
