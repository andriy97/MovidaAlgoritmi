package diachukvicenzi;

import movida.commons.Collaboration;
import movida.commons.Person;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {


        private HashMap<Person, ArrayList<Collaboration>> grafo;

        Graph(){
            this.grafo=new HashMap<>();
        }

    public Person[] getDirectCollaborators(Person actor){
        ArrayList<Collaboration> collaborazioni=this.grafo.get(actor);//ottieni la lista di collaborazioni dell'attore
        Person[] persona=new Person[collaborazioni.size()];//crea un array di ugual grandezza
        int i=0;
        for(Collaboration collaborazione : collaborazioni){//lo riempio con i nomi degli attore COLLABORANTI
            persona[i]=collaborazione.getActorB();
            i++;
        }
        return persona;
    }
}
