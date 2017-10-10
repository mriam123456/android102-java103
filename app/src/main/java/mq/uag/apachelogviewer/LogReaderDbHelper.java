package mq.uag.apachelogviewer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Manuel RIAM on 07/12/2016.
 */

public class LogReaderDbHelper extends SQLiteOpenHelper {


    /**
     * Le numéro de version de DB permet la gestion du cycle de vie de la BDD
     * L'incrémenter provoquer l'exécution de la fonction onUpgrade
     */
    public static final int DATABASE_VERSION = 18;
    //Nom du fichier de base de donnée
    public static final String LOG_ENTRIES_DB = "LogEntry.db";
    public final Context _c;


    /**
     * Constructeur SQLiteOpenHelper
     * @param context
     */
    public LogReaderDbHelper(Context context)
    {
        super(context,LOG_ENTRIES_DB,null,DATABASE_VERSION);
        //On garde une référence au context (mainActivity)
        _c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Effacement d'éventuelle table (pas sur que ca soit utile ca tient , à tester
        sqLiteDatabase.execSQL(LogReaderContract.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_DELETE_REMOTEHOST_DB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_DELETE_HOSTDB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_DELETE_URL_DB);

        //Création des tables du schéma
        sqLiteDatabase.execSQL(LogReaderContract.SQL_CREATE_HOSTDB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_CREATE_REMOTEHOST_DB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_CREATE_ENTRYDB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_CREATE_URL_DB);

        //Routine d'insertion jeu de donnée de Test
        Scanner s = new Scanner(_c.getResources().openRawResource(R.raw.access_log));
        s.useDelimiter("\\r\\n");

        int i=0;//debug

        try {
            while (s.hasNext()) {
                String rawLine = s.next();
                LogEntry le = LogManager.rawLogLineToEntry(rawLine);
                if (le != null)
                    addLogEntry(le,sqLiteDatabase);
            }
        } finally {
            s.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //Effacement tables (StraightUp xD)
        sqLiteDatabase.execSQL(LogReaderContract.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_DELETE_REMOTEHOST_DB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_DELETE_HOSTDB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_DELETE_URL_DB);


        //Recréation des table
        sqLiteDatabase.execSQL(LogReaderContract.SQL_CREATE_HOSTDB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_CREATE_REMOTEHOST_DB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_CREATE_ENTRYDB);
        sqLiteDatabase.execSQL(LogReaderContract.SQL_CREATE_URL_DB);

        //wtf
        this.onCreate(sqLiteDatabase);
    }

    static final String dateToString(Date date)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return df.format(date);
    }



    public void addLogEntry(LogEntry logEntry , SQLiteDatabase db)
    {
        //Log.d("addLogEntry" , logEntry.toString());

        //NB: La Bdd est managée dans le cadre du cycle de vie de ce Helper et l'ouverture est effectué
        //précedemment à la création des table , il n'y a donc pas de besoin d'ouvrir la connexion dans cette routine
        //qui ne fait qu'ajouter des données de test si le DATABASE_VERSION venait à être incrémenté
        // (ou les données / programme effacés)
        //(pour l'instant)

        //2. Créé un Type ContentValue pour ajouter les valeurs dans la table Host
        ContentValues values = new ContentValues();
        values.put(LogReaderContract.ALHost.COLUMN_NAME_HOSTNAME,"10.10.10.10");//___________Hardcode (Phase Dev)
        values.put(LogReaderContract.ALHost.COLUMN_NAME_HOSTTYPE,"Apache");
        //3. Insertion dans la table Host on récupère l'ID généré
        long hostId = db.insertOrThrow(LogReaderContract.ALHost.TABLE_NAME,null,values);

        //Si l'insertion échoue (Valeur déja existante)
        if(hostId == -1)
        {
            //La valeur existante est récupérée
            hostId = getHostID(logEntry._hostName,db);

        }
        //4. Clear notre structure ContentValues pour l'étape suivante
        values.clear();
        //5.Table Remote_host , même mécanisme
        values.put(LogReaderContract.ALRemoteHost.COLUMN_NAME_REMOTEHOSTNAME,logEntry._remoteHost);
        long remoteHostId = db.insertOrThrow(LogReaderContract.ALRemoteHost.TABLE_NAME,null,values);
        if(remoteHostId == -1)
        {
            remoteHostId = getRemoteHostID(logEntry._remoteHost,db);
        }
        values.clear();
        values.put(LogReaderContract.ALurl.COLUMN_NAME_URL,logEntry._url);
        long urlId = db.insertOrThrow(LogReaderContract.ALurl.TABLE_NAME,null,values);
        if(urlId == -1)
        {
            urlId = getUrlID(logEntry._url,db);
        }
        //6.Table LogEntry, on use des deux ID précédent (modélisation 2nf)
        values.clear();
        values.put(LogReaderContract.ALEntry.COLUMN_NAME_HOST,hostId);
        values.put(LogReaderContract.ALEntry.COLUMN_NAME_REMOTEHOST,remoteHostId);
        values.put(LogReaderContract.ALEntry.COLUMN_NAME_DATE,dateToString(logEntry.entryDate));
        values.put(LogReaderContract.ALEntry.COLUMN_NAME_URL,urlId);
        values.put(LogReaderContract.ALEntry.COLUMN_NAME_HTTPCODE,logEntry._http_code);
        values.put(LogReaderContract.ALEntry.COLUMN_NAME_CONTENTLENGTH,logEntry._content_length);
        long leId = db.insertOrThrow(LogReaderContract.ALEntry.TABLE_NAME,null,values);
        if(leId == -1)
        {
            Log.d("ID rejeté", String.valueOf(leId));
        }
    }

