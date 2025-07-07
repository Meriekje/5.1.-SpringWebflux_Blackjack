package cat.itacademy.s05.t01.n01.BLACKJACK.controllers;

import cat.itacademy.s05.t01.n01.BLACKJACK.dto.UpdatePlayerRequest;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Player;
import cat.itacademy.s05.t01.n01.BLACKJACK.services.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController                    // Spring: controlador REST
@RequiredArgsConstructor
@Tag(name = "Player", description = "Player operations")
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/ranking")         // Spring: GET /ranking
    @Operation(summary = "Get player ranking", description = "Retrieves the ranking of all players")  // 📖 Swagger
    @ApiResponse(responseCode = "200", description = "Ranking retrieved successfully")                 // 📖 Swagger
    public Flux<Player> getRanking() {
        return playerService.getRanking();
    }

    @PutMapping("/player/{playerId}")  // Spring: PUT /player/{playerId}
    @Operation(summary = "Update player name", description = "Updates the name of a player")  // 📖 Swagger
    @ApiResponse(responseCode = "200", description = "Player updated successfully")           // 📖 Swagger
    @ApiResponse(responseCode = "404", description = "Player not found")                      // 📖 Swagger
    public Mono<Player> updatePlayerName(
            @Parameter(description = "Player ID") @PathVariable Long playerId,
            @Valid @RequestBody UpdatePlayerRequest request) {
        return playerService.updatePlayerName(playerId, request.getName());
    }
}