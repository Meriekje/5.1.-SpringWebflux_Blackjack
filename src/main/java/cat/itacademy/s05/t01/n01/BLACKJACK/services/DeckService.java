package cat.itacademy.s05.t01.n01.BLACKJACK.services;

import cat.itacademy.s05.t01.n01.BLACKJACK.model.Card;
import java.util.List;

public interface DeckService {
    List<Card> createShuffledDeck();
    Card drawCard();
}