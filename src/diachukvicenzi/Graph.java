package diachukvicenzi;

import movida.commons.Collaboration;
import movida.commons.Movie;
import movida.commons.Person;

import java.util.*;

public class Graph {

    private HashMap<String, ArrayList<Collaboration>> grafo;

    Graph(){
        this.grafo=new HashMap<>();
    }

    public HashMap<String, ArrayList<Collaboration>> getGrafo() {
        return grafo;
    }

    public void setGrafo(HashMap<String, ArrayList<Collaboration>> grafo) {
        this.grafo = grafo;
    }

    public Person[] getDirectCollaborators(Person actor){
        Person[] collabActors = new Person[0];
        if(!grafo.isEmpty()) {
            int i = 0;
            ArrayList<Collaboration> collaborations = this.grafo.get(actor.getName());//lista di collaborazioni dell'attore
            collabActors = new Person[collaborations.size()];
            for (Collaboration collab : collaborations) {//per ogni collaborazione dell'attore
                collabActors[i] = collab.getActorB(); //inserisco il suo collaboratore
                i++;
            }
        }
        return collabActors;
    }

    public Person[] getTeamOf(Person actor){ //tipo visita in ampienza
        Person[] arrayTeam = new Person[0];

        if(!grafo.isEmpty()) {
            ArrayList<Person> team = new ArrayList<>(); //team dell'attore
            HashSet<String> visited = new HashSet<>(); //attori visitati
            ArrayDeque<Person> queue = new ArrayDeque<>();
            //aggiungo l'attore stesso al team e lo segno come visitato
            visited.add(actor.getName());
            team.add(actor);
            queue.add(actor);

            while (!queue.isEmpty()) { //finché ho qualcosa in queue
                Person queueActor = queue.poll(); //prendo l'attore dalla queue
                for (Collaboration collab : this.grafo.get(queueActor.getName())) { //per ogni collaborazione dell'attore preso dalla queue
                    Person collabActor = collab.getActorB(); //prendo il suo collaboratore
                    if (!visited.contains(collabActor.getName())) {//se questo collaboratore non è stato visitato
                        //lo aggiungo a tutte e strutture
                        visited.add(collabActor.getName());
                        team.add(collabActor);
                        queue.add(collabActor);
                    }
                }
            }
            arrayTeam = new Person[team.size()];

            return team.toArray(arrayTeam);
        }
        else return arrayTeam;
    }

    //Max spanning tree
    public Collaboration[] maximizeCollaborationsInTheTeam(Person actor){

        ArrayList<Collaboration> collabs = new ArrayList<>();

        if(!grafo.isEmpty()) {
            //insieme degli attori già passati
            HashSet<String> visited = new HashSet<>();
            //lista delle collaborazioni

            //lista con le collaborazioni da contollare
            PriorityQueue<Collaboration> queue = new PriorityQueue<Collaboration>(new SortCollaborations());
            //le collaborazioni dell'attore iniziale
            queue.addAll(this.grafo.get(actor.getName()));
            while (!queue.isEmpty()) {
                //prendo il massimo nella coda,la collaborazione con più score
                Collaboration e = queue.poll();
                // la collaborazione è un arco (attore A,Attore B) di costo score
                if (!visited.contains(e.getActorB().getName())) {//se la collaborazioen non è gia stata valutata
                    //aggiungo i due attori del team nella lista di colalborazioni gia valutate
                    visited.add(e.getActorA().getName());
                    visited.add(e.getActorB().getName());
                    //e aggiungo la collaborazione
                    collabs.add(e);
                    //per ogni arco uscente dall'attore
                    for (Collaboration c : this.grafo.get(e.getActorB().getName())) {//per ogni arco uscente dell'attore appena marcato(quello collaborante)
                        //controllo se non è stato visitato in quel caso lo aggiungo alla lista di collaborazioni da controllare
                        if (!visited.contains(c.getActorB().getName())) {
                            queue.add(c);
                        }
                    }
                }
            }
        }
        return collabs.toArray(new Collaboration[0]);
    }




    //questa classe interna,mi dovrebbe tenere ordinati, nella priorityQueue, le collaborazioni
    //in ordine DECRESCENTE in base allo score della collaborazione
    class SortCollaborations implements Comparator<Collaboration> {
        public int compare(Collaboration a, Collaboration b) {
            return b.getScore().compareTo(a.getScore());
        }
    }

    //crea le colalborazioni
    public void creaCollaborazioni(Movie movie){
        //per tutti gli attori di un film
        for(Person thisActor: movie.getCast()){
            //se l'attore non è ancora stato aggiunto aggiungie la collaborazione "blank"
            if (!this.grafo.containsKey(thisActor.getName())){
                this.grafo.put(thisActor.getName(),new ArrayList<>());
            }
            //per ogni attore del cast del film
            for (Person otherActor: movie.getCast()){
                //diverso dall'attore in esame
                if(!thisActor.getName().equals(otherActor.getName())){
                    //prende la colalborazione dell'attore in esame
                    ArrayList<Collaboration> collaborations=this.grafo.get(thisActor.getName());
                    //crea la collaborazione
                    Collaboration collab= new Collaboration(thisActor,otherActor);
                    //se questa collaborazione esiste già
                    if(collaborations.contains(collab)){
                        //la trovo e aggiungo i film alla collaborazione nel campo movie
                        int idx=collaborations.indexOf(collab);
                        collaborations.get(idx).addMovie(movie);
                    }else{
                        //se non esiste aggiungo il film alla collaborazione e inserisco la collaborazione
                        collab.addMovie(movie);
                        collaborations.add(collab);
                    }
                }
            }
        }
    }
}
