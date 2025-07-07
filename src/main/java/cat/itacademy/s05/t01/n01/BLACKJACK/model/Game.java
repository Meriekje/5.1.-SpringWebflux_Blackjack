package cat.itacademy.s05.t01.n01.BLACKJACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Card;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class Game {
    @Id
    private String id;
    private String playerId;
    private String playerName;
    private List<Card> playerCards;
    private List<Card> dealerCards;
    private int playerScore;
    private int dealerScore;
    private GameStatus status;
    private double bet;
    private double winnings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}