package com.ovelychko;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailsDataModel {
    @JsonProperty("Title")
    public String title;

    @JsonProperty("Year")
    public String year;

    @JsonProperty("Rated")
    public String rated;

    @JsonProperty("Released")
    public String released;

    @JsonProperty("Runtime")
    public String runtime;

    @JsonProperty("Genre")
    public String genre;

    @JsonProperty("Director")
    public String director;

    @JsonProperty("Writer")
    public String writer;

    @JsonProperty("Actors")
    public String actors;

    @JsonProperty("Plot")
    public String plot;

    @JsonProperty("Language")
    public String language;

    @JsonProperty("Country")
    public String country;

    @JsonProperty("Awards")
    public String awards;

    @JsonProperty("Poster")
    public String poster;

    @JsonProperty("Metascore")
    public String Metascore;

    @JsonProperty("imdbRating")
    public String imdbRating;

    @JsonProperty("imdbVotes")
    public String imdbVotes;

    @JsonProperty("imdbID")
    public String imdbID;

    @JsonProperty("Type")
    public String type;

    @JsonProperty("DVD")
    public String dvd;

    @JsonProperty("BoxOffice")
    public String boxOffice;

    @JsonProperty("Production")
    public String production;

    @JsonProperty("Website")
    public String website;

    @JsonProperty("Response")
    public String response;

    @JsonProperty("Ratings")
    public List<RatingsDataModel> ratings;
}
