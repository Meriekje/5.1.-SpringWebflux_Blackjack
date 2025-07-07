package cat.itacademy.s05.t01.n01.BLACKJACK.controllers;

import cat.itacademy.s05.t01.n01.BLACKJACK.dto.CreateGameRequest;
import cat.itacademy.s05.t01.n01.BLACKJACK.dto.PlayRequest;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Game;
import cat.itacademy.s05.t01.n01.BLACKJACK.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Tag(name = "Game", description = "Blackjack game operations")
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")                    // Spring: POST /game/new
    @ResponseStatus(HttpStatus.CREATED)     // Spring: retorna codi 201
    @Operation(summary = "Create a new game", description = "Creates a new Blackjack game")  // 📖 Swagger
    @ApiResponse(responseCode = "201", description = "Game created successfully")            // 📖 Swagger
    public Mono<Game> createGame(@Valid @RequestBody CreateGameRequest request) {
        return gameService.createGame(request);
    }

    @GetMapping("/{id}")                    // Spring: GET /game/{id}
    @Operation(summary = "Get game details", description = "Retrieves details of a specific game")  // 📖 Swagger
    @ApiResponse(responseCode = "200", description = "Game found")                                   // 📖 Swagger
    @ApiResponse(responseCode = "404", description = "Game not found")                               // 📖 Swagger
    public Mono<Game> getGame(
            @Parameter(description = "Game ID") @PathVariable String id) {  // 📖 Swagger: documenta paràmetre
        return gameService.getGame(id);
    }

    @PostMapping("/{id}/play")              // Spring: POST /game/{id}/play
    @Operation(summary = "Make a play", description = "Makes a play in an existing game")  // 📖 Swagger
    @ApiResponse(responseCode = "200", description = "Play executed successfully")         // 📖 Swagger
    @ApiResponse(responseCode = "404", description = "Game not found")                     // 📖 Swagger
    @ApiResponse(responseCode = "400", description = "Invalid game state")                 // 📖 Swagger
    public Mono<Game> playGame(
            @Parameter(description = "Game ID") @PathVariable String id,
            @Valid @RequestBody PlayRequest playRequest) {
        return gameService.playGame(id, playRequest);
    }

    @DeleteMapping("/{id}/delete")          // Spring: DELETE /game/{id}/delete
    @ResponseStatus(HttpStatus.NO_CONTENT)  // Spring: retorna codi 204
    @Operation(summary = "Delete game", description = "Deletes an existing game")  // 📖 Swagger
    @ApiResponse(responseCode = "204", description = "Game deleted successfully")  // 📖 Swagger
    @ApiResponse(responseCode = "404", description = "Game not found")             // 📖 Swagger
    public Mono<Void> deleteGame(
            @Parameter(description = "Game ID") @PathVariable String id) {
        return gameService.deleteGame(id);
    }
}