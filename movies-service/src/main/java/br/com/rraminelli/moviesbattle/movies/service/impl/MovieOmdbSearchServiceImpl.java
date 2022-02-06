package br.com.rraminelli.moviesbattle.movies.service.impl;

import br.com.rraminelli.moviesbattle.movies.entity.Movie;
import br.com.rraminelli.moviesbattle.movies.repository.MovieRepository;
import br.com.rraminelli.moviesbattle.movies.service.MovieOmdbSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class MovieOmdbSearchServiceImpl implements MovieOmdbSearchService {

    @Value("${app.urlOmdbapi}")
    private String urlOmdbapi;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    private static final int MAX_PAGE_MOVIES = 7;

    @Override
    @Transactional
    public void loadAllMovies() {
        final List<String> idMovies = getAllIdMovies();
        this.getInfoMoviesAndSave(idMovies);
    }

    private List<String> getAllIdMovies() {
        final List<String> idMovies = new ArrayList<>();

        final String url = new StringBuilder(urlOmdbapi).append("&type=movie&s=title&page=%d").toString();

        for (int i=1; i <= MAX_PAGE_MOVIES; i++) {
            final String urlMovies = String.format(url, i);
            final HttpEntity<Map<String, Object>> response = getResponseEntityString(urlMovies);
            if (response != null && response.getBody() != null) {
                final List movies = (List) response.getBody().get("Search");
                movies.forEach(movie -> {
                    final Map<String, Object> movieMap = (Map<String, Object>) movie;
                    idMovies.add((String) movieMap.get("imdbID"));
                });
            }
        }

        return idMovies;
    }

    private void getInfoMoviesAndSave(final List<String> idMovies) {
        final List<Movie> movies = new ArrayList<>();
        idMovies.forEach(idMovie -> {
            final Movie movie = this.getInfoMovie(idMovie);
            if (movie != null && movie.getRating() > 0) {
                movies.add(movie);
            }
        });
        movieRepository.saveAll(movies);
    }

    private Movie getInfoMovie(String idMovie) {
        final String url = new StringBuilder(urlOmdbapi).append("&i=%s").toString();
        final String urlInfoMovie = String.format(url, idMovie);
        final HttpEntity<Map<String, Object>> response = getResponseEntityString(urlInfoMovie);
        final Map<String, Object> infoMovieMap = response != null ? response.getBody() : new HashMap<>();
        return this.setInfoMovie(infoMovieMap);
    }

    private Movie setInfoMovie(final Map<String, Object> infoMovieMap) {
        if (infoMovieMap.isEmpty()) {
            return null;
        }
        return new Movie(
                (String) infoMovieMap.get("imdbID"),
                NumberUtils.toFloat((String) infoMovieMap.get("imdbRating")),
                NumberUtils.toFloat((String) infoMovieMap.get("imdbVotes")),
                (String) infoMovieMap.get("Actors"),
                (String) infoMovieMap.get("Plot"),
                (String) infoMovieMap.get("Awards"),
                (String) infoMovieMap.get("Poster"),
                (String) infoMovieMap.get("Genre")
        );
    }

    private HttpEntity<Map<String, Object>> getResponseEntityString(String url) {
        try {
            final HttpEntity entity = getHttpEntity();
            final ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<>() {};
            return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    private HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
}
