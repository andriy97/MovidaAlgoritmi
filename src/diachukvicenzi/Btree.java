package diachukvicenzi;
import movida.commons.Movie;
import movida.commons.Person;

import java.util.HashSet;
import java.util.Set;

public class Btree {
    public BTreeNode root; // Pointer to root node
    public int t; // Minimum degree
    int size = 0; //size of tree;

    // Constructor (Initializes tree as empty)
    Btree() {
        this.root = null;
        this.t = 3; //the degree of BTree will always be 3
    }

    // function to traverse the tree
    public void traverse() {
        if (this.root != null)
            this.root.traverse();
        System.out.println();
    }

    // function to search a key in this tree
    public BTreeNode searchKey(Movie k) {
        if (this.root == null)
            return null;
        else
            return this.root.searchFromThisNode(k);
    }

    void insert(Movie k) {
        size++;
        if(root == null) {
            root = new BTreeNode(t,true);
            root.keys[0] = k;
            root.n = 1;
        }else {
            if(root.n == 2*t-1) {
                BTreeNode s = new BTreeNode(t,false);
                s.C[0] = root;
                s.splitChild(0,root);
                int i=0;
                if(s.keys[0].compareTo(k) < 1) {
                    i++;
                }
                s.C[i].insertNonFull(k);
                root = s;
            }else {
                root.insertNonFull(k);
            }
        }
    }

    void remove(Movie k) {
        size--;
        if(root == null) {
            System.out.println("Empty tree");
            return;
        }
        root.removeFromNode(k);
        if(root.n == 0) {
            BTreeNode tmp = root;
            if(root.leaf)
                root = null;
            else
                root = root.C[0];
        }
        return;
    }

    public int countMovies() {
        if (this.root != null)
            //return this.root.keys;
            return root.countKeys(root.n);
        else
            return 0;
    }

    // A BTree node
    class BTreeNode {
        Movie[] keys; // An array of keys
        int t; // Minimum degree (defines the range for number of keys)
        BTreeNode[] C; // An array of child pointers
        int n; // Current number of keys
        boolean leaf; // Is true when node is leaf. Otherwise false

        // Constructor
        BTreeNode(int t, boolean leaf) {
            this.t = t;
            this.leaf = leaf;
            this.keys = new Movie[2 * t - 1];
            this.C = new BTreeNode[2 * t];
            this.n = 0;
        }

        public int findKey(Movie k) {
            int idx = 0;
            while (idx<n && keys[idx].compareTo(k) < 0)
                ++idx;
            return idx;
        }

        public int countKeys(int n){
            return 0;

        }

        public void removeFromNode(Movie k) {
            int idx = findKey(k);
            if(idx < n && keys[idx].equals(k)) {
                if(leaf)
                    removeFromLeaf(idx);
                else
                    removeFromNonLeaf(idx);
            }else {
                if(leaf) {
                    System.out.println("The key "+k+" does not exist in tree");
                    return;
                }
                boolean flag = ((idx==n)? true : false);
                if(C[idx].n < t)
                    fill(idx);
                if(flag && idx > n)
                    C[idx - 1].removeFromNode(k);
                else
                    C[idx].removeFromNode(k);
            }
            return;
        }

        public void removeFromLeaf(int idx) {
            // Move all the keys after the idx-th pos one place backward
            for (int i=idx+1; i<n; ++i)
                keys[i-1] = keys[i];

            // Reduce the count of keys
            n--;

            return;
        }

