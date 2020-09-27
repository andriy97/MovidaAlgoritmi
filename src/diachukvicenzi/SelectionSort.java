package diachukvicenzi;

import movida.commons.Movie;

public class SelectionSort {

    public Movie[] sort(Movie[] array, String s) {
        for(int i = 0; i < array.length-1; i++) {
            int minimo = i; //Partiamo dall' i-esimo elemento
            for(int j = i+1; j < array.length; j++) {
                //Qui avviene la selezione, ogni volta
                //che nell' iterazione troviamo un elemento piú piccolo di minimo
                //facciamo puntare minimo all' elemento trovato
                switch (s){
                    case "voti":
                        if(array[minimo].getVotes()>array[j].getVotes()) {
                            minimo = j;
                        }
                        break;
                    case "anno":
                        if(array[minimo].getYear()<array[j].getYear()) {
                            minimo = j;
                        }
                        break;
                }
            }
            //Se minimo e diverso dall' elemento di partenza
            //allora avviene lo scambio
            if(minimo!=i) {
                Movie k = array[minimo];
                array[minimo]= array[i];
                array[i] = k;
            }
        }
        return array;
    }
}
