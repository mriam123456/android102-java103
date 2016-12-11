package mq.uag.apachelogviewer;

import android.provider.BaseColumns;


/**
 * Created by MANUEL RIAM on 07/12/2016.
 *
 * Cette classe défini un "Contrat" avec la base de données , la structure de la classe est importante
 * Notablement:
 *  -La classe est marquée final et ne peut donc pas être héritée
 *  -Le constructeur est déclaré mais marqué privé (pas d'instanciation possible)
 *  -Tous les membres sont statiques
 *  - Le Schéma des tables de BDD est définit par le moyen de classes internes
 *  - Ces classes implémentes BasesColumns (champs _id et _count managés par le système)
 *
 *
 */

public final class LogReaderContract {
    private LogReaderContract(){}

    //Classe interne définissant le contenu d'une table SQLite
    public static class ALEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "logEntry";
        public static final String COLUMN_NAME_HOST  = "host";
        public static final String COLUMN_NAME_REMOTEHOST  = "remote_host";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_HTTPCODE = "http_code";
        public static final String COLUMN_NAME_CONTENTLENGTH = "content_length";
    }

    public static class ALHost implements BaseColumns
    {
        public static final String TABLE_NAME = "host";
        public static final String COLUMN_NAME_HOSTNAME  = "host_name";
        public static final String COLUMN_NAME_HOSTTYPE  = "host_type";
    }

    public static class ALRemoteHost implements BaseColumns

    {
        public static final String TABLE_NAME = "remoteHost";
        public static final String COLUMN_NAME_REMOTEHOSTNAME  = "remote_host_name";
    }

    public static class ALurl implements BaseColumns
    {
        public static final String TABLE_NAME = "url";
        public static final String COLUMN_NAME_URL = "url_name";
    }




    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " DATE";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String NN = " NOT NULL";

    static final String SQL_CREATE_ENTRYDB =
            "CREATE TABLE " + ALEntry.TABLE_NAME + " (" + ALEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ALEntry.COLUMN_NAME_HOST + INTEGER_TYPE + NN + COMMA_SEP +
            ALEntry.COLUMN_NAME_REMOTEHOST+ INTEGER_TYPE + NN + COMMA_SEP +
            ALEntry.COLUMN_NAME_DATE + DATE_TYPE + NN + COMMA_SEP +
            ALEntry.COLUMN_NAME_URL + INTEGER_TYPE + NN +COMMA_SEP +
            ALEntry.COLUMN_NAME_HTTPCODE + INTEGER_TYPE + NN + COMMA_SEP +
            ALEntry.COLUMN_NAME_CONTENTLENGTH + INTEGER_TYPE + NN + COMMA_SEP + " UNIQUE("+ALEntry.COLUMN_NAME_HOST+","+ALEntry.COLUMN_NAME_REMOTEHOST+","+
                    ALEntry.COLUMN_NAME_DATE+","+ALEntry.COLUMN_NAME_URL+","+ALEntry.COLUMN_NAME_CONTENTLENGTH+") ON CONFLICT IGNORE )";



    static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ALEntry.TABLE_NAME;


    static final String SQL_CREATE_HOSTDB =
            "CREATE TABLE " + ALHost.TABLE_NAME+ " (" + ALHost._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ALHost.COLUMN_NAME_HOSTNAME+ TEXT_TYPE + NN +COMMA_SEP +
                    ALHost.COLUMN_NAME_HOSTTYPE + TEXT_TYPE + NN +COMMA_SEP + " UNIQUE("+ALHost.COLUMN_NAME_HOSTTYPE+") ON CONFLICT IGNORE)";

    static final String SQL_DELETE_HOSTDB = "DROP TABLE IF EXISTS " + ALRemoteHost.TABLE_NAME;

    static final String SQL_CREATE_REMOTEHOST_DB =
            "CREATE TABLE " + ALRemoteHost.TABLE_NAME+ " (" + ALRemoteHost._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ALRemoteHost.COLUMN_NAME_REMOTEHOSTNAME+ TEXT_TYPE + NN + COMMA_SEP +
                    " UNIQUE("+ALRemoteHost.COLUMN_NAME_REMOTEHOSTNAME+") ON CONFLICT IGNORE)";

    static final String SQL_DELETE_REMOTEHOST_DB = "DROP TABLE IF EXISTS " + ALHost.TABLE_NAME;

    static final String SQL_CREATE_URL_DB =
            "CREATE TABLE " + ALurl.TABLE_NAME + " (" + ALurl._ID  +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ALurl.COLUMN_NAME_URL + TEXT_TYPE + NN + COMMA_SEP +" UNIQUE("+ALurl.COLUMN_NAME_URL+") ON CONFLICT IGNORE)";


    static final String SQL_DELETE_URL_DB = "DROP TABLE IF EXISTS " + ALurl.TABLE_NAME;
}
