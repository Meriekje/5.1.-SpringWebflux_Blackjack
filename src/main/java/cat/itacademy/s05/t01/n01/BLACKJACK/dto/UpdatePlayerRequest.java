package cat.itacademy.s05.t01.n01.BLACKJACK.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to update player name")
public class UpdatePlayerRequest {
    @NotBlank(message = "Player name is required")
    @Schema(description = "New player name", example = "Meritxell")
    private String name;
}