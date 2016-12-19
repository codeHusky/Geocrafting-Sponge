package pw.codehusky.geocaching;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.*;


@Plugin(id="geocaching", name = "Geocaching",version = "0.1.0",description = "Does the geocaching.")
public class Geocaching {
    @Inject
    private Logger logger;

    private Map<UUID,Object[]> cachers = new HashMap<>();

    @Inject
    private PluginContainer pC;

    @Listener
    public void onServerStart(GameStartedServerEvent event){
        logger.info("Geocaching v" + pC.getVersion().get() + " starting...");
    }

    @Listener
    public void onInventoryClick(ChangeInventoryEvent event){
        Player cause;
        if(!(event.getCause().all().get(0) instanceof Player)){
            return;
        }else{
            cause = (Player) event.getCause().all().get(0);
            if(!cachers.containsKey(cause.getUniqueId())){
                Object[] generic = {false,false,-1};
                cachers.put(cause.getUniqueId(),generic);
            }
        }
        Inventory tt = event.getTargetInventory();
        if(tt.getName().get().contains("§a") && tt.getName().get().contains("'s Geocache")){
            if(event.getTransactions().size() > 0) {
                SlotTransaction g = event.getTransactions().get(0);
                Optional<ItemStack> pre = g.getSlot().peek();
                if(pre.isPresent()){
                    ItemStack ourStack = pre.get();
                    Inventory t = g.getSlot().parent();
                    if(t.getName().get().contains("§a") && t.getName().get().contains("'s Geocache")){
                        logger.info("Geocache");
                    }else{
                        logger.info("Player inventory");
                        PlayerInventory pI = (PlayerInventory) t;
                        if(g.getSlot().contains(ourStack))
                            logger.info("A");
                    }
                }

            }
        }
    }
}
