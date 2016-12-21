package pw.codehusky.geocaching;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Plugin(id="geocaching", name = "Geocaching",version = "0.1.0",description = "Does the geocaching.")
public class Geocaching {
    @Inject
    private Logger logger;

    private Map<UUID,Object[]> cachers = new HashMap<>();

    @Inject
    private PluginContainer pC;
    private HashMap<Long,HashMap<UUID,HashMap<String,Boolean>>> geocacheData = new HashMap<>();
    @Listener
    public void onServerStart(GameStartedServerEvent event){
        logger.info("Geocaching v" + pC.getVersion().get() + " starting...");
        geocacheData.put(0l,new HashMap<>());
    }

    @Listener
    public void onInventoryChange(ChangeInventoryEvent event){
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
            HashMap<UUID,HashMap<String,Boolean>> ourCache = geocacheData.get(0l);
            if(!ourCache.containsKey(cause.getUniqueId())){
                ourCache.put(cause.getUniqueId(),new HashMap<>());
            }
            HashMap<String,Boolean> personalProperties = ourCache.get(cause.getUniqueId());
            if(event.getTransactions().size() > 0) {
                SlotTransaction g = event.getTransactions().get(0);
                Optional<ItemStack> pre = g.getSlot().peek();
                if(pre.isPresent() && false){
                    ItemStack ourStack = pre.get();
                    Inventory t = g.getSlot().parent();
                    if(t.getName().get().contains("§a") && t.getName().get().contains("'s Geocache")){
                        logger.info("Geocache");
                        if(t.contains(ourStack))
                            logger.info("geo find");
                    }else{
                        logger.info("Player inventory");
                        if(t.contains(ourStack))
                            logger.info("ply find");
                    }
                }

            }
        }
    }
    @Listener
    public void onInventoryClick(ClickInventoryEvent event){
        logger.info("woah");
        logger.info(event.getTransactions().get(0).getSlot().parent().getName().get());
    }
}
