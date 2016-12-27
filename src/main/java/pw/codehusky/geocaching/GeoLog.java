package pw.codehusky.geocaching;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

/**
 * Created by lokio on 12/25/2016.
 */
public class GeoLog {
    public static BookView create(String cacheID,Player openee,Geocaching plugin){
        Text logSwap = Text.builder().onClick(TextActions.executeCallback(commandSource ->  {
            Player plr = (Player) commandSource;

            plr.openInventory(GeoDeposit.create(cacheID,plugin),plugin.genericCause);
        })).append(Text.of(TextColors.RED, TextStyles.UNDERLINE,"here")).build();
        Text logOnly = Text.builder().onClick(TextActions.executeCallback(commandSource -> {

        })).append(Text.of(TextColors.RED,TextStyles.UNDERLINE,"here")).build();

        String resolvedCacheName = "what";
        return BookView.builder()
                .addPage(Text.of(TextStyles.BOLD,TextColors.DARK_GREEN,resolvedCacheName,TextColors.RESET,TextStyles.RESET,
                        TextColors.GRAY,"\nCache ID #" + cacheID,TextColors.RESET,
                        "\n\nClick " , logSwap, TextColors.RESET, " to log your find and swap an item.",
                        "\n\nClick ", logOnly,TextColors.RESET, " to only log your find.",
                        "\n\nGo to the next page to view previous visits."))
                .build();

    }
}
