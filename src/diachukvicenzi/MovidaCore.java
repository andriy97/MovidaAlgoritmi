package diachukvicenzi;

import movida.commons.*;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MovidaCore implements IMovidaConfig, IMovidaDB, IMovidaSearch, IMovidaCollaborations {
    private SortingAlgorithm algorithm;
    private MapImplementation structure;
    final private AVL avl;
    final private Btree btree;
    final private QuickSort quickSort;
    final SelectionSort selectionSort;
    private Graph grafo;


    public MovidaCore(){
        algorithm = SortingAlgorithm.QuickSort;
        structure = MapImplementation.BTree;
        avl=new AVL();
        btree=new Btree();
        quickSort=new QuickSort();
        selectionSort=new SelectionSort();
        grafo=new Graph();
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
            Utils utils=new Utils();

            String Titolo,Anno,Voti,s;
            Person[] Cast;
            Person Regista;
            String reg="Regista";

            while ((s = br.readLine()) != null) {

                //trim lo uso per togliere eventuali spazi

                Titolo = utils.findElement(s);
                Anno = utils.findElement(br.readLine()).trim();
                Regista = new Person(utils.findElement(br.readLine()),reg,1);
                Cast = utils.findCast(br.readLine());
                Voti = utils.findElement(br.readLine()).trim();

                Movie movie = new Movie(Titolo, Integer.parseInt(Anno), Integer.parseInt(Voti), Cast, Regista);

                // aggiungo le collaborazioni al grafo
                grafo.extractMovieCollaborations(movie);

                if(AVL()){
                    avl.insert(movie);
                }else{
                    btree.insert(movie);
                }


                // per scorrere la linea vuota
                s=br.readLine();
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
            return btree.size;
        }else{
            return avl.nodecount;
        }

    }

    @Override
    public int countPeople() { //funzia
        if (AVL()){
          return btree.countPeople();
        }else{
            return avl.countPeople();
        }
    }

    @Override
    public boolean deleteMovieByTitle(String title) { //funzia
        if(AVL()){
            return avl.deleteMovieByTitle(title);
        }else{
            return btree.deleteMovieByTitle(title);
        }

    }

    @Override
    public Movie getMovieByTitle(String title) { //funzia
        if(AVL()){
            return btree.getMovieByTitle(title);
        }else{
            return avl.getMovieByTitle(title);
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

            Movie[] movie=getAllMovies();
            Set<Movie> film;
            film = new HashSet<>();
            for (Movie x : movie) {
                if(x.getTitle().contains(title)){
                    film.add(x);
                }
            }
            movie=new Movie[film.size()];
            return film.toArray(movie);
    }

    @Override
    public Movie[] searchMoviesInYear(Integer year) { //funzia
        Movie[] movie=getAllMovies();
        Set<Movie> film;
        film = new HashSet<>();
        for (Movie x : movie) {
            if(x.getYear()==year){
                film.add(x);
            }
        }
        movie=new Movie[film.size()];
        return film.toArray(movie);
    }

    @Override

    public Movie[] searchMoviesDirectedBy(String name) { //Funzia
        Movie[] movie=getAllMovies();
        Set<Movie> film;
        film = new HashSet<>();
        for (Movie x : movie) {
            if(x.getDirector().getName().contains(name)){
                film.add(x);
            }
        }
        movie=new Movie[film.size()];
        return film.toArray(movie);
    }

    @Override
    public Movie[] searchMoviesStarredBy(String name) { //Funzia

            Movie[] movie=getAllMovies();
            Set<Movie> result  = new HashSet<>();
            for (int i = 0; i < movie.length; i++) {
                for (Person p:movie[i].getCast() ) {
                    if(p.getName().contains(name)){
                        result.add(movie[i]);
                    }
                }
            }
            movie=new Movie[result.size()];
            return result.toArray(movie);



    }

    @Override
    public Movie[] searchMostVotedMovies(Integer N) { //dovrebbe funzia
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
    public Movie[] searchMostRecentMovies(Integer N) { //dovrebbe funzia
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
            avl.getPersonSet();
            x = avl.getPersonSet1();
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

        return grafo.getTeam(actor);
    }

    @Override
    public Collaboration[] maximizeCollaborationsInTheTeamOf(Person actor){

        return grafo.maximizeCollaborationsInTheTeam(actor);
    }


}




