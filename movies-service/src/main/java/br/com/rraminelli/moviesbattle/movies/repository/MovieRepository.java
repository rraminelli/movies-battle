package br.com.rraminelli.moviesbattle.movies.repository;

import br.com.rraminelli.moviesbattle.movies.dto.MoviePairDto;
import br.com.rraminelli.moviesbattle.movies.entity.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {

    @Query("select new br.com.rraminelli.moviesbattle.movies.dto.MoviePairDto(m1, m2) " +
            "from Movie m1, Movie m2 " +
            "where m1.id <> m2.id " +
            "and concat(m1.id, ';', m2.id) NOT IN (:excludeIdMoviesPair) " +
            "and concat(m2.id, ';', m1.id) NOT IN (:excludeIdMoviesPair) " +
            "order by RAND() ")
    List<MoviePairDto> getMoviePairGame(
            @Param("excludeIdMoviesPair") List<String> excludeIdMoviesPair,
            Pageable pageable
    );

}
