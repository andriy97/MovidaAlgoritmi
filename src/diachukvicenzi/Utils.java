
package diachukvicenzi;

import movida.commons.Collaboration;
import movida.commons.Movie;
import movida.commons.Person;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Utils {

    HashMap<String, Integer> AttoriGiaInseriti;

    public HashMap<String, Integer> getAttoriGiaInseriti() {
        return AttoriGiaInseriti;
    }

    public Utils(){
        this.AttoriGiaInseriti=new HashMap<>();
    }

    // funzione che prende il testo dopo i ":"
    public String findElement(String riga){

        int index = riga.indexOf(':');
        riga=riga.substring(index + 2, riga.length());
        return riga.trim();

    }

    //funzione che prende la riga del file del cast e prende ogni singolo elemento e lo inserisce in un array di tipo persona
    public Person[] fillhashMap(String riga){

        String Att="Attore";
        int index;
        index = riga.indexOf(':');
        riga=riga.substring(index + 2);
        String[] array = riga.split(",");
        Person[] cast = new Person[array.length];

        for (int i = 0; i < array.length; i++) {

            index = array[i].indexOf(',');
            array[i]=array[i].substring(index+1);
            if(AttoriGiaInseriti.containsKey(array[i].trim())){

                AttoriGiaInseriti.get(array[i].trim());
                AttoriGiaInseriti.put(array[i].trim(),AttoriGiaInseriti.get(array[i].trim())+1);

            }else{
                AttoriGiaInseriti.put(array[i].trim(),1);
            }
            Person persona=new Person(array[i].trim(),Att,AttoriGiaInseriti.get(array[i].trim()));
            cast[i]=persona;

        }
        return cast;

    }


    public Person findRegista(String riga){
        int index = riga.indexOf(':');
        riga=riga.substring(index + 2, riga.length());

        if(AttoriGiaInseriti.containsKey(riga.trim())){

            AttoriGiaInseriti.get(riga.trim());
            AttoriGiaInseriti.put(riga.trim(),AttoriGiaInseriti.get(riga.trim())+1);

        }else{
            AttoriGiaInseriti.put(riga.trim(),1);
        }

        Person regista=new Person(riga.trim(),"Regista",AttoriGiaInseriti.get(riga.trim()));

        return regista;
    }


    public Person[] findCast(String riga){

        String Att="Attore";
        int index;
        index = riga.indexOf(':');
        riga=riga.substring(index + 2);
        String[] array = riga.split(",");
        Person[] cast = new Person[array.length];

        for (int i = 0; i < array.length; i++) {

            index = array[i].indexOf(',');
            array[i]=array[i].substring(index+1);
            Person persona=new Person(array[i].trim(),Att,AttoriGiaInseriti.get(array[i].trim()));


            cast[i]=persona;

        }
        return cast;

    }

    public static void saveFile(File f, Movie[] movies) {



        try {
            FileWriter fw = new FileWriter(f.getName(),false);
            PrintWriter out = new PrintWriter(fw);
            for (Movie movie: movies) {
                //System.out.println(""+movie.getTitle());
                out.println("Title: "+movie.getTitle());
                out.println("Year: "+movie.getYear()  );
                out.println("Director: "+movie.getDirector().getName());
                out.println("Cast: "+toString(movie.getCast()));
                out.println("Votes: "+movie.getVotes() );
                out.println("");
            }
            out.close();

            //System.out.println(""+out);


        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String toString(Person[] person){
        String result="";
        for (Person p:person) {
            result+=p.getName()+" ,";
        }

        return result.substring(0, result.length() - 1);
    }


    public static Movie[] toArrayMovie (Set<Movie> set){
        Movie[] film=new Movie[set.size()];
        set.toArray(film);
        return film;
    }

    public static Person[] toArrayPerson (Set<Person> set){
        Person[] film=new Person[set.size()];
        set.toArray(film);
        return film;
    }
}


