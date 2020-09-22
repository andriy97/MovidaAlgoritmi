
package diachukvicenzi;

import movida.commons.Person;

import java.lang.reflect.Array;

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


    }


