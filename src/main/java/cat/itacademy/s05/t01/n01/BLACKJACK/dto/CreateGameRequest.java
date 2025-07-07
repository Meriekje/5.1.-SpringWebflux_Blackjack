package cat.itacademy.s05.t01.n01.BLACKJACK.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Request to create a new game")
public class CreateGameRequest {
    @NotBlank(message = "Player name is required")
    @Schema(description = "Name of the player", example = "Joan")
    private String playerName;

    @Positive(message = "Bet must be positive")
    @Schema(description = "Initial bet amount", example = "10.0")
    private double bet = 10.0;
}