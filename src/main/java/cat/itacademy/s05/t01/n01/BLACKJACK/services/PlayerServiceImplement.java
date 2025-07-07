package cat.itacademy.s05.t01.n01.BLACKJACK.services;

import cat.itacademy.s05.t01.n01.BLACKJACK.model.Game;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Player;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.GameStatus;
import cat.itacademy.s05.t01.n01.BLACKJACK.exceptions.PlayerNotFoundException;
import cat.itacademy.s05.t01.n01.BLACKJACK.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerServiceImplement implements PlayerService {

    private final PlayerRepository playerRepository;

    @Override
    public Mono<Player> findOrCreatePlayer(String name) {
        log.info("Finding or creating player with name: {}", name);

        return playerRepository.findByName(name)
                .doOnNext(player -> log.info("Player found: {}", player.getName()))
                .switchIfEmpty(createNewPlayer(name));
    }

    @Override
    public Mono<Player> updatePlayerName(Long playerId, String newName) {
        log.info("Updating player name for ID: {} to: {}", playerId, newName);

        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with ID: " + playerId)))
                .flatMap(player -> {
                    player.setName(newName);
                    player.setUpdatedAt(LocalDateTime.now());
                    return playerRepository.save(player);
                })
                .doOnSuccess(player -> log.info("Player name updated successfully for ID: {}", playerId))
                .doOnError(error -> log.error("Error updating player name: {}", error.getMessage()));
    }

    @Override
    public Mono<Void> updatePlayerStats(Long playerId, Game game) {
        log.info("Updating stats for player ID: {} based on game: {}", playerId, game.getId());

        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with ID: " + playerId)))
                .flatMap(player -> {
                    // Actualitzar estadístiques basades en el joc
                    player.setGamesPlayed(player.getGamesPlayed() + 1);
                    player.setUpdatedAt(LocalDateTime.now());

                    // Determinar si el jugador ha guanyat
                    boolean playerWon = determinePlayerWin(game);

                    if (playerWon) {
                        player.setGamesWon(player.getGamesWon() + 1);
                        // Assumint que hi ha algun sistema de guanys
                        player.setTotalWinnings(player.getTotalWinnings() + calculateWinnings(game));
                        log.info("Player {} won the game", player.getName());
                    }

                    // Calcular win rate
                    double winRate = player.getGamesPlayed() > 0 ?
                            (double) player.getGamesWon() / player.getGamesPlayed() * 100.0 : 0.0;
                    player.setWinRate(Math.round(winRate * 100.0) / 100.0);

                    return playerRepository.save(player);
                })
                .doOnSuccess(player -> log.info("Player stats updated successfully for ID: {}", playerId))
                .doOnError(error -> log.error("Error updating player stats: {}", error.getMessage()))
                .then();
    }

    @Override
    public Flux<Player> getRanking() {
        log.info("Fetching player ranking");

        return playerRepository.findAllOrderByRanking()
                .doOnNext(player -> log.debug("Processing player for ranking: {} - Win Rate: {}%",
                        player.getName(), player.getWinRate()))
                .doOnComplete(() -> log.info("Player ranking fetched successfully"))
                .doOnError(error -> log.error("Error fetching player ranking: {}", error.getMessage()));
    }

    // Mètodes privats auxiliars
    private Mono<Player> createNewPlayer(String name) {
        log.info("Creating new player with name: {}", name);

        Player newPlayer = Player.builder()
                .name(name)
                .gamesPlayed(0)
                .gamesWon(0)
                .totalWinnings(0.0)
                .winRate(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return playerRepository.save(newPlayer)
                .doOnSuccess(player -> log.info("New player created successfully with ID: {}", player.getId()));
    }

    private boolean determinePlayerWin(Game game) {
        GameStatus status = game.getStatus();

        // El jugador guanya en aquests casos:
        return status == GameStatus.PLAYER_WIN ||
                status == GameStatus.PLAYER_BLACKJACK ||
                status == GameStatus.DEALER_BUST;
    }

    private double calculateWinnings(Game game) {
        GameStatus status = game.getStatus();

        // Càlcul de guanys segons el tipus de victòria
        return switch (status) {
            case PLAYER_BLACKJACK -> 1.5; // Blackjack paga 3:2
            case PLAYER_WIN, DEALER_BUST -> 1.0; // Victòria normal paga 1:1
            default -> 0.0; // No hi ha guanys
        };
    }
}