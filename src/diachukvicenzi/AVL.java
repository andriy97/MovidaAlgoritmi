package diachukvicenzi;

import movida.commons.Movie;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import movida.commons.Person;
public class AVL {

    public static class AVLNode {
        //fattore di bilanciamento
        int bf;
        Movie m;
        int height;
        AVLNode left;
        AVLNode right;

        public AVLNode(Movie m) {
            this.m = m;
        }

        public AVLNode getLeft() {
            return left;
        }

        public AVLNode getRight() {
            return right;
        }

        public String getTitle() {
            return this.m.getTitle();
        }
    }

    public AVLNode root;
    public int nodecount = 0;

    // get height
    public int height_tree() {
        return height_tree(root);
    }

    public int height_tree(AVLNode N) {
        if (root == null) {
            return 0;
        } else {
            return root.height;
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return nodecount;
    }

    // make empty
    public void makeEmpty() {
        root.right = null;
        root.left = null;
        root = null;
        nodecount = 0;
    }

    public boolean contains(Movie m) {
        return contains(root, m);
    }

    public boolean contains(AVLNode node, Movie m) {
        if (node == null) {
            return false;
        }

        //fa una ricerca in base al titolo se il titolo è piu corto il risultato sara' a destra altrimenti a sinistra
        int compare = m.getTitle().compareTo(node.getTitle());

        //fa una ricerca ricorsiva e ritorna true se è presente
        if (compare < 0) {
            return contains(node.left, m);
        }
        if (compare > 0) {
            return contains(node.right, m);
        }

        return true;
    }

    public AVLNode balance(AVLNode node) {

        // left heavy subtree
        if (node.bf == -2) {

            if (node.left.bf <= 0) {
                return leftLeftCase(node);
            } else {
                return leftRightCase(node);
            }

        } else if (node.bf == 2) {

            if (node.right.bf >= 0) {
                return rightRightCase(node);
            } else {
                return rightLeftCase(node);
            }

        }

        return node;
    }

    //rotazioni per bilanciare la struttura
    public AVLNode leftLeftCase(AVLNode node) {
        return rightRotation(node);
    }

    public AVLNode leftRightCase(AVLNode node) {
        node.left = leftRotation(node.left);
        return leftLeftCase(node);
    }

    public AVLNode rightRightCase(AVLNode node) {
        return leftRotation(node);
    }

    public AVLNode rightLeftCase(AVLNode node) {
        node.right = rightRotation(node.right);
        return rightRightCase(node);
    }

    public AVLNode leftRotation(AVLNode node) {
        AVLNode newparent = node.right;
        node.right = newparent.left;
        newparent.left = node;

        update(node);
        update(newparent);
        return newparent;

    }

    public AVLNode rightRotation(AVLNode node) {
        AVLNode newparent = node.left;
        node.left = newparent.right;
        newparent.right = node;

        update(node);
        update(newparent);
        return newparent;

    }

    //aggiorna la profondita e l'altezza dei nodi dopo la rimozione/inserimento dei nodi
    public void update(AVLNode node) {
        //se i nodi figli del nodo passato sono nulli allora la loro
        // l'altezza è -1 altrimenti prende la loro salvata nella struttura
        int leftNodeHeight = (node.left == null) ? -1 : node.left.height;
        int rightNodeHeight = (node.right == null) ? -1 : node.right.height;

        //aggiorna la loro altezza
        node.height = Math.max(leftNodeHeight, rightNodeHeight) + 1;
        //aggiorno il bilanciamento
        node.bf = rightNodeHeight - leftNodeHeight;
    }

    //inserisce un movie nella struttura
    public boolean insert(Movie m) {
        //se movie e vuoto ritorna false
        if (m == null || m.getTitle() == null) {
            return false;
        }
        //altrimenti inserisce nella struttura
        if (!contains(root, m)) {
            root = insert(root, m);
            nodecount++;
            return true;
        }
        return false;
    }

    AVLNode insert(AVLNode node, Movie m) {

        if (node == null) {
            node = new AVLNode(m);
            return node;
        }

        int compare = m.getTitle().compareTo(node.getTitle());

        //inserisce in base alla lunghezza del titolo se piu corto va nei nodi di sinistra
        if (compare < 0) {

            node.left = insert(node.left, m);
        } else {
            node.right = insert(node.right, m);
        }

        update(node);
        return balance(node);
    }

    public boolean remove(Movie m) {
        if (m == null || m.getTitle() == null) {
            return false;
        }
        if (contains(root, m)) {
            root = remove(root, m);
            nodecount--;
            return true;
        }
        return false;
    }


    //rimuove il nodo
    public AVLNode remove(AVLNode node, Movie m) {
        if (node == null || node.getTitle() == null) {
            return null;
        }

        int compare = m.getTitle().compareTo(node.getTitle());

        //ricerca il nodo per la lunghezz del nome
        if (compare < 0) {

            node.left = remove(node.left, m);
        } else if (compare > 0) {
            node.right = remove(node.right, m);
        }

        //quando arrivo al nodo corrente elimino i suoi figli e poi lui stesso
        else {
            // only right subtree o no subtree
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                // remove from left subtree
                if (node.left.height > node.right.height) {
                    Movie successorMovie = findMax(node.left);
                    node.m = successorMovie;

                    node.left = remove(node.left, successorMovie);

                } else {
                    Movie successorMovie = findMin(node.right);
                    node.m = successorMovie;

                    node.right = remove(node.right, successorMovie);
                }
            }

        }
        update(node);
        return balance(node);

    }

