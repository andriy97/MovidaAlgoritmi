package diachukvicenzi;

import movida.commons.Movie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import movida.commons.Person;
public class AVL {

    public static class AVLNode {

        int bf;
        Movie m;
        int height;
        AVLNode left, right;

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

        int compare = m.getTitle().compareTo(node.getTitle());

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

        }

        else if (node.bf == 2) {

            if (node.right.bf >= 0) {
                return rightRightCase(node);
            } else {
                return rightLeftCase(node);
            }

        }

        return node;
    }

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

    public void update(AVLNode node) {
        int leftNodeHeight = (node.left == null) ? -1 : node.left.height;
        int rightNodeHeight = (node.right == null) ? -1 : node.right.height;

        node.height = Math.max(leftNodeHeight, rightNodeHeight) + 1;
        node.bf = rightNodeHeight - leftNodeHeight;
    }

    public boolean insert(Movie m) {
        if (m == null || m.getTitle() == null) {
            return false;
        }

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

    public AVLNode remove(AVLNode node, Movie m) {
        if (node == null || node.getTitle() == null) {
            return null;
        }

        int compare = m.getTitle().compareTo(node.getTitle());

        if (compare < 0) {

            node.left = remove(node.left, m);
        } else if (compare > 0) {
            node.right = remove(node.right, m);
        }

        else {
            // only right subtree o no subtree
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }

            else {
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

    //set di attori di tipo persona presenti nell'albero(non ci sono duplicati perchè è un set)
    Set<Person> actor;

    // conta i nodi
    public int countPeople() {
        actor = new HashSet<>();
        countPeople(root);
        return actor.size();
    }

    //scorre ricorsivamente i nodi e conta gli attori
    public void countPeople(AVLNode r) {
        if (r != null) {
            EsaminaNodoActor(r);
            countPeople(r.left);
            countPeople(r.right);

        }

    }

    //funzione che ritorna il set completo di attori
    public Set<Person> getPersonSet() {
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

    int sommaCast=0;
    //inserisce gli attori del cast nel set attori
    public void EsaminaNodoActor(AVLNode n) {
        Person[] cast=n.m.getCast();
        sommaCast=sommaCast+cast.length;
        if (n != null) {
            actor.add(n.m.getDirector());
            for (int i = 0; i < cast.length; i++) {

                actor.add(cast[i]);
            }
        }
    }

    Set<String> ActorName;
    Person[] FilmCount;
    int flag=-1;

    public Person[] getPersonSet1() {
        flag=-1;
        FilmCount=new Person[sommaCast];
        ActorName = new HashSet<>();
        getPersonSet1(root);
        Person[] result=new Person[flag];
        for (int i = 0; i <flag ; i++) {
            result[i]=FilmCount[i];
        }

        return result;
    }

    //ricorsivamente controlla tutti i nodi per inserire tutti gli attori di un film
    public void getPersonSet1(AVLNode r) {
        if (r != null) {
            ContaFilm(r);
            getPersonSet1(r.left);
            getPersonSet1(r.right);
        }
    }

    public void ContaFilm(AVLNode n) {
        Person[] cast=n.m.getCast();

        for (int i = 0; i < cast.length; i++) {

            if(ActorName.contains(cast[i].getName().trim()))
            {
                for(int y=0;y<flag+1;y++){
                    if(FilmCount[y].getName().trim().contains(cast[i].getName().trim())){
                        FilmCount[y].setFilmCount(FilmCount[y].getFilmCount()+1);
                    }
                }
            }
            else {
                ActorName.add(cast[i].getName().trim());
                flag=flag+1;
                FilmCount[flag]=cast[i];
            }
        }

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


    Set<Movie> FilmAnno;

    public Set<Movie> getMovieYearSet(Integer Anno) {
        FilmAnno = new HashSet<>();
        getMovieYearSet(root,Anno);
        return FilmAnno;
    }

    public void getMovieYearSet(AVLNode r,Integer Anno) {
        if (r != null) {
            EsaminaNodoYear(r,Anno);
            getMovieYearSet(r.left,Anno);
            getMovieYearSet(r.right,Anno);
        }
    }

    public void EsaminaNodoYear(AVLNode n,Integer Anno) {

        if (Anno-n.m.getYear()==0) {

            FilmAnno.add(n.m);
        }

    }


///////////////// ritorna un set di film di un determinato Regista

    Set<Movie> FilmDirector;

    public Set<Movie> searchMoviesDirectedBy(String Regista) {
        FilmDirector = new HashSet<>();
        searchMoviesDirectedBy(root,Regista);
        return FilmDirector;
    }

    public void searchMoviesDirectedBy(AVLNode r,String Regista) {
        if (r != null) {
            EsaminaNodoDirector(r,Regista);
            searchMoviesDirectedBy(r.left,Regista);
            searchMoviesDirectedBy(r.right,Regista);
        }
    }

    public void EsaminaNodoDirector(AVLNode n,String Regista) {

        if (n.m.getDirector().getName().trim().compareTo(Regista.trim())==0) {

            FilmDirector.add(n.m);
        }
    }
/////////////////////////////////////////////////////////////////////////////////

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
        if (compare > 1) {
            return getMovieByTitle(node.right, Title);
        }
        if (compare == 0 || compare==1) {
            return node.m;
        }

        return null;
    }







    public Person getPersonByName(String name) {
        for (Person p : getPersonSet()) {
            if (p.getName().trim().compareTo(name.trim())==0) {
                return p;
            }
        }
        return null;
    }

    public boolean deleteMovieByTitle(String Title) {

        if (getMovieByTitle(Title) != null) {
            remove(getMovieByTitle(root,Title));
            return true;
        }
        else return false;
    }


}
