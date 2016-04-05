package com.example.list.gabbisandra;

public class Item {
	  public long id;
	  private String title;
      private String description;
	  private int rating;

	  public Item(){
	  }

	  public long getId() {
	    return id;
	  }

	  public void setId(long id) {
	    this.id = id;
	  }

	  public String getTitle() {
	    return title;
	  }

	  public void setTitle(String title) {
	    this.title = title;
	  }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return title;
	  }

}