    //il film piu a sinistra
    public Movie findMin(AVLNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node.m;
    }

    //il film piu a destra
    public Movie findMax(AVLNode node) {
        while (node.right != null) {
            node = node.right;
        }
        return node.m;
    }



    // conta i le persone
    public int countPeople() {
        return getPersonSet().size();
    }

    //set di attori di tipo persona presenti nell'albero(non ci sono duplicati perchè è un set)
    Set<String> nomiPersoneAggiunte;
    Set<Person> actor;
    //funzione che ritorna il set completo di attori
    public Set<Person> getPersonSet() {
        nomiPersoneAggiunte = new HashSet<>();
        actor = new HashSet<>();
        getPersonSet(root);
        return actor;
    }

    //ricorsivamente controlla tutti i nodi per inserire tutti gli attori di un film
    public void getPersonSet(AVLNode r) {
        if (r != null) {
            EsaminaNodoActor(r);
            getPersonSet(r.left);
            getPersonSet(r.right);
        }
    }

    int sommaCast = 0;

    //inserisce gli attori del cast nel set attori
    public void EsaminaNodoActor(AVLNode n) {
        Person[] cast = n.m.getCast();
        sommaCast = sommaCast + cast.length;
        //se nella lista di persone gia inserite non c'è il suo nome aggiungilo
        if (!nomiPersoneAggiunte.contains(n.m.getDirector().getName())) {
            actor.add(n.m.getDirector());
            nomiPersoneAggiunte.add(n.m.getDirector().getName());
        }

        //per ogni persona se non è aggiunta alla lista di persone gia inserite la aggiunge alla lista da stampare
        for (int i = 0; i < cast.length; i++) {
            if (!nomiPersoneAggiunte.contains(n.m.getCast()[i].getName())) {
                actor.add(cast[i]);
                nomiPersoneAggiunte.add(n.m.getCast()[i].getName());
            }
        }
    }

    public Person[] getAllActors() {
        Set<Person> Persone = getPersonSet();
        Set<Person> Attori = new HashSet<>();

        for (Person p : Persone) {

            if (p.getRole().equals("Attore")) {
                Attori.add(p);
            }

        }

        Person[] attori = new Person[Attori.size()];
        Attori.toArray(attori);
        return attori;
    }


///////////////// ritorna un set di film

    Set<Movie> Titles;

    public Set<Movie> getMovieSet() {
        Titles = new HashSet<>();
        getMovieSet(root);
        return Titles;
    }

    public void getMovieSet(AVLNode r) {
        if (r != null) {
            EsaminaNodoTitles(r);
            getMovieSet(r.left);
            getMovieSet(r.right);
        }
    }

    public void EsaminaNodoTitles(AVLNode n) {
        if (n != null) {
            Titles.add(n.m);
        }

    }
///////////////// ritorna un set di film di un determinato anno
    //controlla il campo anno di ogni film per vedere se è uscito nell'anno richiesto e lo aggiunge ad una lista

    Set<Movie> FilmAnno;

    public Set<Movie> getMoviesByYear(Integer Anno) {
        FilmAnno = new HashSet<>();
        getMovieYearSet(root, Anno);
        return FilmAnno;
    }

    public void getMovieYearSet(AVLNode r, Integer Anno) {
        if (r != null) {
            EsaminaNodoYear(r, Anno);
            getMovieYearSet(r.left, Anno);
            getMovieYearSet(r.right, Anno);
        }
    }

    public void EsaminaNodoYear(AVLNode n, Integer Anno) {

        if (Anno - n.m.getYear() == 0) {

            FilmAnno.add(n.m);
        }

    }


///////////////// ritorna un set di film di un determinato Regista

