package cat.itacademy.Blackjack.application.mapper;

import cat.itacademy.Blackjack.dto.PlayerView;
import cat.itacademy.Blackjack.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Convierte entre Player (modelo interno) y PlayerView (DTO expuesto al cliente).
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlayerMapper {

    /** De modelo interno (MySQL) a DTO para API */
    PlayerView toView(Player player);
}
