package mq.uag.apachelogviewer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import static org.hamcrest.CoreMatchers.*;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by DaKi on 11/12/2016.
 */
@RunWith(AndroidJUnit4.class)
public class LogManagerTest {

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
    public void initDb() throws Exception {
        assertNotNull("La BDD ne s'est pas instanciée",LogManager.mDbHelper);
        assertEquals("La base de donnée n'a pas chargé le bon fichier DB", "LogEntry.db",LogManager.mDbHelper.getDatabaseName());
    }

    @Test
    public void Test_loadBaseList() throws Exception {

        /**########>>>>>>Worst Case , contrainte d'unicité inclue la longueur de contenu pour y pallier
         * 	Line 1083: h24-71-236-129.ca.shawcable.net - - [10/Mar/2004:11:45:51 -0800] "GET /mailman/admin/ppwc/gateway HTTP/1.1" 200 0
         *  Line 1084: h24-71-236-129.ca.shawcable.net - - [10/Mar/2004:11:45:51 -0800] "GET /mailman/admin/ppwc/gateway HTTP/1.1" 200 8692
         */

        lle = LogManager.loadBaseList();

        assertEquals("Le nombre d'éléments chargés n'est pas celui attendu",ref_lle.size(),lle.size());
        int i=0;
        for(LogEntry le : lle)
        {
            i++;
            assertEquals("La valeur _hostname du "+i+"eme LogEntry de la List n'est pas la bonne","10.10.10.10",le._hostName);
        }

        assertEquals("La valeur _http_code du 1er LogEntry de la List n'est pas la bonne",200,lle.get(0)._http_code);
        assertEquals("La valeur _contentLength du 1er LogEntry de la List n'est pas la bonne",7368,lle.get(0)._content_length);
        assertEquals("La valeur _remoteHost du 1er LogEntry de la List n'est pas la bonne","d97082.upc-d.chello.nl",lle.get(0)._remoteHost);
        assertEquals("La valeur _url du 1er LogEntry de la List n'est pas la bonne","/SpamAssassin.html",lle.get(0)._url);

    }

    @Test
    public void getTopLevelListOfMaps() throws Exception {


        lle = LogManager.loadBaseList();
        List<Map<String,String>> toplevel = LogManager.getTopLevelListOfMaps(lle);



        assertThat("toplevel n'est pas une List<>",toplevel,isA(List.class));
        assertThat("toplevel.get(0) n'est pas un Map<>",toplevel.get(0),isA(Map.class));
        assertThat("Le type de la valeur toplevel.get(0).get(\"date\") n'est pas celui attendu",toplevel.get(0).get("date"),isA(String.class));

        assertEquals("topLevel n'a pas la taille de la List<LogEntry> en entrée",lle.size(),toplevel.size());

        assertEquals("Le premier HashMap ne correspond pas pour la date","12/03/2004 17:25:45",toplevel.get(0).get("date"));
        assertEquals("Le premier HashMap ne correspond pas pour le remote_host","d97082.upc-d.chello.nl",toplevel.get(0).get("remote_host"));
        assertEquals("Le premier HashMap ne correspond pas pour le content_length","7368",toplevel.get(0).get("content_length"));

        assertEquals("Le 98eme HashMap ne correspond pas pour la date","11/03/2004 20:02:37",toplevel.get(97).get("date"));
        assertEquals("Le 98eme HashMap ne correspond pas pour le remote_host","lj1105.inktomisearch.com",toplevel.get(97).get("remote_host"));
        assertEquals("Le 98eme HashMap ne correspond pas pour le content_length","209",toplevel.get(97).get("content_length"));

    }

    @Test
    public void getChildListOfListOfMaps() throws Exception {

        lle = LogManager.loadBaseList();
        List<List<Map<String,String>>> childlevel = LogManager.getChildListOfListOfMaps(lle);

        assertThat("childlevel n'est pas une List<>",childlevel,isA(List.class));
        assertThat("childlevel.get(0) n'est pas un List<>",childlevel.get(0),isA(List.class));
        assertThat("childlevel.get(0).get(0) n'est pas un Map<>",childlevel.get(0).get(0),isA(Map.class));
        assertThat("childlevel.get(0).get(0).get(\"url\") n'est pas un String",childlevel.get(0).get(0).get("url"),isA(String.class));
        assertEquals("La valeur childlevel.get(4).get(0).get(\"url\") n'est pas celle attendue","/dccstats/stats-spam-ratio.1day.png",childlevel.get(4).get(0).get("url"));
        assertEquals("La valeur childlevel.get(4).get(0).get(\"http_code\") n'est pas celle attendue","200",childlevel.get(4).get(0).get("http_code"));
        assertEquals("Il existe une différence entre deux valeur qui devrais être égale (référence entre les 2 listes) "
            ,lle.get(4)._url,childlevel.get(4).get(0).get("url")
        );
        assertEquals("Il existe une différence entre deux valeur qui devrais être égale (référence entre les 2 listes) "
                ,lle.get(1305)._url,childlevel.get(1305).get(0).get("url"));
        assertEquals("Il existe une différence entre deux valeur qui devrais être égale (référence entre les 2 listes) "
                ,lle.get(1506)._url,childlevel.get(1506).get(0).get("url"));



    }

}