package cat.itacademy.s05.t01.n01.BLACKJACK.services;

import cat.itacademy.s05.t01.n01.BLACKJACK.dto.CreateGameRequest;
import cat.itacademy.s05.t01.n01.BLACKJACK.dto.PlayRequest;
import cat.itacademy.s05.t01.n01.BLACKJACK.exceptions.GameNotFoundException;
import cat.itacademy.s05.t01.n01.BLACKJACK.exceptions.InvalidGameException;  // ✅ Ara existeix
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Card;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Game;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.GameStatus;
import cat.itacademy.s05.t01.n01.BLACKJACK.repositories.GameRepository;  // ✅ Respectem el teu nom
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImplement implements GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;
    private final DeckServiceImplement deckService;

    @Override
    public Mono<Game> createGame(CreateGameRequest request) {
        log.info("Creating new game for player: {}", request.getPlayerName());

        return playerService.findOrCreatePlayer(request.getPlayerName())
                .flatMap(player -> {

                    Game game = Game.builder()
                            .id(UUID.randomUUID().toString())
                            .playerId(player.getId().toString())
                            .playerName(player.getName())
                            .playerCards(new ArrayList<>())
                            .dealerCards(new ArrayList<>())
                            .playerScore(0)
                            .dealerScore(0)
                            .status(GameStatus.IN_PROGRESS)
                            .bet(request.getBet())
                            .winnings(0.0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    // Repartir cartes inicials
                    dealInitialCards(game);

                    return gameRepository.save(game);
                });
    }

    @Override
    public Mono<Game> getGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with id: " + gameId)));
    }

    @Override
    public Mono<Game> playGame(String gameId, PlayRequest playRequest) {
        return getGame(gameId)
                .flatMap(game -> {
                    if (game.getStatus() != GameStatus.IN_PROGRESS) {
                        return Mono.error(new InvalidGameException("Game is not in progress"));
                    }

                    switch (playRequest.getAction()) {
                        case HIT:
                            return handleHit(game);
                        case STAND:
                            return handleStand(game);
                        default:
                            return Mono.error(new IllegalArgumentException("Invalid action"));
                    }
                })
                .flatMap(gameRepository::save)
                .flatMap(this::updatePlayerStats);
    }

    @Override
    public Mono<Void> deleteGame(String gameId) {
        return gameRepository.deleteById(gameId);
    }

    private void dealInitialCards(Game game) {
        List<Card> deck = deckService.createShuffledDeck();


        game.getPlayerCards().add(deck.get(0));
        game.getDealerCards().add(deck.get(1));
        game.getPlayerCards().add(deck.get(2));
        game.getDealerCards().add(deck.get(3));

        game.setPlayerScore(calculateScore(game.getPlayerCards()));
        game.setDealerScore(calculateScore(game.getDealerCards()));

        // Comprovar Blackjack immediat
        if (game.getPlayerScore() == 21) {
            game.setStatus(GameStatus.PLAYER_BLACKJACK);
            game.setWinnings(game.getBet() * 1.5);  // Blackjack paga 1.5x
        }
    }

    private Mono<Game> handleHit(Game game) {
        Card newCard = deckService.drawCard();
        game.getPlayerCards().add(newCard);
        game.setPlayerScore(calculateScore(game.getPlayerCards()));
        game.setUpdatedAt(LocalDateTime.now());

        if (game.getPlayerScore() > 21) {
            game.setStatus(GameStatus.PLAYER_BUST);
            game.setWinnings(-game.getBet());  // Perd l'aposta
        }

        return Mono.just(game);
    }

    private Mono<Game> handleStand(Game game) {
        // La casa juga automàticament
        while (game.getDealerScore() < 17) {
            Card newCard = deckService.drawCard();
            game.getDealerCards().add(newCard);
            game.setDealerScore(calculateScore(game.getDealerCards()));
        }

        // Determinar guanyador
        determineWinner(game);
        game.setUpdatedAt(LocalDateTime.now());

        return Mono.just(game);
    }

    private void determineWinner(Game game) {
        int playerScore = game.getPlayerScore();
        int dealerScore = game.getDealerScore();

        if (dealerScore > 21) {
            game.setStatus(GameStatus.DEALER_BUST);
            game.setWinnings(game.getBet());
        } else if (playerScore > dealerScore) {
            game.setStatus(GameStatus.PLAYER_WIN);
            game.setWinnings(game.getBet());
        } else if (dealerScore > playerScore) {
            game.setStatus(GameStatus.DEALER_WIN);
            game.setWinnings(-game.getBet());
        } else {
            game.setStatus(GameStatus.PUSH);
            game.setWinnings(0.0);  // Empat
        }
    }

    private int calculateScore(List<Card> cards) {
        int score = 0;
        int aces = 0;

        for (Card card : cards) {
            if (card.getRank() == Card.Rank.ACE) {
                aces++;
            }
            score += card.getValue();
        }

        // Ajustar asos (11 → 1 si cal per evitar bust)
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    private Mono<Game> updatePlayerStats(Game game) {
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            return Mono.just(game);
        }

        return playerService.updatePlayerStats(Long.parseLong(game.getPlayerId()), game)
                .then(Mono.just(game));
    }
}