
package diachukvicenzi;

import movida.commons.Movie;
import movida.commons.Person;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Set;

public class Utils {

        // funzione che prende il testo dopo i ":"
        public String findElement(String riga){

            int index = riga.indexOf(':');
            riga=riga.substring(index + 2, riga.length());
            return riga;

        }

        //funzione che prende la riga del file del cast e prende ogni singolo elemento e lo inserisce in un array di tipo persona
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
                Person persona=new Person(array[i],Att,1);
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

    }


