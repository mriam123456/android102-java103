package mq.uag.apachelogviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import java.util.List;

/**
 * Auteur et Enseignement : Manuel RIAM
 *
 * POINT D'ENTREE GLOBAL DE L'APPLICATION
 * https://developer.android.com/reference/android/app/Activity.html
 * ======================================
 * TD - Construction Applicative sous Android Etape 2 (java103 / android 102)
 * ---------------------------------------------------
 * Fonctions expliquée en cours :
 * - View findViewById(int id) - https://developer.android.com/reference/android/app/Activity.html#findViewById(int)
 *      *cast du type de retour
 * - Contexte / passage du contexte et conséquences
 * - Type ContentValues utilisation avec boucle
 * - checkup SQL
 * - Cursor Traversal avec do{}while() et itérator du Cursor (moveFirst()/moveNext()/cursor.get*())
 *
 * =========================
 * Objectifs pédagogique : ||
 * =========================
 * - Découverte API IHM d'Android
 * - Encacapsulation de fonctionnalité (Facilite les portages)
 * - Découverte Design Pattern Adapter
 * - Découverte couche SQLite
 * - Consolidation bases programmation sur collections
 * - Découverte du Cursor
 */
public class MainActivity extends AppCompatActivity {


    //Liste de LogEntry
    private List<LogEntry> _activeViewList ;

    // Liste extensible utilisée comme Frond-End pour l'affichage des Logs
    private ExpandableListView elv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Association du layout activity_main à notre MainActivity
        setContentView(R.layout.activity_main);

        //init couche SQLite
        LogManager.initDb(this);

        //Chargement de la liste de logEntry depuis la BDD
        _activeViewList = LogManager.loadBaseList();

        //Récupère la View correspondant à mainLV et la cast en ExpandableListView dans elv;
        elv = (ExpandableListView) findViewById(R.id.mainLV);

        /**
         * =============
         * Explications :
         * =============
         * -->elv (La listView extensible déclarée en champ de notre activity)
         * est désormais accesible programmatiquement , la ligne de code précédente le permet et
         * démontre le mécanisme générique pour obtenir une référence à un élément défini dans le layout
         * Le cast dénoté par les parenthèse nous permet de spécifier quel type de classe est attendu
         * car findViewById retourne une View(qui est la SuperClasse racine de notre ExpandableListView)
         *
         * -->La ligne suivante (elv.setAdapter(refreshView()) )associe un adapter à notre elv
         *
         * Voir
         * ----
         * https://sourcemaking.com/design_patterns/adapter
         *
         * Plus précisément un SimpleExpandableListAdapter (SELA) https://developer.android.com/reference/android/widget/SimpleExpandableListAdapter.html
         * qui permet de "brancher" un objet instancié avec des structures de données remontées
         * par LogManager ( Exercice 1 & 2 . Implémentation Adapter)
         *
         * Concrètement , cet adapter nous permet d'interfacer notre couche  DB via List<LogEntry> à l'élément mainLV
         * (un ExpandableListView)
         *
         * C'est l'élément présenté à l'utislisateur final
         *
         *
         * Exercice 1. SQLite Basique sous Android
         * ============
         * 1.2.3-> LogReaderDbHelper.get(url|hostremotehost)id() Manipulation basique de curseur sur 3 Méthodes
         *      -*> utlisation boucle do{}while()
         * 4-> Explications fonctionnement get.AllLogEntries()
         *
         *
         * Exercice 2. Exemple d'approvisionnement d'ExpandableViewList via un SimpleExpandableListAdapter
         * ============
         * ->voir refreshView() (retoune un SELA instancié et consommant 2 méthodes de LogManager pour son alimentation)
         * 1->voir List<Map<String,String>> LogManager.getTopLevelListOfMaps(List<LogEntry> lle)
         * 2->voir List<List<Map<String,String>>> LogManager.getChildListOfListOfMaps(List<LogEntry> lle)
         *
         */

