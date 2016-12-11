package mq.uag.apachelogviewer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DaKi on 05/12/2016.
 */

public class LogEntry {

    /*private*/ String _remoteHost;
                String _hostName;
   /*private*/ String _url;
    /*private*/ int _http_code;
    /*private*/ int _content_length;
    public Date entryDate;

    /**
     * Constructeur de la classe LogEntry
     * -Préconditions : Recoit un tableau contenant les groupes extraits par expressions régulières
     * -Postconditions : Initialise entièrement l'object y compris la propriété  Date entrydate
     * @param parsedLine
     */
    public LogEntry(String[] parsedLine , String host){
        _remoteHost = parsedLine[0];


        try {
            entryDate = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z",Locale.US).parse(parsedLine[1]);
        }catch (ParseException e)
        {
            e.printStackTrace();
        }

        _url = parsedLine[2];
        _http_code = Integer.parseInt(parsedLine[3]);
        if(parsedLine[4].contains("-") || _http_code == 304)
            _content_length = 0;
        else
            _content_length = Integer.parseInt(parsedLine[4]);
        _hostName = host;


    }

    public  LogEntry(String remote , String date , String url , String http , String cLength, String host )
    {
        _remoteHost = remote;

        try {
            entryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).parse(date);
        }catch (ParseException e)
        {
            e.printStackTrace();
        }
        _hostName = host;
        _url = url;
        _http_code = Integer.parseInt(http);
        _content_length = Integer.parseInt(cLength);

    }

}
