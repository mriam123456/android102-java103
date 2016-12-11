package mq.uag.apachelogviewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by DaKi on 11/12/2016.
 */
@RunWith(AndroidJUnit4.class)
public class LogReaderDbHelperTest {

    Context ctx;
    List<LogEntry> lle;
    List<LogEntry> ref_lle = new ArrayList<>();

    @Before
    public void setup() throws Exception
    {
        ctx = InstrumentationRegistry.getTargetContext();
        LogManager.initDb(ctx);


        Scanner s = new Scanner(ctx.getResources().openRawResource(R.raw.access_log));
        s.useDelimiter("\\r\\n");
        try {
            while (s.hasNext()) {
                String rawLine = s.next();
                LogEntry le = LogManager.rawLogLineToEntry(rawLine);
                if(le != null)
                    ref_lle.add(le);
            }
        } finally {
            s.close();
        }

    }
    @Test
    public void getHostID() throws Exception {

        SQLiteDatabase db =LogManager.mDbHelper.getReadableDatabase();
        long test = LogManager.mDbHelper.getHostID("10.10.10.10",db);
        assertEquals("Ce test devrait retourner la référence associée a 10.10.10.10 soit 1",1,test);
        db.close();
    }

    @Test
    public void getUrlID() throws Exception {
        SQLiteDatabase db =LogManager.mDbHelper.getReadableDatabase();
        long test = LogManager.mDbHelper.getUrlID("/twiki/bin/statistics/Main",db);
        assertEquals("Ce test devrait retourner la référence associée a /twiki/bin/statistics/Main soit 38",38,test);

        long test2 = LogManager.mDbHelper.getUrlID("/twiki/bin/rdiff/Main/WebChanges",db);
        assertEquals("Ce test devrait retourner la référence associée a /twiki/bin/rdiff/Main/WebChanges soit 85",85,test2);
        db.close();
        //
    }

    @Test
    public void getRemoteHostID() throws Exception {
        SQLiteDatabase db =LogManager.mDbHelper.getReadableDatabase();
        long test = LogManager.mDbHelper.getRemoteHostID("64.242.88.10",db);
        assertEquals("Ce test devrait retourner la référence associée a 64.242.88.10 soit 38",1,test);

        long test2 = LogManager.mDbHelper.getRemoteHostID("dsl-80-43-113-44.access.uk.tiscali.com",db);
        assertEquals("Ce test devrait retourner la référence associée a dsl-80-43-113-44.access.uk.tiscali.com soit 197",197,test2);
        db.close();
    }

    @Test
    public void getAllLogEntries() throws Exception {
        List<LogEntry> lle = LogManager.mDbHelper.getAllLogEntries();
        assertEquals(1546,lle.size());
        assertEquals(3241,lle.get(99)._content_length);
        assertEquals(200,lle.get(99)._http_code);
        assertEquals("/dccstats/stats-spam.1day.png",lle.get(99)._url);
        assertEquals("10.0.0.153",lle.get(99)._remoteHost);


    }

}