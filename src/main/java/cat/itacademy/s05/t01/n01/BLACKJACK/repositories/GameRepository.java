package cat.itacademy.s05.t01.n01.BLACKJACK.repositories;

import cat.itacademy.s05.t01.n01.BLACKJACK.model.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String> {
    Flux<Game> findByPlayerId(String playerId);
    Flux<Game> findByPlayerName(String playerName);
}