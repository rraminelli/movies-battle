package br.com.rraminelli.moviesbattle.game.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "game_point")
public class GamePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "movie1_id")
    private String movie1Id;

    @Column(name = "movie2_id")
    private String movie2Id;

    @Column(name = "movie_check_id")
    private String movieCheckId;

    @Column(columnDefinition = "boolean")
    private Boolean result;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePoint gamePoint = (GamePoint) o;
        return id.equals(gamePoint.id);
    }

}
