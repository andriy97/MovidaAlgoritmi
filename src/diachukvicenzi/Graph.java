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


    public Person[] getDirectCollaborators(Person actor){
        int i=0;
        ArrayList<Collaboration> collaborations=this.grafo.get(actor.getName());//lista di collaborazioni dell'attore
        Person[] collabActors=new Person[collaborations.size()];
        for(Collaboration collab : collaborations){//per ogni collaborazione dell'attore
            collabActors[i]=collab.getActorB(); //inserisco il suo collaboratore
            i++;
        }
        return collabActors;
    }

    public Person[] getTeamOf(Person actor){ //tipo visita in ampienza
        ArrayList<Person> team = new ArrayList<>(); //team dell'attore
        HashSet<String> visited = new HashSet<>(); //attori visitati
        ArrayDeque<Person> queue= new ArrayDeque<>();
        //aggiungo l'attore stesso al team e lo segno come visitato
        visited.add(actor.getName());
        team.add(actor);
        queue.add(actor);

        while (!queue.isEmpty()){ //finché ho qualcosa in queue
            Person queueActor=queue.poll(); //prendo l'attore dalla queue
            for(Collaboration collab: this.grafo.get(queueActor.getName())){ //per ogni collaborazione dell'attore preso dalla queue
                    Person collabActor=collab.getActorB(); //prendo il suo collaboratore
                    if(!visited.contains(collabActor.getName())){//se questo collaboratore non è stato visitato
                       //lo aggiungo a tutte e strutture
                        visited.add(collabActor.getName());
                        team.add(collabActor);
                        queue.add(collabActor);
                    }
            }
        }
        Person[] arrayTeam= new Person[team.size()];
        return team.toArray(arrayTeam);
    }

    //Opposto del problema del Minimum Spanning Tree
    public Collaboration[] maximizeCollaborationsInTheTeam(Person actor){
        HashSet<String> visited=new HashSet<>();//insieme degli attori già marcati
        ArrayList<Collaboration> collabs=new ArrayList<>();//lista delle collaborazioni nel MST
        PriorityQueue<Collaboration> q=new PriorityQueue<Collaboration>(new SortCollaborations());//contiene le collaborazioni da valutare
        for(Collaboration c: this.grafo.get(actor.getName())){//in questo ciclo,inserisco
            q.add(c);                               //le collaborazioni dell'attore iniziale
        }
        while (!q.isEmpty()){
            Collaboration e=q.poll();//prendo il massimo nella coda,la collaborazione con più score
            // la collaborazione può essere vista come un arco (u,v) di costo w(score)
            if(!visited.contains(e.getActorB().getName())){//se l'attore collaborante( nell'arco (u,v) è v!!) non è marcato
                visited.add(e.getActorA().getName());//aggiungo i due attori del team ai visitati(li marco!)
                visited.add(e.getActorB().getName());//nb: actorA sarà semprè già nel mark,non succede niente in questo caso!
                collabs.add(e);//aggiungi la collaborazione a quelle del MST
                for(Collaboration c:this.grafo.get(e.getActorB().getName())){//per ogni arco uscente dell'attore appena marcato(quello collaborante)
                    if(!visited.contains(c.getActorB().getName())){//se l'arco incide su un nodo non ancora marcato
                        q.add(c);
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

    public void creaCollaborazioni(Movie movie){//crea le varie collaborazioni dato un film
        for(Person thisActor: movie.getCast()){ //per ogni attore nel cast
            if (!this.grafo.containsKey(thisActor.getName())){//se l'attore non è ancora stato aggiunto al grafo
                this.grafo.put(thisActor.getName(),new ArrayList<>());//aggiungilo
            }
            for (Person otherActor: movie.getCast()){//per ogni attore del cast del film
                if(!thisActor.getName().equals(otherActor.getName())){//diverso dall'attore in esame
                    ArrayList<Collaboration> collaborations=this.grafo.get(thisActor.getName());//ottieni le collaborazioni dell'attore in esame
                    Collaboration collab= new Collaboration(thisActor,otherActor);//crea la collaborazione tra i due attori
                    if(collaborations.contains(collab)){//se questa collaborazione esiste già
                        int idx=collaborations.indexOf(collab);//prendo il suo indice
                        collaborations.get(idx).addMovie(movie);//aggiungo il movie alla collaborazione esistente
                    }else{
                        collab.addMovie(movie);//inserisci il film alla nuova collaborazione
                        collaborations.add(collab);//inserisci la nuova collaborazione alle collaborazioni
                    }
                }
            }
        }
    }
}
