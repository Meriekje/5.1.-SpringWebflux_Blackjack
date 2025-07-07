package cat.itacademy.s05.t01.n01.BLACKJACK.controllers;

import cat.itacademy.s05.t01.n01.BLACKJACK.dto.UpdatePlayerRequest;
import cat.itacademy.s05.t01.n01.BLACKJACK.model.Player;
import cat.itacademy.s05.t01.n01.BLACKJACK.services.PlayerService;
import cat.itacademy.s05.t01.n01.BLACKJACK.exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private PlayerService playerService;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        // Crear WebTestClient manualment amb el mock
        PlayerController playerController = new PlayerController(playerService);
        webTestClient = WebTestClient.bindToController(playerController).build();

        testPlayer = Player.builder()
                .id(1L)
                .name("TestPlayer")
                .gamesPlayed(10)
                .gamesWon(7)
                .totalWinnings(12.5)
                .winRate(70.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getRanking_ShouldReturnPlayersOrderedByRanking() {
        // Given
        Player player1 = Player.builder()
                .id(1L)
                .name("TopPlayer")
                .winRate(90.0)
                .totalWinnings(50.0)
                .build();

        Player player2 = Player.builder()
                .id(2L)
                .name("SecondPlayer")
                .winRate(75.0)
                .totalWinnings(30.0)
                .build();

        when(playerService.getRanking()).thenReturn(Flux.just(player1, player2));

        // When & Then
        webTestClient.get()
                .uri("/ranking")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Player.class)
                .hasSize(2)
                .value(players -> {
                    assert players.get(0).getName().equals("TopPlayer");
                    assert players.get(1).getName().equals("SecondPlayer");
                    assert players.get(0).getWinRate() == 90.0;
                    assert players.get(1).getWinRate() == 75.0;
                });

        verify(playerService).getRanking();
    }

    @Test
    void getRanking_WhenNoPlayers_ShouldReturnEmptyList() {
        // Given
        when(playerService.getRanking()).thenReturn(Flux.empty());

        // When & Then
        webTestClient.get()
                .uri("/ranking")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Player.class)
                .hasSize(0);

        verify(playerService).getRanking();
    }

    @Test
    void updatePlayerName_ShouldReturnUpdatedPlayer() {
        // Given
        UpdatePlayerRequest request = new UpdatePlayerRequest();
        request.setName("UpdatedName");

        Player updatedPlayer = Player.builder()
                .id(1L)
                .name("UpdatedName")
                .gamesPlayed(10)
                .gamesWon(7)
                .totalWinnings(12.5)
                .winRate(70.0)
                .createdAt(testPlayer.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(playerService.updatePlayerName(1L, "UpdatedName"))
                .thenReturn(Mono.just(updatedPlayer));

        // When & Then
        webTestClient.put()
                .uri("/player/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Player.class)
                .value(player -> {
                    assert player.getId().equals(1L);
                    assert player.getName().equals("UpdatedName");
                    assert player.getWinRate() == 70.0;
                    assert player.getTotalWinnings() == 12.5;
                });

        verify(playerService).updatePlayerName(1L, "UpdatedName");
    }

    @Test
    void updatePlayerName_WithInvalidId_ShouldReturnError() {
        // Given
        UpdatePlayerRequest request = new UpdatePlayerRequest();
        request.setName("UpdatedName");

        when(playerService.updatePlayerName(999L, "UpdatedName"))
                .thenReturn(Mono.error(new PlayerNotFoundException("Player not found with ID: 999")));

        // When & Then
        webTestClient.put()
                .uri("/player/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(playerService).updatePlayerName(999L, "UpdatedName");
    }

    @Test
    void updatePlayerName_WithDifferentValidName_ShouldReturnUpdatedPlayer() {
        // Given
        UpdatePlayerRequest request = new UpdatePlayerRequest();
        request.setName("NewPlayerName");

        Player updatedPlayer = Player.builder()
                .id(2L)
                .name("NewPlayerName")
                .gamesPlayed(5)
                .gamesWon(3)
                .totalWinnings(7.5)
                .winRate(60.0)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        when(playerService.updatePlayerName(2L, "NewPlayerName"))
                .thenReturn(Mono.just(updatedPlayer));

        // When & Then
        webTestClient.put()
                .uri("/player/2")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Player.class)
                .value(player -> {
                    assert player.getId().equals(2L);
                    assert player.getName().equals("NewPlayerName");
                    assert player.getWinRate() == 60.0;
                    assert player.getGamesPlayed() == 5;
                });

        verify(playerService).updatePlayerName(2L, "NewPlayerName");
    }

    @Test
    void updatePlayerName_ServiceThrowsGenericException_ShouldReturnError() {
        // Given
        UpdatePlayerRequest request = new UpdatePlayerRequest();
        request.setName("TestName");

        when(playerService.updatePlayerName(1L, "TestName"))
                .thenReturn(Mono.error(new RuntimeException("Database connection error")));

        // When & Then
        webTestClient.put()
                .uri("/player/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(playerService).updatePlayerName(1L, "TestName");
    }
}