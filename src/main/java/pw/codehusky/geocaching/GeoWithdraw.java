package pw.codehusky.geocaching;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * Created by lokio on 12/25/2016.
 */
public class GeoWithdraw {
    public static Inventory create(ItemStack stack, String cacheID, Geocaching plugin){
        Inventory redeem = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("Take Item")))
                .listener(ClickInventoryEvent.class, evt2 -> {
                    System.out.println("uh");
                    if(evt2.getTransactions().size() == 0)
                        return;
                    Inventory clickd = evt2.getTransactions().get(0).getSlot().parent();
                    String g = clickd.getName().get();
                    if(g.equals("Take Item")){
                        Player plr = (Player)evt2.getCause().root();
                        ItemStackSnapshot sn = evt2.getCursorTransaction().getFinal();
                        plr.getInventory().offer(sn.createStack());
                        plr.closeInventory(plugin.genericCause);
                        try {
                            Optional<TileEntity> tt = plugin.getGeocacheLocation(cacheID).getTileEntity();
                            if(tt.isPresent()){
                                TileEntity next = tt.get();
                                if(next instanceof TileEntityCarrier){
                                    Inventory updatable = ((TileEntityCarrier) next).getInventory();
                                    for(Inventory ps: clickd.slots()){
                                        Slot s = (Slot) ps;
                                        if(s.peek().isPresent()){
                                            if(s.peek().get() != sn.createStack())
                                                updatable.offer(s.peek().get());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //plugin.updateGeocacheContents(cacheID,clickd);
                    }
                }).build(Sponge.getPluginManager().getPlugin("geocaching").get());
        try {
            Optional<TileEntity> tt = plugin.getGeocacheLocation(cacheID).getTileEntity();
            if(tt.isPresent()) {
                TileEntity next = tt.get();
                if (next instanceof TileEntityCarrier) {
                    Inventory updatable = ((TileEntityCarrier) next).getInventory();
                    for (Inventory e : updatable.slots()) {
                        Slot s = (Slot) e;
                        Optional<ItemStack> stackmaybe = s.peek();
                        if (stackmaybe.isPresent()) {
                            redeem.offer(stackmaybe.get());
                            System.out.println("We found some items..");
                        }
                    }
                    updatable.offer(stack);
                }else{
                    System.out.println("Excuse me?");
                }
            }else{
                System.out.println("Uh, what?");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return redeem;
    }
}