        public void removeFromNonLeaf(int idx) {
            Movie k = keys[idx];

            // If the child that precedes k (C[idx]) has atleast t keys,
            // find the predecessor 'pred' of k in the subtree rooted at
            // C[idx]. Replace k by pred. Recursively delete pred
            // in C[idx]
            if (C[idx].n >= t)
            {
                Movie pred = getPred(idx);
                keys[idx] = pred;
                C[idx].removeFromNode(pred);
            }

            // If the child C[idx] has less that t keys, examine C[idx+1].
            // If C[idx+1] has atleast t keys, find the successor 'succ' of k in
            // the subtree rooted at C[idx+1]
            // Replace k by succ
            // Recursively delete succ in C[idx+1]
            else if  (C[idx+1].n >= t)
            {
                Movie succ = getSucc(idx);
                keys[idx] = succ;
                C[idx+1].removeFromNode(succ);
            }

            // If both C[idx] and C[idx+1] has less that t keys,merge k and all of C[idx+1]
            // into C[idx]
            // Now C[idx] contains 2t-1 keys
            // Free C[idx+1] and recursively delete k from C[idx]
            else
            {
                merge(idx);
                C[idx].removeFromNode(k);
            }
            return;
        }

        public Movie getPred(int idx) {
            // Keep moving to the right most node until we reach a leaf
            BTreeNode cur = C[idx];
            while (!cur.leaf)
                cur = cur.C[cur.n];

            // Return the last key of the leaf
            return cur.keys[cur.n-1];
        }

        public Movie getSucc(int idx) {
            // Keep moving the left most node starting from C[idx+1] until we reach a leaf
            BTreeNode cur = C[idx+1];
            while (!cur.leaf)
                cur = cur.C[0];

            // Return the first key of the leaf
            return cur.keys[0];
        }

        public void fill(int idx) {
            // If the previous child(C[idx-1]) has more than t-1 keys, borrow a key
            // from that child
            if (idx!=0 && C[idx-1].n >= t)
                borrowFromPrev(idx);

                // If the next child(C[idx+1]) has more than t-1 keys, borrow a key
                // from that child
            else if (idx!=n && C[idx+1].n >= t)
                borrowFromNext(idx);

                // Merge C[idx] with its sibling
                // If C[idx] is the last child, merge it with with its previous sibling
                // Otherwise merge it with its next sibling
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

            // The last key from C[idx-1] goes up to the parent and key[idx-1]
            // from parent is inserted as the first key in C[idx]. Thus, the  loses
            // sibling one key and child gains one key

            // Moving all key in C[idx] one step ahead
            for (int i=child.n-1; i>=0; --i)
                child.keys[i+1] = child.keys[i];

            // If C[idx] is not a leaf, move all its child pointers one step ahead
            if (!child.leaf)
            {
                for(int i=child.n; i>=0; --i)
                    child.C[i+1] = child.C[i];
            }

            // Setting child's first key equal to keys[idx-1] from the current node
            child.keys[0] = keys[idx-1];

            // Moving sibling's last child as C[idx]'s first child
            if(!child.leaf)
                child.C[0] = sibling.C[sibling.n];

            // Moving the key from the sibling to the parent
            // This reduces the number of keys in the sibling
            keys[idx-1] = sibling.keys[sibling.n-1];

            child.n += 1;
            sibling.n -= 1;

            return;
        }

        public void  borrowFromNext(int idx) {

            BTreeNode child = C[idx];
            BTreeNode sibling = C[idx+1];

            // keys[idx] is inserted as the last key in C[idx]
            child.keys[(child.n)] = keys[idx];

            // Sibling's first child is inserted as the last child
            // into C[idx]
            if (!(child.leaf))
                child.C[(child.n)+1] = sibling.C[0];

            //The first key from sibling is inserted into keys[idx]
            keys[idx] = sibling.keys[0];

            // Moving all keys in sibling one step behind
            for (int i=1; i<sibling.n; ++i)
                sibling.keys[i-1] = sibling.keys[i];

            // Moving the child pointers one step behind
            if (!sibling.leaf)
            {
                for(int i=1; i<=sibling.n; ++i)
                    sibling.C[i-1] = sibling.C[i];
            }

            // Increasing and decreasing the key count of C[idx] and C[idx+1]
            // respectively
            child.n += 1;
            sibling.n -= 1;

            return;
        }

