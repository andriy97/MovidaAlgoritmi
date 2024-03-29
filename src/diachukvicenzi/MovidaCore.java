package diachukvicenzi;

import movida.commons.*;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class MovidaCore implements IMovidaConfig, IMovidaDB, IMovidaSearch, IMovidaCollaborations {
    private SortingAlgorithm algorithm;
    private MapImplementation structure;
    final private AVL avl;
    final private Btree btree;
    final private QuickSort quickSort;
    final SelectionSort selectionSort;
    private Graph grafo;
    private Utils utils;


    public MovidaCore(){
        algorithm = SortingAlgorithm.QuickSort;
        structure = MapImplementation.BTree;
        avl=new AVL();
        btree=new Btree();
        quickSort=new QuickSort();
        selectionSort=new SelectionSort();
        grafo=new Graph();
        utils=new Utils();
    }
//IMovidaConfig



    public boolean mySort (SortingAlgorithm a) { //ritorna 1 se è tra quei due, 0 altrimenti
        return a == SortingAlgorithm.QuickSort || a == SortingAlgorithm.SelectionSort;
    }
    public boolean myMap (MapImplementation m){
        return m == MapImplementation.AVL || m == MapImplementation.BTree;
    }

    @Override
    public boolean setSort(SortingAlgorithm a) {
        SortingAlgorithm old = algorithm;
        if(mySort(a)) { //se è tra i miei algoritmi lo cambio altrimenti non faccio nulla
            algorithm = a;
        }
        return algorithm != old; // 1 se è cambiato
    }

    @Override
    public boolean setMap(MapImplementation m) {
        MapImplementation old = structure;
        if(myMap(m)){
            structure=m;
        }
        return structure != old;
    }

    private boolean AVL() {//ritorna 1 se la struttura in uso è AVL
        return structure == MapImplementation.AVL;
    }
    private boolean SelSort() {//ritorna 1 se l'algoritmo in uso è Selection Sort
        return algorithm == SortingAlgorithm.SelectionSort;
    }

//IMovidaDB

    @Override
    public void loadFromFile(File f) {

        try {
            FileReader fr = new FileReader(f); // read file
            BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
            utils =new Utils();

            String Titolo,Anno,Voti,s;
            Person[] Cast;
            Person Regista;
            Set<Movie> movieSet=new HashSet<>();
            while ((s = br.readLine()) != null) {

                //trim lo uso per togliere eventuali spazi

                Titolo = utils.findElement(s);
                Anno = utils.findElement(br.readLine()).trim();
                Regista = utils.findRegista(br.readLine());
                Cast = utils.fillhashMap(br.readLine());
                Voti = utils.findElement(br.readLine()).trim();
                s = br.readLine();

                //creo il movie e lo aggiungo al set
                Movie movie = new Movie(Titolo, Integer.parseInt(Anno), Integer.parseInt(Voti), Cast, Regista);
                movieSet.add(movie);

            }

            for(Movie m:movieSet){
                //prendo l'hashmap di tutti gli attori
                HashMap x=utils.getAttoriGiaInseriti();

                for(Person p: m.getCast()){
                    //numero di film fatti da un attore
                    p.setFilmCount((Integer) x.get(p.getName()));
                }
                m.getDirector().setFilmCount((Integer) x.get(m.getDirector().getName()));
            }

            for (Movie m:movieSet) {
                grafo.creaCollaborazioni(m);

                    if(AVL()){
                        avl.insert(m);
                    }else{
                        btree.insert(m);
                    }
            }


        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        {

        }
    }
    @Override
    public void saveToFile(File f) { //fare getAllMovies e togliere if else

            Utils.saveFile(f, getAllMovies());

    }

    @Override
    public void clear() { //funzia
        if (AVL()){
            avl.makeEmpty();
        }else{
            btree.clear();
        }



    }

    @Override
    public int countMovies() { //funzia
        if(AVL()){
            return avl.nodecount;
        }else{
            return btree.size;
        }

    }

    @Override
    public int countPeople() { //funzia
        if (AVL()){
          return avl.countPeople();
        }else{
            return btree.countPeople();
        }
    }

    @Override
    public boolean deleteMovieByTitle(String title) {
        boolean Deleted;
        if(AVL()){
            Deleted=avl.deleteMovieByTitle(title);
            if(Deleted){
                //prende il grafo di questa istanza e lo cambia con quello con le collaborazioni aggiornate
                grafo.setGrafo(avl.returnNewGraph());
                return true;
            }else return false;

        }else{
            Deleted=btree.deleteMovieByTitle(title);
            if(Deleted){
                //prende il grafo di questa istanza e lo cambia con quello con le collaborazioni aggiornate
                grafo.setGrafo(btree.returnNewGraph());
                return true;
            }else return false;
        }

    }


    @Override
    public Movie getMovieByTitle(String title) { //funzia
        if(AVL()){
            return avl.getMovieByTitle(title);
        }else{
            return btree.getMovieByTitle(title);
        }
    }

    @Override
    public Person getPersonByName(String name) { //funzia
        if(AVL()){
            return avl.getPersonByName(name);
        }else{
            return btree.getPersonByName(name);
        }

    }

    @Override
    public Movie[] getAllMovies() { //funzia
        if(AVL()){
           return Utils.toArrayMovie(avl.getMovieSet());
        }else{
            return btree.getAllMovies();
        }

    }

    @Override
    public Person[] getAllPeople() { //funzia
        if(AVL()){
            return Utils.toArrayPerson(avl.getPersonSet());
        }else{
            return btree.getAllPeople();
        }
    }


    @Override
    public Movie[] searchMoviesByTitle(String title) { //Funzia
        if(AVL()){
             return Utils.toArrayMovie(avl.searchMoviesByTitle(title));

        }else{
            return btree.searchMovieByTitle(title);

        }
    }


    @Override
    public Movie[] searchMoviesInYear(Integer year) { //funzia
        if(AVL()){
            return Utils.toArrayMovie(avl.getMoviesByYear(year));
        }else{
            return btree.searchMoviesInYear(year);
        }
    }

    @Override

    public Movie[] searchMoviesDirectedBy(String name) { //Funzia
        if(AVL()){
           return Utils.toArrayMovie(avl.searchMoviesDirectedBy(name));
        }else{
            return btree.searchMoviesDirectedBy(name);

        }
    }

    @Override
    public Movie[] searchMoviesStarredBy(String name) { //Funzia
        if(AVL()){
            return Utils.toArrayMovie(avl.searchMoviesStarredBy(name));
        }else{
            return btree.searchMoviesStarredBy(name);
        }





    }

    @Override
    public Movie[] searchMostVotedMovies(Integer N) { //ok
        Movie[] x=getAllMovies();
        Movie[] result;
        int max=x.length;
        if (max<N) {
            N=max;
            result = new Movie[max];
        }else{
            result=new Movie[N];
        }
        if(SelSort()){
            x=selectionSort.sort(x, "voti");
        }else {
            x = quickSort.sort(x, 0, max - 1, 0);
        }
        for (int i = 0; i < N; i++) {
            result[i]=x[i];
        }
        return  result;

        
    }



    @Override
    public Movie[] searchMostRecentMovies(Integer N) { //ok
        Movie[] x=getAllMovies();
        Movie[] result;
        int max=x.length;
        if (max<N) {
            N=max;
            result = new Movie[max];
        }else{
            result=new Movie[N];
        }
        if(SelSort()){
            x=selectionSort.sort(x, "anno");
        }else {
            x = quickSort.sort(x, 0, max - 1, 1);
        }
        for (int i = 0; i < N; i++) {
            result[i]=x[i];
        }
        return  result;

    }

    @Override
    public Person[] searchMostActiveActors(Integer N) { //crea getallactors e togli if


        Person[] x;
        if(AVL()){
            x = avl.getAllActors();
        }else{
            x=btree.getAllActors();
        }
        Person[] result;
        int max = x.length;
        if (max < N) {
            N = max;
            result = new Person[max];
        } else result = new Person[N];

        if (SelSort()) {
            x = selectionSort.sort(x);
        } else {
            x = quickSort.sort(x, 0, max - 1, 0);
        }
        for (int i = 0; i < N; i++) {
            result[i] = x[i];
        }
        return result;


    }


    //ImovidaCollaborations
    @Override
    public Person[] getDirectCollaboratorsOf(Person actor){

        return grafo.getDirectCollaborators(actor);
    }

    @Override
    public Person[] getTeamOf(Person actor){

        return grafo.getTeamOf(actor);
    }

    @Override
    public Collaboration[] maximizeCollaborationsInTheTeamOf(Person actor){

        return grafo.maximizeCollaborationsInTheTeam(actor);
    }


}




