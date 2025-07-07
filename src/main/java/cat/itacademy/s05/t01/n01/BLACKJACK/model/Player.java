package cat.itacademy.s05.t01.n01.BLACKJACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("players")
public class Player {
    @Id
    private Long id;
    private String name;
    private int gamesPlayed;
    private int gamesWon;
    private double totalWinnings;
    private double winRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}