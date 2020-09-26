package diachukvicenzi;

import movida.commons.Movie;
import movida.commons.Person;

public class main {

    public static void main(String[] args){
        Btree btree=new Btree();
        Person persona = new Person("Filippo", "attore", 3);
        Person persona2 = new Person("Filip", "attore", 3);
        Person[] persone= new Person[2];
        persone[0]=persona;
        persone[1]=persona2;

        Movie movie1 =new Movie("babbo", 3, 32, persone, persona2);
        Movie movie2 = new Movie ("birra", 4, 3332, persone, persona);
        Movie movie3 = new Movie ("oijsf", 2, 433, persone, persona);
        Movie movie4 = new Movie ("fagiano", 7, 34, persone, persona);
        Movie movie5 = new Movie ("cano", 8, 35, persone, persona);
        Movie movie6 = new Movie ("brodo", 8, 35, persone, persona);
        Movie movie7 = new Movie ("nano", 8, 35, persone, persona);
        btree.insert(movie1);
        btree.insert(movie2);
        btree.insert(movie3);
        btree.insert(movie4);
        btree.insert(movie5);
        btree.insert(movie6);
        btree.insert(movie7);
        //stampa i film nell'albero
        btree.traverse();
        //cerca nodo in cui si trova il film
        Btree.BTreeNode node=btree.searchKey(movie1);
        System.out.println(node.keys[1].getTitle());
        //mette tutti i film presenti nella struttura in un array
        Movie[] list=btree.getAllMovies();
        for (int i=0; i<7;i++){
            System.out.println(list[i]);
        }
        //cerca il film per titolo
       // Movie quello=btree.getMovieByTitle("1");
        //System.out.println(quello.getTitle());
        //stampa il primo film del primo nodo figlio
        //System.out.println(btree.root.C[0].keys[0]);


        //btree.deleteMovieByTitle("4");

        //btree.traverse();

        Movie[] film =btree.searchMoviesStarredBy("Filippo");
        for(Movie movie: film){
            System.out.println(movie.getTitle());
        }



    }
}
