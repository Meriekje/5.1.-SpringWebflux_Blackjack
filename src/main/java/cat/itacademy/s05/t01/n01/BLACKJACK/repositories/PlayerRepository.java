package cat.itacademy.s05.t01.n01.BLACKJACK.repositories;

import cat.itacademy.s05.t01.n01.BLACKJACK.model.Player;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends ReactiveCrudRepository<Player, Long> {
    Mono<Player> findByName(String name);

    @Query("SELECT * FROM players ORDER BY win_rate DESC, total_winnings DESC")
    Flux<Player> findAllOrderByRanking();
}