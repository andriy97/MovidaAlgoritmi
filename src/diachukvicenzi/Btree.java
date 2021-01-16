package diachukvicenzi;
import com.sun.deploy.util.ArrayUtil;
import movida.commons.Movie;
import movida.commons.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Btree {
    public BTreeNode root; // puntatore al nodo radice
    public int t; // grado minimo, ogni nodo ha max 2t-1 chiavi dentro (nel nostro caso 5) e max 2t figli (6)
    int size = 0; // numero movie presenti

    // Costruttore
    Btree() {
        this.root = null;
        this.t = 3; //il grado sarà sempre 3
    }

    // visita albero
    public void traverse() {
        if (this.root != null)
            this.root.traverse();
        System.out.println();
    }

    //inserisci movie k in posizione adeguata
    void insert(Movie k) {
        size++;
        if(root == null) { //se l'albero è vuoto inserisco il movie come prima chiave del nodo root
            root = new BTreeNode(t,true);
            root.keys[0] = k;
            root.n = 1; //attuale numero delle chiavi presenti nel nodo
        }else {
            if(root.n == 2*t-1) { //il caso in cui il nodo sia pieno devo creare un nuovo nodo
                BTreeNode s = new BTreeNode(t,false); //creo nuovo nodo
                s.C[0] = root; //metto root come figlio del nodo che ho creato
                s.splitChild(0,root); //faccio lo split del nodo root
                int i=0;
                if(s.keys[0].compareTo(k) < 1) { //se la prima chiave del nodo s è "più piccola" o uguale a k significa che k deve andare nel figlio destro
                    i++;
                }
                s.C[i].insertNonFull(k); //inserisco k nel figlio destro o sinistro a seconda di dove deve andare
                root = s; //faccio diventare s il nuovo nodo radice
            }else { //il caso in cui il nodo abbia ancora dello spazio inserisco k nella posizione giusta
                root.insertNonFull(k);
            }
        }
    }

    void remove(Movie k) {
        size--;
        if(root == null) { //se il nodo radice è null non ho nulla da eliminare
            System.out.println("Albero vuoto");
            return;
        }
        root.removeFromNode(k); //elimino k dal nodo
        if(root.n == 0) { //se il numero di chiavi nel diventa 0 devo eliminare il nodo e aggiustare la struttura dell'albero
            if(root.leaf) //se era un nodo foglia semplicemente elimino il nodo quindi l'albero diventa vuoto
                root = null;
            else //se aveva dei figli faccio diventare root il primo figlio
                root = root.C[0];
        }
        return;
    }

    // cerca chiave nell'albero
    public BTreeNode searchKey(Movie k) { //la chiave è il movie in questione
        if (this.root == null) { //se è vuoto ritorno null
            return null;
        }
        else
            return this.root.searchFromThisNode(k); //altrimenti chiamo la funzione del BTreeNode root per cercare da lì in poi
    }

    // A BTree node
    class BTreeNode {
        Movie[] keys; // array di chiavi
        int t; // grado minimo
        BTreeNode[] C; // array di figli
        int n; // attuale numero di chiavi
        boolean leaf; // vero se il nodo è una foglia, falso altrimenti

        // Costruttore
        BTreeNode(int t, boolean leaf) {
            this.t = t;
            this.leaf = leaf;
            this.keys = new Movie[2 * t - 1];
            this.C = new BTreeNode[2 * t];
            this.n = 0;
        }
        //scorre l'indice delle chiavi del nodo finché non arriva alla posizione che dovrebbe contenere la chiave da eliminare
        public int findKey(Movie k) {
            int idx = 0;
            while (idx<n && keys[idx].compareTo(k) < 0)
                ++idx;
            return idx;
        }

        //elimina movie dal nodo
        public void removeFromNode(Movie k) {
            int idx = findKey(k); //indice chiave da rimuovere
            if(idx < n && keys[idx].equals(k)) { //se k corrisponde alla chiave che si trova in quell'indice
                if(leaf) //se il nodo da cui sto eliminando è una foglia
                    removeFromLeaf(idx);
                else
                    removeFromNonLeaf(idx);
            }else { //significa che movie k non esiste nell'albero oppure si trova in un sottoalbero
                if(leaf) {
                    System.out.println("La chiave "+k+" non esiste");
                    return;
                }
                boolean flag = ((idx==n)? true : false); //true se idx è l'ultima chiave nel nodo
                if(C[idx].n < t) //se il numero di chiavi nel figlio è minore del grado minimo
                    fill(idx); //devo aggiungerci una chiave da uno dei nodi adiacenti o fare merge
                if(flag && idx > n)
                    C[idx - 1].removeFromNode(k);
                else
                    C[idx].removeFromNode(k);
            }
            return;
        }

        public void removeFromLeaf(int idx) {
            //sposta tutte le chiavi dopo idx una posizione indietro
            for (int i=idx+1; i<n; ++i)
                keys[i-1] = keys[i];

            //diminuisci numero chiavi
            n--;

            return;
        }

        public void removeFromNonLeaf(int idx) {
            Movie k = keys[idx];

            //se il figlio che precede k (C[idx]) ha almeno t chiavi, trova il predecessore di k (pred)
            //nel sottoalbero con radice C[idx], mette pred al posto di k e elimina ricorsivamente pred in C[idx]
            if (C[idx].n >= t)
            {
                Movie pred = getPred(idx);
                keys[idx] = pred;
                C[idx].removeFromNode(pred);
            }

            //se il figlio C[idx] ha meno di t chiavi, esamina C[idx+1]. Se ha almeno t chiavi trova il successore (succ)
            //di k nel sottoalbero con radice C[idx+1]. mette succ al posto di k e elimina succ
            else if  (C[idx+1].n >= t)
            {
                Movie succ = getSucc(idx);
                keys[idx] = succ;
                C[idx+1].removeFromNode(succ);
            }

            //se sia C[idx] che C[idx+1] hanno meno di t chiavi si fa il merge tra k e tutti i C[idx+1] formando C[idx]
            //ora C[idx] ha 2t+1 chiavi. Si libera C[idx+1] e si elimina k da C[idx]
            else
            {
                merge(idx);
                C[idx].removeFromNode(k);
            }
            return;
        }

        public Movie getPred(int idx) {
            //mi sposto sul nodo più a destra finché non arrivo alla foglia
            BTreeNode cur = C[idx];
            while (!cur.leaf)
                cur = cur.C[cur.n];

            // ritorno l'ultima chiave della foglia
            return cur.keys[cur.n-1];
        }

        public Movie getSucc(int idx) {
            //mi sposto sul nodo più a sinistraa partire da C[idx+1] finché non arrivo alla foglia
            BTreeNode cur = C[idx+1];
            while (!cur.leaf)
                cur = cur.C[0];

            // ritorno la prima chiave della foglia
            return cur.keys[0];
        }

        public void fill(int idx) {

            //se C[idx-1] ha piu di t-1 chiavi rubo una chiave a lui
            if (idx!=0 && C[idx-1].n >= t)
                borrowFromPrev(idx);

                //se C[idx+1] ha piu di t-1 chiavi rubo una chiave a lui
            else if (idx!=n && C[idx+1].n >= t)
                borrowFromNext(idx);

            //faccio merge di C[idx] coi suoi fratelli, se è l'ultimo lo mergo con il precedente,
                // altrimenti col prossimo
            else
            {
                if (idx != n)
                    merge(idx);
                else
                    merge(idx-1);
            }
            return;
        }

        public void borrowFromPrev(int idx) {
            BTreeNode child = C[idx];
            BTreeNode sibling = C[idx-1];

            //l'ultima chiave di C[idx-1] va al nodo parent e la chiave keys[idx-1] dal nodo parent viene
            //inserita come prima chiave in C[idx]. child guadagna una chiave e sibling ne perde una


            //Sposto tutte le chiavi in C[idx] un posto avanti
            for (int i=child.n-1; i>=0; --i)
                child.keys[i+1] = child.keys[i];

            //se C[idx] non è una foglia allora muovo tutti i suoi figli un posto avanti
            if (!child.leaf)
            {
                for(int i=child.n; i>=0; --i)
                    child.C[i+1] = child.C[i];
            }

            //setto la prima chiave di child uguale a keys[idx-1] del nodo attuale
            child.keys[0] = keys[idx-1];

            //metto l'ultimo figlio di sibling come primo figlio di child
            if(!child.leaf)
                child.C[0] = sibling.C[sibling.n];

            //sposto la chiave dal sibling al suo nodo parent
            keys[idx-1] = sibling.keys[sibling.n-1];

            child.n += 1;
            sibling.n -= 1;

            return;
        }

        public void  borrowFromNext(int idx) {

            BTreeNode child = C[idx];
            BTreeNode sibling = C[idx+1];

            // metto keys[idx] come ultima chiave in C[idx]
            child.keys[(child.n)] = keys[idx];

            // Sibling's first child is inserted as the last child
            // into C[idx]
            //il primo figlio di sibling lo metto come ultimo figlio di C[idx]
            if (!(child.leaf))
                child.C[(child.n)+1] = sibling.C[0];

            //la prima chiave di sibling la metto in keys[idx]
            keys[idx] = sibling.keys[0];

            //sposto tutte le chiavi in sibling una posizione indietro
            for (int i=1; i<sibling.n; ++i)
                sibling.keys[i-1] = sibling.keys[i];

            // sposto i puntatori ai figli una posizione indietro
            if (!sibling.leaf)
            {
                for(int i=1; i<=sibling.n; ++i)
                    sibling.C[i-1] = sibling.C[i];
            }

            // incremento e decremento i numeri delle chiavi in C[idx] e C[idx+1] rispettivamente
            child.n += 1;
            sibling.n -= 1;

            return;
        }

        public void merge(int idx) {
            BTreeNode child = C[idx];
            BTreeNode sibling = C[idx+1];

            //tolgo una chiave dal nodo attuale e la inserisco nella posizione t-1 di C[idx]
            child.keys[t-1] = keys[idx];

            //copio le chiavi da C[idx+1] in C[idx]
            for (int i=0; i<sibling.n; ++i)
                child.keys[i+t] = sibling.keys[i];

            //copio i puntatori ai figli da C[idx+1] a C[idx]
            if (!child.leaf)
            {
                for(int i=0; i<=sibling.n; ++i)
                    child.C[i+t] = sibling.C[i];
            }
            //muovo tutte le chiavi dopo idx del nodo attuale un passo indietro per
            //riempire il buco creatosi dopo aver spostato keys[idx] in C[idx]
            for (int i=idx+1; i<n; ++i)
                keys[i-1] = keys[i];


            //muovo i puntatori ai figli dopo idx+1 del nodo attuale un passo indietro
            for (int i=idx+2; i<=n; ++i)
                C[i-1] = C[i];

            //aggiorno il numero delle chiavi di child e nodo attuale
            child.n += sibling.n+1;
            n--;

            return;
        }

        // visita del sottoalbero a partire dal nodo attuale
        public void traverse() {

            int i = 0;
            for (i = 0; i < this.n; i++) {

                if (this.leaf == false) {
                    C[i].traverse();
                }
                System.out.println(keys[i] + " ");
            }

            if (leaf == false)
                C[i].traverse();
        }


        //cerca una chiave nel sottoalbero a partire da questo nodo
        BTreeNode searchFromThisNode(Movie k) { // ritorna null se k non presente

            // Find the first key greater than or equal to k
            int i = 0;
            while (i < n && k.compareTo(keys[i]) > 0)
                i++;

            // If the found key is equal to k, return this node

            //if(keys[i].equals(k))
            if(i < n && keys[i]!=null && keys[i].getTitle().equalsIgnoreCase(k.getTitle()))
                return this;

            // se la chiave non è in questo nodo ed è un nodo foglia
            if (leaf == true)
                return null;

            // vai al figlio giusto
            return C[i].searchFromThisNode(k);

        }

        void insertNonFull(Movie k) {
            int i = n-1;
            if(leaf == true) {
                while(i>= 0 && keys[i].compareTo(k) > 0) {
                    keys[i+1] = keys[i];
                    i--;
                }
                keys[i+1] = k;
                n = n+1;
            }else {
                while(i >= 0 && keys[i].compareTo(k) > 0)
                    i--;
                if(C[i+1].n == 2*t-1) {
                    splitChild(i+1,C[i+1]);
                    if(keys[i+1].compareTo(k) < 1)
                        i++;
                }
                C[i+1].insertNonFull(k);
            }
        }

        void splitChild(int i, BTreeNode y) {
            BTreeNode z = new BTreeNode(y.t,y.leaf);
            z.n = t-1;
            for(int j=0; j< t-1; j++)
                z.keys[j] = y.keys[j+t];
            if(y.leaf == false) {
                for(int j=0; j<t; j++)
                    z.C[j] = y.C[j+t];

            }
            y.n = t-1;
            for(int j = n; j >= i+1; j--)
                C[j+1] = C[j];
            C[i+1] = z;
            for(int j = n-1; j>= i; j--)
                keys[j+1] = keys[j];
            keys[i] = y.keys[t-1];
            n=n+1;
        }
    }



    public Movie getMovieByTitle(String k) {
        Movie movieToSearch = new Movie(k);
        BTreeNode node = searchKey(movieToSearch);
        if(node!=null) {
            for(int i=0; i < node.n; i++) {
                if(node.keys[i].getTitle().equals(k)) {
                    return node.keys[i];
                }
            }
        }
        return null;
    }

    public Person getPersonByName(String name){
        Movie[] movies=getAllMovies();
        for(Movie movie:movies){
            for(Person person :movie.getCast()){
                if(person.getName().trim().compareTo(name.trim())==0){
                    return person;
                }
            }
        }
        return null;
    }


    public Person[] searchMostActiveActors(Integer N) {
        SelectionSort selectionSort=new SelectionSort();
        Person[] attori=getAllActors();
        Person[] result;
        int max = attori.length;
        if (max < N) {
            N = max;
            result = new Person[max];
        } else result = new Person[N];
        attori = selectionSort.sort(attori);

        for (int i = 0; i < N; i++) {
            result[i] = attori[i];
        }

        return result;
    }


public void aggiornaAttore(String nome, BTreeNode node){
    int i = 0;
    for (i = 0; i < node.n; i++) {

        if (node.leaf == false) {
            aggiornaAttore(nome,node.C[i]);
        }
        for(Person attore: node.keys[i].getCast()) {
            if(attore.getName().equals(nome)){
               attore.setFilmCount(attore.getFilmCount()-1);
            }
        }
    }

    if (node.leaf == false) {
        aggiornaAttore(nome,node.C[i]);
    }
    return;

}
    public boolean deleteMovieByTitle(String k){
        //aggiorno i filmcount del cast
        Movie film=getMovieByTitle(k);
        Person[] attoriDaAggiornare=film.getCast();
        for(Person attore:attoriDaAggiornare){
            aggiornaAttore(attore.getName(), root);
        }
        //elimino il film
        if (film!=null){
            Movie movieToDelete =new Movie(k);
            remove(movieToDelete);
            return true;
        }else{
            return false;
        }
    }

    public int sizeOfTree() {
        return size;
    }

    //scorre l'array finché non arriva alla posizione libera in cui inserire e ritorna quella
    public int getNextPosition(Movie[] listOfMovies) {
        int i;
        for(i = 0; i < listOfMovies.length; i++) {
            if(listOfMovies[i] == null) {
                return i;
            }
        }
        return -1;
    }

    //scorre l'array finché non arriva alla posizione libera in cui inserire e ritorna quella
    public int getNextPositionPerson(Person[] listOfPeople) {
        int i;
        for(i = 0; i < listOfPeople.length; i++) {
            if(listOfPeople[i] == null) {
                return i;
            }
        }
        return -1;
    }



    public int countPeople(){
            return getAllPeople().length;
    }




    public Person[] getAllActors(){
        List<Person> actors= new ArrayList<>();
        getActors(root, actors);
        Person[] arrayActors= new Person[actors.size()];
        actors.toArray(arrayActors);
        return arrayActors;
    }

    public Person[] getAllPeople(){
        List<Person> people= new ArrayList<>();
        getPeople(root, people);
        Person[] arrayPeople= new Person[people.size()];
        people.toArray(arrayPeople);
        return arrayPeople;
    }

    public void clear() {
        Movie[] listOfMovies = getAllMovies();
        for(int i = 0; i<listOfMovies.length; i++) {
            remove(listOfMovies[i]);
        }
        root = null;

    }


    public Movie[] getAllMovies() {
        Movie[] listOfMovies = new Movie[sizeOfTree()];
        getMovies(root,listOfMovies,0);
        return listOfMovies;
    }

    public void getMovies(BTreeNode node,Movie[] listOfMovies,int k) {
        int i = 0;
        for (i = 0; i < node.n; i++) {

            if (node.leaf == false) {
                k = getNextPosition(listOfMovies);
                getMovies(node.C[i],listOfMovies,k);

            }
            k = getNextPosition(listOfMovies);
            listOfMovies[k] = node.keys[i];
            k++;
        }

        if (node.leaf == false) {
            k = getNextPosition(listOfMovies);
            getMovies(node.C[i],listOfMovies,k);
        }

        return;
    }
    List<String> nomiAttoriInseriti=new ArrayList<>();
    public void getActors(BTreeNode node,List<Person> listOfActors) {
        int i = 0;
        for (i = 0; i < node.n; i++) {

            if (node.leaf == false) {
                getActors(node.C[i],listOfActors);
            }
            for(int k=0; k<node.keys[i].getCast().length; k++) {
                if(!nomiAttoriInseriti.contains(node.keys[i].getCast()[k].getName())){
                    listOfActors.add(node.keys[i].getCast()[k]);
                    nomiAttoriInseriti.add(node.keys[i].getCast()[k].getName());
                }
            }
        }

        if (node.leaf == false) {
            getActors(node.C[i],listOfActors);
        }
        return;
    }
    List<String> nomiPersoneInserite=new ArrayList<>();
    public void getPeople(BTreeNode node,List<Person> listOfPeople) {

        int i = 0;
        for (i = 0; i < node.n; i++) {

            if (node.leaf == false) {
                getPeople(node.C[i],listOfPeople);
            }
            for(int k=0; k<node.keys[i].getCast().length; k++) {
                    if(!nomiPersoneInserite.contains(node.keys[i].getCast()[k].getName())){
                        listOfPeople.add(node.keys[i].getCast()[k]);
                        nomiPersoneInserite.add(node.keys[i].getCast()[k].getName());
                    }



            }
            if(!nomiPersoneInserite.contains(node.keys[i].getDirector().getName())){
                listOfPeople.add(node.keys[i].getDirector());
                nomiPersoneInserite.add(node.keys[i].getDirector().getName());
            }

        }

        if (node.leaf == false) {
            getPeople(node.C[i],listOfPeople);
        }
        return;
    }

    public Movie[] searchMovieByTitle(String title){
        List<Movie> moviesList=new ArrayList<>();
        getAllMoviesByTitle(root,moviesList, title);
        Movie[] moviesArray=new Movie[moviesList.size()];
        moviesList.toArray(moviesArray);
        return moviesArray;
    }

    public void getAllMoviesByTitle(BTreeNode node, List<Movie> listOfMovies, String title) {
        int i = 0;
        for (i = 0; i < node.n; i++) {

            if (node.leaf == false) {
                getAllMoviesByTitle(node.C[i],listOfMovies, title);
            }
           if(node.keys[i].getTitle().contains(title)){
                listOfMovies.add(node.keys[i]);
            }
        }

        if (node.leaf == false) {
            getAllMoviesByTitle(node.C[i],listOfMovies, title);
        }
        return;
    }
   public Movie[] searchMoviesInYear(Integer year){
        List<Movie> moviesList=new ArrayList<>();
        getAllMoviesInYear(root,moviesList, year);
        Movie[] moviesArray=new Movie[moviesList.size()];
        moviesList.toArray(moviesArray);
        return moviesArray;
    }

    public void getAllMoviesInYear(BTreeNode node, List<Movie> listOfMovies, Integer year){
        int i = 0;
        for (i = 0; i < node.n; i++) {

            if (node.leaf == false) {
                getAllMoviesInYear(node.C[i],listOfMovies, year);
            }
            if(node.keys[i].getYear().equals(year)){
                listOfMovies.add(node.keys[i]);
            }
        }

        if (node.leaf == false) {
            getAllMoviesInYear(node.C[i],listOfMovies, year);
        }
        return;
    }

   public Movie[] searchMoviesDirectedBy(String name){
       List<Movie> moviesList=new ArrayList<>();
       getAllMoviesDirectedBy(root,moviesList, name);
       Movie[] moviesArray=new Movie[moviesList.size()];
       moviesList.toArray(moviesArray);
       return moviesArray;
   }
   public void getAllMoviesDirectedBy(BTreeNode node, List<Movie> listOfMovies, String name){
       int i = 0;
       for (i = 0; i < node.n; i++) {

           if (node.leaf == false) {
               getAllMoviesDirectedBy(node.C[i],listOfMovies, name);
           }
           if(node.keys[i].getDirector().getName().equals(name)){
               listOfMovies.add(node.keys[i]);
           }
       }

       if (node.leaf == false) {
           getAllMoviesDirectedBy(node.C[i],listOfMovies, name);
       }
       return;
   }

    public Movie[] searchMoviesStarredBy(String name){
        List<Movie> moviesList=new ArrayList<>();
        getAllMoviesStarredBy(root,moviesList, name);
        Movie[] moviesArray=new Movie[moviesList.size()];
        moviesList.toArray(moviesArray);
        return moviesArray;
    }

    public void getAllMoviesStarredBy(BTreeNode node, List<Movie> listOfMovies, String name){
        int i = 0;
        for (i = 0; i < node.n; i++) {

            if (node.leaf == false) {
                getAllMoviesStarredBy(node.C[i],listOfMovies, name);
            }
            for(Person actor:node.keys[i].getCast()){
                if(actor.getName().equals(name)){
                    listOfMovies.add(node.keys[i]);
                }
            }

        }

        if (node.leaf == false) {
            getAllMoviesStarredBy(node.C[i],listOfMovies, name);
        }
        return;
    }
}


