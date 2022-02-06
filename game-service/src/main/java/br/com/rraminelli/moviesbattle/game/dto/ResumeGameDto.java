package br.com.rraminelli.moviesbattle.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
public class ResumeGameDto {

    private String username;
    private long corrects;
    private long faults;
    private double percent;

    public ResumeGameDto(String username, long corrects, long faults) {
        this.username = username;
        this.corrects = corrects;
        this.faults = faults;

        final BigDecimal totalQuiz = new BigDecimal(corrects + faults);
        if (totalQuiz.signum() > 0) {
            this.percent = new BigDecimal(corrects)
                    .divide(totalQuiz, 2, RoundingMode.HALF_UP)
                    .doubleValue() * 100;
        }
    }

}
