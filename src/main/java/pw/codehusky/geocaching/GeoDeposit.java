package pw.codehusky.geocaching;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by lokio on 12/25/2016.
 */
public class GeoDeposit {
    public static Inventory create(String cacheID, Geocaching plugin){
        return Inventory.builder()
                .of(InventoryArchetypes.DISPENSER)
                .property(InventoryTitle.PROPERTY_NAME,InventoryTitle.of(Text.of("Deposit Item")))
                .listener(ClickInventoryEvent.class, evt -> {
                    Player responsible = (Player)evt.getCause().root();
                    if(evt.getTransactions().size() == 0)
                        return;

                    String t = evt.getTransactions().get(0).getSlot().parent().getName().get();
                    if(t.equals("Deposit Item")){
                        responsible.closeInventory(plugin.genericCause);
                        Task.Builder twenty = plugin.scheduler.createTaskBuilder();
                        twenty.execute(task -> {

                            ItemStackSnapshot ss = evt.getCursorTransaction().getOriginal();
                            ItemStack clone = ss.createStack();
                            clone.setQuantity(1);
                            Inventory redeem = GeoWithdraw.create(clone,cacheID,plugin);

                            int retCount = ss.getCount()-1;
                            if(retCount > 0) {
                                ItemStack ret = ss.createStack();
                                ret.setQuantity(retCount);
                                responsible.getInventory().offer(ret);
                            }
                            int items = 0;
                            for(Inventory ps : redeem.slots()){
                                Slot s = (Slot) ps;
                                if(s.peek().isPresent()){
                                    items += s.peek().get().getQuantity();
                                }

                            }
                            if(items < 2){
                                responsible.sendMessage(Text.of(TextColors.DARK_GREEN,"GEOCACHING",TextColors.RESET,": There currently are not enough items in this cache to take an item out! Please either put another item in, or try again later!"));
                                return;
                            }
                            /*if(redeem.totalItems() == 1){
                                return;
                            }*///useless code
                            responsible.openInventory(redeem,plugin.genericCause);
                        }).delayTicks(1).submit(Sponge.getPluginManager().getPlugin("geocaching").get());
                    }

                })
                .build(Sponge.getPluginManager().getPlugin("geocaching").get());

    }
}
