package diachukvicenzi;

import movida.commons.Movie;
import movida.commons.Person;

public class QuickSort {
    ///////PER MOVIE/////////////


    /*Sceglie l'ultimo elemento come pivot, lo posiziona nella sua corretta posizione
    * e mette tutti gli elementi più piccoli del pivot a sinistra e piu grandi a destra
    *
    * func è l'intero che indica per cosa si vuole ordinare
    * 1: anno
    * 2: voti
    *
    * */

    int partition(Movie[] movie, int low, int high,int func)
    {
        Movie pivot = movie[high];
        int i = (low-1); // indice dell'elemento più piccolo
        for (int j=low; j<high; j++)
        {

            switch (func){

                case 0:
                    if (movie[j].getVotes() >= pivot.getVotes()  )
                    {
                        i++;
                        // scambia arr[i] e arr[j]
                        Movie temp = movie[i];
                        movie[i] = movie[j];
                        movie[j] = temp;
                    }
                    break;
                case 1:
                    if (movie[j].getYear() >= pivot.getYear()  )
                    {
                        i++;
                        // scambia arr[i] e arr[j]
                        Movie temp = movie[i];
                        movie[i] = movie[j];
                        movie[j] = temp;
                    }
                    break;
            }
        }

        // scambia arr[i+1] e arr[high] (o il pivot)
        Movie temp = movie[i+1];
        movie[i+1] = movie[high];
        movie[high] = temp;

        return i+1; //ritorna posizione pivot
    }


    /* Funzione che implementa QuickSort()
      arr[] --> Array da ordinare,
      low  --> indice inizio,
      high  --> indice fine */
    Movie[] sort(Movie[] movie, int low, int high,int func)
    {
        if (low < high)
        {
            /* ordino e ritorno pivot */
            int pi = partition(movie, low, high,func);

            // ricorsivamente ordino elementi prima del pivot e dopo pivot
            sort(movie, low, pi-1,func);
            sort(movie, pi+1, high,func);
        }
        return movie;
    }



    //////PER PERSON///////////


    int partition(Person[] person, int low, int high,int func)
    {
        Person pivot = person[high];
        int i = (low-1);
        for (int j=low; j<high; j++)
        {
            switch (func){
                case 0:
                    if (person[j].getFilmCount() >= pivot.getFilmCount()  )
                    {
                        i++;
                        Person temp = person[i];
                        person[i] = person[j];
                        person[j] = temp;
                    }
                    break;
            }
        }
        Person temp = person[i+1];
        person[i+1] = person[high];
        person[high] = temp;
        return i+1;
    }


    /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
    Person[] sort(Person[] person, int low, int high, int func)
    {
        if (low < high)
        {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(person, low, high,func);

            // Recursively sort elements before
            // partition and after partition
            sort(person, low, pi-1,func);
            sort(person, pi+1, high,func);
        }
        return person;
    }


}
