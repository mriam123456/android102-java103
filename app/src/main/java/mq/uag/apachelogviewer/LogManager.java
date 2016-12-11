package mq.uag.apachelogviewer;


import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Manuel RIAM on 05/12/2016.
 *
 * Classe statique pour opération de transformation / Traitement de logs
 */

public class LogManager {

    static LogReaderDbHelper mDbHelper;


    //region ##########[Fonctions Utilitaires / Pas Touche !!]##########
    /**
     * Préconditions:
     * - Recoit une ligne de texte de Log brut en argument
     * Postconditions:
     * - Doit retourner:
     *      -Un LogEntry instancié si après parsing par expression régulière
     *      -Null en cas de Skip du masque d'expression régulière
     * @param rawLine
     * @return LogEntry
     */

     public  static LogEntry rawLogLineToEntry(String rawLine)
     {


         //Excpression régulière (voir java102 et TP)
         String sPattern = "^([\\w.\\-]+)\\s-\\s-\\s\\[(\\d{2}/\\w{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2}\\s-\\d{4})\\][^/]+([^\\s]+)[^\"]+\"\\s(\\d{3})\\s(-|\\d+)";

         //Si la ligne de la log est validée par l'expression régulière
         if(Pattern.matches(sPattern,rawLine))
         {
             Pattern ptrn = Pattern.compile(sPattern);
             Matcher matcher = ptrn.matcher(rawLine);

             //On extrait les jetons par Regex + insanciation et renvoi d'un LogEntry
             while (matcher.find()){
                 String test = matcher.group(1);
                return new LogEntry(new String[]{
                        matcher.group(1),
                        matcher.group(2),
                        matcher.group(3),
                        matcher.group(4),
                        matcher.group(5)
                },"10.10.10.10");//Test /!\ Hardc0de
             }


         }
         //Sinon on retourne null
        else {return null;}
         return null;
     }


    /**
     * Initialise notre objet concret d'accès à la base de données
     * NB: le cycle de vie de la BDD est pris en charge par le système => voir LogReaderDbHelper
     * @param context
     */
    public static void initDb(Context context)
    {
        //Initialise l'objet concret d'accès à la base de données et lui associe le contexte
        // de l'activity (very bad c0de ^^ , pour le moment...)
        mDbHelper = new LogReaderDbHelper(context);
    }


    /**
     * Invoque la base de données pour charger l'ensemble des entrées enregistrée sous forme de
     * LogEntry
     * @return List<LogEntry>
     */
    public static List<LogEntry> loadBaseList()
    {
        //Tout dans la subtibilité xD
        return mDbHelper.getAllLogEntries();
    }


    //endregion
    /**
     * Execice .2.1
     *
     * Approvisionnement SimpleExpandableListAdapter
     * 2 eme paramètre
     *
     * -Pré-conditions :
     *      * Reçoit une List<LogEntry>
     * -Post-conditions
     *   Une List<Map<String,String>> contenant
     *      * Pour chaque LogEntry de la list en entrée
     *        1 Map<String,String> en sortie
     *        contenant :
     *              Pour chaque élément TextView du layout parent_item_log_entry
     *                   ---------------
     *              % en Clé , le nom d'une clé en DB (Voir MainActivity.refreshView paramètre 3)
     *              % en Valeur , la valeur de cette clé pour le Logentry en cours
     *
     *
     *  1.bis S'assurer que la valeur de la date pour un LogEntry donné est
     *  passée au format Français  ex: 20/11/1981 21:45:00 dans l'affichage final
     *
     * @param lle
     * @return
     */
    public static List<Map<String,String>> getTopLevelListOfMaps(List<LogEntry> lle) {

        //Nom des clés en DB : éléments parents
        //LogReaderContract.ALEntry.COLUMN_NAME_REMOTEHOST
        //LogReaderContract.ALEntry.COLUMN_NAME_CONTENTLENGTH
        //LogReaderContract.ALEntry.COLUMN_NAME_DATE


        return null;
    }


    /**
     * Exercice .2.2
     *
     * Approvisionnement SimpleExpandableListAdapter
     * 5 eme paramètre
     *
     * -Pré-conditions :
     *      * Reçoit une List<LogEntry>
     * -Post-conditions
     *   Une List<List<Map<String,String>>> contenant
     *      * Pour chaque LogEntry de la list en entrée
     *        1 List<Map<String,String>> en sortie
     *        contenant :
     *              Pour chaque élément TextView du layout child_items_log_entry
     *                   ---------------
     *              % en Clé , le nom d'une clé en DB (Voir MainActivity.refreshView paramètre 6 )
     *              % en Valeur , la valeur de cette clé pour le Logentry en cours
     *
     *
     * @param lle
     * @return
     */
    public static List<List<Map<String,String>>> getChildListOfListOfMaps(List<LogEntry> lle)
    {
            //Nom des clé en DB :éléments enfants
            //LogReaderContract.ALEntry.COLUMN_NAME_HOST
            //LogReaderContract.ALEntry.COLUMN_NAME_URL
            //LogReaderContract.ALEntry.COLUMN_NAME_HTTPCODE



        return null;
    }


}
