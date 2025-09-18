package cat.itacademy.Blackjack.application.mapper;

import cat.itacademy.Blackjack.dto.PlayerView;
import cat.itacademy.Blackjack.model.Player;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PlayerMapper {


    PlayerView toView(Player player);
}
