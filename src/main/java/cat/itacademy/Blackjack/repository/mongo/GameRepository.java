package cat.itacademy.Blackjack.repository.mongo;

import cat.itacademy.Blackjack.model.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String> {

}

