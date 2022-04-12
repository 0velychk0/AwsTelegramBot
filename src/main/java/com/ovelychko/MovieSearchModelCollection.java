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
public class MovieSearchModelCollection {
    @JsonProperty("Search")
    public List<MovieSearchModel> search;
    @JsonProperty("totalResults")
    public int totalResults;
    @JsonProperty("Response")
    public boolean response;
    @JsonProperty("Error")
    public String error;
}
