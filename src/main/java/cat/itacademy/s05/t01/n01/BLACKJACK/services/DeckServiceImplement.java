package cat.itacademy.s05.t01.n01.BLACKJACK.services;

import cat.itacademy.s05.t01.n01.BLACKJACK.model.Card;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class DeckServiceImplement implements DeckService {
    private final Random random = new Random();

    @Override
    public List<Card> createShuffledDeck() {
        List<Card> deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                deck.add(Card.builder()
                        .suit(suit)
                        .rank(rank)
                        .build());
            }
        }
        Collections.shuffle(deck);
        return deck;
    }

    @Override
    public Card drawCard() {
        List<Card> deck = createShuffledDeck();
        return deck.get(random.nextInt(deck.size()));
    }
}