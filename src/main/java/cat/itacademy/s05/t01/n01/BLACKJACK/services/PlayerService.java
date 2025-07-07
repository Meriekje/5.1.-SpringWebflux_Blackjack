package cat.itacademy.s05.t01.n01.BLACKJACK.services;

import cat.itacademy.s05.t01.n01.BLACKJACK.model.Game;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerService {
    Mono<Player> findOrCreatePlayer(String name);
    Mono<Player> updatePlayerName(Long playerId, String newName);
    Mono<Void> updatePlayerStats(Long playerId, Game game);
    Flux<Player> getRanking();
}