        //Associe un adapter (SimpleExpandableListAdapter)
        elv.setAdapter(refreshView());
    }

    /**
     * Fonction utilitaire (Dev // A noter : L'implémentation finale du programme n'utilisera pas cet adapter)
     * @return SimpleExpandableListAdapter (https://newfivefour.com/android-SimpleExpandableListAdapter-example.html)
     */
    private SimpleExpandableListAdapter refreshView()
    {
        //Instanciation et renvoi d'un nouvel SimpleExpandableListAdapter , entièrement paramètré pour
        //le cas de base
        return new SimpleExpandableListAdapter(
                this,//paramètre 1 ._. Context , en l'occurence notre MainActivity
                LogManager.getTopLevelListOfMaps(_activeViewList),//paramètre 2 ._. List de Map . Chaque Map représente une ligne dans la liste <clé,valeur>
                R.layout.parent_item_log_entry,//paramètre 3 ._.  Identifiant pour Layout XML élément parent (android.R. ou R.)
                new String[]{
                        LogReaderContract.ALEntry.COLUMN_NAME_REMOTEHOST , //Nom Clé en DB (voir classe LogReaderContract)
                        LogReaderContract.ALEntry.COLUMN_NAME_CONTENTLENGTH, //Nom Clé en DB (voir classe LogReaderContract)
                        LogReaderContract.ALEntry.COLUMN_NAME_DATE //Nom Clé en DB (voir classe LogReaderContract)
                },//paramètre 4 ._. +++++>>> Tableau de clés(String) qui seront mappées aux identifiants(int) de goupTo <<<+++
                new int[] {
                        R.id.parentTV_RemoteHost, // > Référence élément TextView dans res.layout.parent_item_log_entry
                        R.id.parentTV_cLength, // // > Référence élément TextView dans res.layout.parent_item_log_entry
                        R.id.parentTV_datetime // > Référence élément TextView dans res.layout.parent_item_log_entry
                },//paramètre 5 ._. +++++>>> Tableau d'identifiants d'éléments du layout de l'élément parent <<<+++

                LogManager.getChildListOfListOfMaps(_activeViewList),//paramètre 6 ._.  List de List de Map , le premier niveau de list est mappé aux éléments de la ligne extensible
                R.layout.child_items_log_entry,//paramètre 7 ._. Identifiant pour Layout XML élément enfant(android.R. ou R.)
                new String[]{
                        LogReaderContract.ALEntry.COLUMN_NAME_HOST, //Nom Clé en DB (voir classe LogReaderContract)
                        LogReaderContract.ALEntry.COLUMN_NAME_URL, //Nom Clé en DB (voir classe LogReaderContract)
                        LogReaderContract.ALEntry.COLUMN_NAME_HTTPCODE //Nom Clé en DB (voir classe LogReaderContract)
                },//paramètre 6 ._. +++++>>> Tableau de clés(String) qui seront mappées aux identifiants(int) de childTo <<<+++
                new int[]{
                        R.id.childTV_host, // > Référence élément TextView dans res.layout.child_items_log_entry
                        R.id.childTV_url, // > Référence élément TextView dans res.layout.child_items_log_entry
                        R.id.childTV_code // > Référence élément TextView dans res.layout.child_items_log_entry
                }//paramètre 7 ._. +++++>>> Tableau d'identifiants d'éléments du layout de l'élément enfant <<<+++
        );
    }
}




        /**
         * Une fois tout vos tests au VERT (et fonctionnement normal de la liste comme démontré en cours)
         * ________________________________________
         * ||||||||||||||||||||||||||||||||||||||||
         * vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
         * Exercice Final + compréhension de code
         * !!!!!!Porter attention à la première entrée de la liste dans l'application!!!!!
         *
         * Décommenter la ligne 1. et build , tester l'application
         * puis décommentez également la ligne 2. et build , tester l'application
         *
         * Quelles différences avez vous constaté ?
         * Que pouvez vous en déduire ?
         * Devriez vous recommenter les lignes ? Si oui ou non pourquoi ?
         * ////////////////////////////////////////////////////////////////
         * Réponse
         * --------
         * (Vos réponses ....)
         * ////////////////////////////////////////////////////////////////
         */
                //_activeViewList.remove(0); // Etape .1
                //elv.setAdapter(refreshView()); // Etape .2
        /**
         * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         * ////////////////////////////////////////////////////////////////
         * Fin Exercice Final
         */
//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
////////////////////////[Partie Optionnelle]\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\[DEBUT]\\\\\\

        /** 4 th4 c0d3rZ
         *
         * Vous n'aimez pas les layouts de la liste? moi non plus à vrai dire ^^
         *
         * Prouvez votre maitrise de la couche XML et de l'assignation des données via l'adapter en :
         * - Réarrangeant la distribution des attributs LogEntry dans les noeuds parent / enfant
         * - Expérimentant avec la capacités offerte par la couche XML en terme de customization (voir child_items_log_entry.xml) et values/colors.xml pour un exemmple)
         *
         * Les meilleurs Layouts seront considérés pour la version Finale (en terme de "look&feel/ Choix thème et originalité)"
         *
         */
//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        /** 4 th4 1337 s33k3rz
         *
         * Cette version n'est qu'une étape à but pédagogique,
         * le SELA est l'adaptateur le plus simple et le moins potent pour notre cas d'usage ,
         * l'Adapter final sera intefacé  non pas avec des List et Map , mais par des Curseur sur des
         * requêtes paramétrées par l'utilisateur(via les filtres de l'IHM) vers la base SQL (Famille CursorTree)
         *
         * Être un codeur , c'est suivre le cours + effectuer les exercices
         *
         * Pour devenir programmeur il faut tenter de le précéder , se documenter ... Et tenter des chose de son côté
         *
         * Quelques pistes pour y parvenir
         *
         * -Le SELA paramètre tout l'adapter via le constructeur , mais des adapter plus évolués en terme de capacités de spécialisation
         * (et donc plus haut dans leur hiérarchie de classe) offrent bien plus de fonctions à @override
         *
         * -Penser à comment paramétriser la couche d'interface BDD ce qui définira en bout de course notre capacité à
         *  prendre en charge un ensemble de scénario :
         *      -Importation de logs Auto (Service), depuis différents serveurs enregistrés par l"utilisateur en BDD
         *      -Filter les logs selon différents attributs
         *      -Proposer des vues groupés selon certains attributs
         *      -Trier
         *      -Extraire des statistiques
         *      -prendre en charge d'autre Log que ceux d'apache (Nginx / LightHTTPd / PHP ...)
         *
         *  => Coder quelques requêtes face au 2nf (partiel) de la DB
         *  => Tenter d'implémenter un SimpleCursorTreeAdapter et de l'attribuer à notre ExLV en lieu et place du SELA
         *
         *  4 th4 Profesion4l c0der
         *  => Essayer de conquérir le problème du chargement Asynchrone des Données (essentiel pour infinite scroll)
         *
         */
//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\^^\\\\\\\\\\
////////////////////////[Partie Optionnelle]\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\/||\\\\\\\\\\
//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\[FIN]\\\\\\