    /**
     * Exercice .1.1
     *
     * Cette fonction est utilisée en cas d'échec d'insertion d'un hôte suite à la violation d'une
     * contrainte d'intégrité référentielle (unicité d'1 seul et meme hôte dans la table Host)
     * -Préconditions:
     *      * Recoit un nom d'hôte enregistré et une référence BDD
     *
     * -PostCondition
     *      * Renvoie l'identifiant primaire correspondant à ce nom d'hôte en BDD
     *
     */
    /*private*/public long getHostID(String hostName, SQLiteDatabase db) {

        Cursor cursor = db.rawQuery("SELECT _id FROM host WHERE host_name='"+hostName+"'",null);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor.getLong(0);
    }

    /**
     * Exercice .1.2
     *
     * Cette fonction est utilisée en cas d'échec d'insertion d'un URL suite à la violation d'une
     * contrainte d'intégrité référentielle (unicité d'1 seule et meme url dans la table URL/!\)
     * -Préconditions:
     *      * Recoit une valeur d'url enregistrée et une référence BDD
     *
     * -PostCondition
     *      * Renvoie l'identifiant primaire correspondant à ce nom d'URL en BDD
     *
     */
    /*private*/public long getUrlID(String uName , SQLiteDatabase db)
    {
        Cursor cursor = db.rawQuery("SELECT _id FROM url WHERE url_name='"+uName+"'",null);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor.getLong(0);
    }

    /**
     * Exercice .1.3
     *
     * Cette fonction est utilisée en cas d'échec d'insertion d'un RemoteHost suite à la violation d'une
     * contrainte d'intégrité référentielle (unicité d'1 seul et même Remote Host dans la table RemoteHost/!\)
     * -Préconditions:
     *      * Recoit une valeur remote_host enregistrée et une référence BDD
     *
     * -PostCondition
     *      * Renvoie l'identifiant primaire correspondant à ce remoteHost en BDD
     *
     */
    /*private*/public long getRemoteHostID(String hName , SQLiteDatabase db)
    {
        Cursor cursor = db.rawQuery("SELECT _id FROM remote_host WHERE remote_host_name='"+hName+"'",null);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor.getLong(0);

    }

    /**
     * cette fonction fonctionnera si les trst des trois fonction Get*Id sont Vert
     *
     * Incrémenter le champ static DATABASE_VERSION afin de provoquer la réinitialisation
     * de la DB avec les requête SQL appropriées implémentées
     *
     * Exercice .1.4
     * Décrire le fonctionnement de cette fonction une fois que vous avez validé son test
     *
     */
    public List<LogEntry> getAllLogEntries()
    {
        List<LogEntry> leList = new ArrayList<>();


        String SQL= "SELECT (SELECT remote_host_name FROM remoteHost as rh WHERE rh._id = le.remote_host) as remote_host , date ," +
                " (SELECT url_name FROM url as ur WHERE ur._id = le.url) as url," +
                " http_code , content_length ,(SELECT host_name FROM host as ho WHERE ho._id = le.host) as host " +
                "FROM logEntry as le ORDER BY date DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(SQL,null);

        //LogEntry le;

        if(cursor.moveToFirst())
        {
            do
            {
                LogEntry le = new LogEntry(
                        cursor.getString(0),//Hôte distant
                        cursor.getString(1), //DateTime + TZ
                        cursor.getString(2), // URI
                        cursor.getString(3), //code http
                        cursor.getString(4), // Longueur contenu (en octets)
                        cursor.getString(5)  //Hôte
                        );
                leList.add(le);

            }while(cursor.moveToNext());
        }

        return leList;
    }

}
