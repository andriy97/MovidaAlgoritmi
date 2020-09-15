package diachukvicenzi;

import movida.commons.*;

import java.io.*;
import java.util.Map;

public class MovidaCore implements IMovidaConfig, IMovidaDB, IMovidaSearch {

//IMovidaConfig

    private SortingAlgorithm algorithm = SortingAlgorithm.QuickSort;
    private MapImplementation structure = MapImplementation.AVL;

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

//IMovidaDB

    @Override
    public void loadFromFile(File f) {

        try {
            FileReader fr = new FileReader(f); // read file
            BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream

            String Titolo,Anno,Regista,Cast,Voti;
            int index;
            String s;



            Titolo=br.readLine();
            index = Titolo.indexOf(':');
            Titolo=Titolo.substring(index + 2, Titolo.length());

            Anno=br.readLine();
            index = Anno.indexOf(':');
            Anno=Anno.substring(index + 2, Anno.length());

            Cast=br.readLine();
            index = Cast.indexOf(':');
            Cast=Cast.substring(index + 2, Cast.length());

            Regista=br.readLine();
            index = Regista.indexOf(':');
            Regista=Regista.substring(index + 2, Regista.length());

            Voti=br.readLine();
            index = Voti.indexOf(':');
            Voti=Voti.substring(index + 2, Voti.length());


            System.out.println(Titolo);
            System.out.println(Anno);
            System.out.println(Cast);
            System.out.println(Regista);
            System.out.println(Voti);






        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void saveToFile(File f) {

    }

    @Override
    public void clear() {

    }

    @Override
    public int countMovies() {
        return 0;
    }

    @Override
    public int countPeople() {
        return 0;
    }

    @Override
    public boolean deleteMovieByTitle(String title) {
        return false;
    }

    @Override
    public Movie getMovieByTitle(String title) {
        return null;
    }

    @Override
    public Person getPersonByName(String name) {
        return null;
    }

    @Override
    public Movie[] getAllMovies() {
        return new Movie[0];
    }

    @Override
    public Person[] getAllPeople() {
        return new Person[0];
    }

    @Override
    public Movie[] searchMoviesByTitle(String title) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesInYear(Integer year) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesDirectedBy(String name) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesStarredBy(String name) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMostVotedMovies(Integer N) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMostRecentMovies(Integer N) {
        return new Movie[0];
    }

    @Override
    public Person[] searchMostActiveActors(Integer N) {
        return new Person[0];
    }
}


//NEL MAIN:

/*
java.io.file
//prima scelgo struttura
//
MovidaCore movida = new MovidaCore();
movida.setMap("una delle strutture in enum");
movida.setSort();

albero=movida.loadfromfile();
//caricare dati

*/