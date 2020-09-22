package diachukvicenzi;

import movida.commons.Movie;
import movida.commons.Person;

public class QuickSort {
    /// Java program for implementation of QuickSort

    /* This function takes last element as pivot,
       places the pivot element at its correct
       position in sorted array, and places all
       smaller (smaller than pivot) to left of
       pivot and all greater elements to right
       of pivot */
    int partition(Movie[] movie, int low, int high,int func)
    {
        Movie pivot = movie[high];
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {

            switch (func){

                case 0:
                    if (movie[j].getVotes() >= pivot.getVotes()  )
                    {
                        i++;

                        // swap arr[i] and arr[j]
                        Movie temp = movie[i];
                        movie[i] = movie[j];
                        movie[j] = temp;
                    }
                    break;
                case 1:
                    if (movie[j].getYear() >= pivot.getYear()  )
                    {
                        i++;

                        // swap arr[i] and arr[j]
                        Movie temp = movie[i];
                        movie[i] = movie[j];
                        movie[j] = temp;
                    }
                    break;

            }

        }

        // swap arr[i+1] and arr[high] (or pivot)
        Movie temp = movie[i+1];
        movie[i+1] = movie[high];
        movie[high] = temp;

        return i+1;
    }


    /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
    Movie[] sort(Movie[] movie, int low, int high,int func)
    {
        if (low < high)
        {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(movie, low, high,func);

            // Recursively sort elements before
            // partition and after partition
            sort(movie, low, pi-1,func);
            sort(movie, pi+1, high,func);
        }
        return movie;
    }

    int partition(Person[] person, int low, int high,int func)
    {
        Person pivot = person[high];
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {

            switch (func){

                case 0:

                    if (person[j].getFilmCount() >= pivot.getFilmCount()  )
                    {
                        i++;

                        // swap arr[i] and arr[j]
                        Person temp = person[i];
                        person[i] = person[j];
                        person[j] = temp;
                    }
                    break;


            }

        }

        // swap arr[i+1] and arr[high] (or pivot)
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