        public void merge(int idx) {
            BTreeNode child = C[idx];
            BTreeNode sibling = C[idx+1];

            // Pulling a key from the current node and inserting it into (t-1)th
            // position of C[idx]
            child.keys[t-1] = keys[idx];

            // Copying the keys from C[idx+1] to C[idx] at the end
            for (int i=0; i<sibling.n; ++i)
                child.keys[i+t] = sibling.keys[i];

            // Copying the child pointers from C[idx+1] to C[idx]
            if (!child.leaf)
            {
                for(int i=0; i<=sibling.n; ++i)
                    child.C[i+t] = sibling.C[i];
            }

            // Moving all keys after idx in the current node one step before -
            // to fill the gap created by moving keys[idx] to C[idx]
            for (int i=idx+1; i<n; ++i)
                keys[i-1] = keys[i];

            // Moving the child pointers after (idx+1) in the current node one
            // step before
            for (int i=idx+2; i<=n; ++i)
                C[i-1] = C[i];

            // Updating the key count of child and the current node
            child.n += sibling.n+1;
            n--;

            return;
        }

        // A function to traverse all nodes in a subtree rooted with this node
        public void traverse() {
            // There are n keys and n+1 children, travers through n keys
            // and first n children
            int i = 0;
            for (i = 0; i < this.n; i++) {

                // If this is not leaf, then before printing key[i],
                // traverse the subtree rooted with child C[i].
                if (this.leaf == false) {
                    C[i].traverse();
                }
                System.out.println(keys[i] + " ");
            }

            // Print the subtree rooted with last child
            if (leaf == false)
                C[i].traverse();
        }

        // A function to search a key in the subtree rooted with this node.
        BTreeNode searchFromThisNode(Movie k) { // returns NULL if k is not present.

            // Find the first key greater than or equal to k
            int i = 0;
            while (i < n && k.compareTo(keys[i]) > 0)
                i++;

            // If the found key is equal to k, return this node

            //if(keys[i].equals(k))
            if(i < n && keys[i]!=null && keys[i].getTitle().equalsIgnoreCase(k.getTitle()))
                return this;

            // If the key is not found here and this is a leaf node
            if (leaf == true)
                return null;

            // Go to the appropriate child
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

    public Movie[] searchMoviesByTitle(String k){
        Movie[] movies=getAllMovies();
        Set<Movie> movieSet=new HashSet<>();
        for(Movie movie:movies){
            if(movie.getTitle().contains(k)){
                movieSet.add(movie);
            }
        }
        movies=new Movie[movieSet.size()];
        return movieSet.toArray(movies);

    }
    public Movie[] searchMoviesInYear(Integer year){
        Movie[] movies=getAllMovies();
        Set<Movie> movieSet=new HashSet<>();
        for(Movie movie:movies){
            if(movie.getYear()==year){
                movieSet.add(movie);
            }
        }
        movies=new Movie[movieSet.size()];
        return movieSet.toArray(movies);
    }
    public Movie[] searchMoviesDirectedBy(String name){
        Movie[] movies=getAllMovies();
        Set<Movie> movieSet=new HashSet<>();
        for(Movie movie:movies){
            if(movie.getDirector().getName()==name){
                movieSet.add(movie);
            }
        }
        movies=new Movie[movieSet.size()];
        return movieSet.toArray(movies);
    }

    public Movie[] searchMoviesStarredBy(String name){
        Movie[] movies=getAllMovies();
        Set<Movie> movieSet=new HashSet<>();
        for(Movie movie:movies){
            for(Person actor:movie.getCast()){
                if(actor.getName()==name){
                    movieSet.add(movie);
                }

            }
        }
        movies=new Movie[movieSet.size()];
        return movieSet.toArray(movies);
    }

    public boolean deleteMovieByTitle(String k){
        if (getMovieByTitle(k)!=null){
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

    public int getNextPosition(Movie[] listOfMovies) {
        int i;
        for(i = 0; i < listOfMovies.length; i++) {
            if(listOfMovies[i] == null) {
                return i;
            }
        }
        return -1;
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

}

