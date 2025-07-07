package cat.itacademy.s05.t01.n01.BLACKJACK.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request to make a play")
public class PlayRequest {
    @NotNull(message = "Action is required")
    @Schema(description = "Player action", example = "HIT")
    private PlayAction action;

    public enum PlayAction {
        @Schema(description = "I need another card")
        HIT,
        @Schema(description = "Keep current hand")
        STAND
    }
}