package br.com.rraminelli.moviesbattle.game.repository;

import br.com.rraminelli.moviesbattle.game.dto.ResumeGameDto;
import br.com.rraminelli.moviesbattle.game.entity.GamePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GamePointRepository extends JpaRepository<GamePoint, Long> {

    @Query("select gp from GamePoint gp " +
            "where gp.game.username = :username " +
            "and gp.result is null")
    GamePoint getQuizOpenByUsername(@Param("username") String username);

    @Query("select concat(gp.movie1Id,';',gp.movie2Id) " +
            "from GamePoint gp " +
            "where gp.game.id = :gameId")
    List<String> getIdsMoviesQuizByGameId(@Param("gameId") Long gameId);

    @Query("select distinct new br.com.rraminelli.moviesbattle.game.dto.ResumeGameDto (" +
            " gp.game.username, " +
            " sum(CASE gp.result WHEN true THEN 1 ELSE 0 END)," +
            " sum(CASE gp.result WHEN false THEN 1 ELSE 0 END) " +
            ") from GamePoint gp " +
            "where (:gameId is null or gp.game.id = :gameId) " +
            "group by gp.game.username ")
    List<ResumeGameDto> getResumeRanking(@Param("gameId") Long gameId);

}