    Set<Movie> FilmDirector;

    public Set<Movie> searchMoviesDirectedBy(String Regista) {
        FilmDirector = new HashSet<>();
        searchMoviesDirectedBy(root, Regista);
        return FilmDirector;
    }

    public void searchMoviesDirectedBy(AVLNode r, String Regista) {
        if (r != null) {
            EsaminaNodoDirector(r, Regista);
            searchMoviesDirectedBy(r.left, Regista);
            searchMoviesDirectedBy(r.right, Regista);
        }
    }

    public void EsaminaNodoDirector(AVLNode n, String Regista) {

        if (n.m.getDirector().getName().trim().compareTo(Regista.trim()) == 0) {

            FilmDirector.add(n.m);
        }
    }
/////////////////////////////////////////////////////////////////////////////////
//funzione per trovare i film che contengono la stringa inserita

    Set<Movie> movieTitle;
    public Set<Movie> searchMoviesByTitle(String title) {
        movieTitle = new HashSet<>();
        searchMoviesByTitle(root, title);
        return movieTitle;
    }


    public void searchMoviesByTitle(AVLNode r, String title) {
        if (r != null) {
            EsaminaTitoloFilm(r, title);
            searchMoviesByTitle(r.left, title);
            searchMoviesByTitle(r.right, title);
        }
    }

    public void EsaminaTitoloFilm(AVLNode n, String title) {

        if (n.m.getTitle().contains(title)) {
            movieTitle.add(n.m);
        }
    }

    //per trovare ogni film in cui ha recitato un attore

    Set<Movie> MovieStarred;
    public Set<Movie> searchMoviesStarredBy(String name) {
        MovieStarred = new HashSet<>();
        searchMoviesStarredBy(root, name);
        return MovieStarred;
    }


    public void searchMoviesStarredBy(AVLNode r, String name) {
        if (r != null) {
            EsaminaAttoriFilm(r, name);
            searchMoviesStarredBy(r.left, name);
            searchMoviesStarredBy(r.right, name);
        }
    }

    public void EsaminaAttoriFilm(AVLNode n, String name) {

        for(Person p:n.m.getCast())
        {
            if(p.getName().equals(name)){
                MovieStarred.add(n.m);
            }
        }
    }

    //funzione per trovare il nodo con il titolo esatto
    public Movie getMovieByTitle(String Title) {
        return getMovieByTitle(root, Title);
    }

    public Movie getMovieByTitle(AVLNode node, String Title) {
        if (node == null) {
            return null;
        }

        int compare = (Title.trim()).compareTo(node.m.getTitle().trim());
        if (compare < 0) {
            return getMovieByTitle(node.left, Title);
        }
        if (compare > 0) {
            return getMovieByTitle(node.right, Title);
        } else {
            return node.m;
        }

    }


    public Person getPersonByName(String name) {
        for (Person p : getPersonSet()) {
            if (p.getName().trim().compareTo(name.trim()) == 0) {
                return p;
            }
        }
        return null;
    }

    public boolean deleteMovieByTitle(String Title) {

        if (getMovieByTitle(Title) != null) {
            Movie movieRemoved = getMovieByTitle(root, Title);

            boolean removed = remove(getMovieByTitle(root, movieRemoved.getTitle()));
            if (removed) {
                updateCollaborazioni();
                findDeletedActors(root, movieRemoved);
                return true;
            } else return false;

        } else return false;
    }


    void findDeletedActors(AVLNode node, Movie DeletedMovie) {
        if (node != null) {
            UpdateAfterDelete(node, DeletedMovie);
            findDeletedActors(node.left, DeletedMovie);
            findDeletedActors(node.right, DeletedMovie);
        }
    }

    void UpdateAfterDelete(AVLNode nodo, Movie DeletedMovie) {

        for (Person p : nodo.m.getCast()) {
            for (Person r : DeletedMovie.getCast()) {
                if (r.getName().equals(p.getName())) {
                    p.setFilmCount(p.getFilmCount() - 1);
                }
            }
        }
        if (nodo.m.getDirector().equals(DeletedMovie.getDirector())) {
            nodo.m.getDirector().setFilmCount(nodo.m.getDirector().getFilmCount() - 1);
        }

    }

    Graph graph=new Graph();
    void updateCollaborazioni() {

        Set<Movie> m =getMovieSet();
        for(Movie x:m) {
            graph.creaCollaborazioni(x);
        }

    }

    public HashMap returnNewGraph(){
        return graph.getGrafo();
    }
}