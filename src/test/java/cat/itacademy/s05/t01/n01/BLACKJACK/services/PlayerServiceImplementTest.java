package cat.itacademy.s05.t01.n01.BLACKJACK.services;

import cat.itacademy.s05.t01.n01.BLACKJACK.model.Game;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.GameStatus;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Player;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Card;
import cat.itacademy.s05.t01.n01.BLACKJACK.exceptions.PlayerNotFoundException;
import cat.itacademy.s05.t01.n01.BLACKJACK.repositories.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlayerServiceImplementTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImplement playerService;

    private Player testPlayer;
    private Game testGame;

    @BeforeEach
    void setUp() {
        testPlayer = Player.builder()
                .id(1L)
                .name("TestPlayer")
                .gamesPlayed(5)
                .gamesWon(3)
                .totalWinnings(4.5)
                .winRate(60.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Card> playerCards = new ArrayList<>();
        playerCards.add(new Card(Card.Suit.HEARTS, Card.Rank.TEN));
        playerCards.add(new Card(Card.Suit.SPADES, Card.Rank.KING));

        List<Card> dealerCards = new ArrayList<>();
        dealerCards.add(new Card(Card.Suit.DIAMONDS, Card.Rank.NINE));
        dealerCards.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT));

        testGame = Game.builder()
                .id("game-123")
                .playerId("1")
                .playerName("TestPlayer")
                .playerCards(playerCards)
                .dealerCards(dealerCards)
                .playerScore(20)
                .dealerScore(17)
                .status(GameStatus.PLAYER_WIN)
                .bet(10.0)
                .winnings(10.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findOrCreatePlayer_WhenPlayerExists_ShouldReturnExistingPlayer() {
        // Given
        when(playerRepository.findByName(anyString()))
                .thenReturn(Mono.just(testPlayer));
        when(playerRepository.save(any(Player.class)))
                .thenReturn(Mono.just(testPlayer));

        // When & Then
        StepVerifier.create(playerService.findOrCreatePlayer("TestPlayer"))
                .expectNextMatches(player ->
                        player.getName().equals("TestPlayer") &&
                                player.getId().equals(1L))
                .verifyComplete();

        verify(playerRepository, times(1)).findByName("TestPlayer");
        // Acceptem que save() es pugui cridar o no
        verify(playerRepository, atMost(1)).save(any(Player.class));
    }

    @Test
    void findOrCreatePlayer_WhenPlayerDoesNotExist_ShouldCreateNewPlayer() {
        // Given
        Player newPlayer = Player.builder()
                .id(2L)
                .name("NewPlayer")
                .gamesPlayed(0)
                .gamesWon(0)
                .totalWinnings(0.0)
                .winRate(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(playerRepository.findByName("NewPlayer"))
                .thenReturn(Mono.empty());
        when(playerRepository.save(any(Player.class)))
                .thenReturn(Mono.just(newPlayer));

        // When & Then
        StepVerifier.create(playerService.findOrCreatePlayer("NewPlayer"))
                .expectNextMatches(player ->
                        player.getName().equals("NewPlayer") &&
                                player.getGamesPlayed() == 0 &&
                                player.getGamesWon() == 0 &&
                                player.getTotalWinnings() == 0.0 &&
                                player.getWinRate() == 0.0)
                .verifyComplete();

        verify(playerRepository, times(1)).findByName("NewPlayer");
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void updatePlayerName_WhenPlayerExists_ShouldUpdateSuccessfully() {
        // Given
        Player updatedPlayer = Player.builder()
                .id(1L)
                .name("UpdatedName")
                .gamesPlayed(5)
                .gamesWon(3)
                .totalWinnings(4.5)
                .winRate(60.0)
                .createdAt(testPlayer.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(playerRepository.findById(1L))
                .thenReturn(Mono.just(testPlayer));
        when(playerRepository.save(any(Player.class)))
                .thenReturn(Mono.just(updatedPlayer));

        // When & Then
        StepVerifier.create(playerService.updatePlayerName(1L, "UpdatedName"))
                .expectNextMatches(player -> player.getName().equals("UpdatedName"))
                .verifyComplete();

        verify(playerRepository, times(1)).findById(1L);
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void updatePlayerName_WhenPlayerNotFound_ShouldThrowException() {
        // Given
        when(playerRepository.findById(999L))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(playerService.updatePlayerName(999L, "NewName"))
                .expectError(PlayerNotFoundException.class)
                .verify();

        verify(playerRepository, times(1)).findById(999L);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void updatePlayerStats_WhenPlayerWins_ShouldUpdateStatsCorrectly() {
        // Given
        Player updatedPlayer = Player.builder()
                .id(1L)
                .name("TestPlayer")
                .gamesPlayed(6)
                .gamesWon(4)
                .totalWinnings(5.5)
                .winRate(66.67)
                .createdAt(testPlayer.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(playerRepository.findById(1L))
                .thenReturn(Mono.just(testPlayer));
        when(playerRepository.save(any(Player.class)))
                .thenReturn(Mono.just(updatedPlayer));

        // When & Then
        StepVerifier.create(playerService.updatePlayerStats(1L, testGame))
                .verifyComplete();

        verify(playerRepository, times(1)).findById(1L);
        verify(playerRepository, times(1)).save(argThat(player ->
                player.getGamesPlayed() == 6 &&
                        player.getGamesWon() == 4));
    }

    @Test
    void updatePlayerStats_WhenPlayerLoses_ShouldUpdateStatsCorrectly() {
        // Given
        Game losingGame = Game.builder()
                .id("game-456")
                .playerId("1")
                .playerName("TestPlayer")
                .playerCards(new ArrayList<>())
                .dealerCards(new ArrayList<>())
                .playerScore(15)
                .dealerScore(20)
                .status(GameStatus.DEALER_WIN)
                .bet(10.0)
                .winnings(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Player updatedPlayer = Player.builder()
                .id(1L)
                .name("TestPlayer")
                .gamesPlayed(6)
                .gamesWon(3)
                .totalWinnings(4.5)
                .winRate(50.0)
                .createdAt(testPlayer.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(playerRepository.findById(1L))
                .thenReturn(Mono.just(testPlayer));
        when(playerRepository.save(any(Player.class)))
                .thenReturn(Mono.just(updatedPlayer));

        // When & Then
        StepVerifier.create(playerService.updatePlayerStats(1L, losingGame))
                .verifyComplete();

        verify(playerRepository, times(1)).findById(1L);
        verify(playerRepository, times(1)).save(argThat(player ->
                player.getGamesPlayed() == 6 &&
                        player.getGamesWon() == 3));
    }

    @Test
    void updatePlayerStats_WhenPlayerNotFound_ShouldThrowException() {
        // Given
        when(playerRepository.findById(999L))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(playerService.updatePlayerStats(999L, testGame))
                .expectError(PlayerNotFoundException.class)
                .verify();

        verify(playerRepository, times(1)).findById(999L);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void getRanking_ShouldReturnPlayersOrderedByRanking() {
        // Given
        Player player1 = Player.builder()
                .id(1L)
                .name("Player1")
                .winRate(80.0)
                .gamesPlayed(10)
                .gamesWon(8)
                .build();

        Player player2 = Player.builder()
                .id(2L)
                .name("Player2")
                .winRate(60.0)
                .gamesPlayed(10)
                .gamesWon(6)
                .build();

        when(playerRepository.findAllOrderByRanking())
                .thenReturn(Flux.just(player1, player2));

        // When & Then
        StepVerifier.create(playerService.getRanking())
                .expectNext(player1)
                .expectNext(player2)
                .verifyComplete();

        verify(playerRepository, times(1)).findAllOrderByRanking();
    }

    @Test
    void getRanking_WhenNoPlayers_ShouldReturnEmptyFlux() {
        // Given
        when(playerRepository.findAllOrderByRanking())
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(playerService.getRanking())
                .verifyComplete();

        verify(playerRepository, times(1)).findAllOrderByRanking();
    }
}