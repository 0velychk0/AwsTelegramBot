package com.ovelychko;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSearchModel {
    @JsonProperty("Title")
    public String title;
    @JsonProperty("Year")
    public String year;
    @JsonProperty("imdbID")
    public String imdbID;
    @JsonProperty("Type")
    public String type;
    @JsonProperty("Poster")
    public String poster;
}
