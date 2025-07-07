package cat.itacademy.s05.t01.n01.BLACKJACK.services;

import cat.itacademy.s05.t01.n01.BLACKJACK.dto.CreateGameRequest;
import cat.itacademy.s05.t01.n01.BLACKJACK.dto.PlayRequest;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Game;
import reactor.core.publisher.Mono;

public interface GameService {
    Mono<Game> createGame(CreateGameRequest request);
    Mono<Game> getGame(String gameId);
    Mono<Game> playGame(String gameId, PlayRequest play);
    Mono<Void> deleteGame(String gameId);
}