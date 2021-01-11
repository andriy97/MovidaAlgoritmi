package movida.commons;

import java.util.ArrayList;

public class Collaboration {

	Person actorA;
	Person actorB;
	ArrayList<Movie> movies;

	public Collaboration(Person actorA, Person actorB) {
		this.actorA = actorA;
		this.actorB = actorB;
		this.movies = new ArrayList<Movie>();
	}

	public ArrayList<Movie> film(){
		return movies;
	}
	public Person getActorA() {
		return actorA;
	}

	public Person getActorB() {
		return actorB;
	}

	public void addMovie(Movie m){
		this.movies.add(m);
	}

	public Double getScore(){

		Double score = 0.0;

		for (Movie m : movies)
			score += m.getVotes();

		return score / movies.size();
	}
	@Override
	public boolean equals(Object obj) {
		Collaboration c=(Collaboration) obj;
		return (this.getActorA().equals(c.getActorA()) && this.getActorB().equals(c.getActorB())
				|| this.getActorA().equals(c.getActorB()) && this.getActorB().equals(c.getActorA()));
	}


}
