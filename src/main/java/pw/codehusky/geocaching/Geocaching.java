package pw.codehusky.geocaching;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Plugin(id="geocaching", name = "Geocaching",version = "0.1.0",description = "Does the geocaching.")
public class Geocaching {
    @Inject
    private Logger logger;
    @Inject
    private PluginContainer pC;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> privateConfig;

    private Map<UUID,Object[]> cachers = new HashMap<>();
    private HashMap<Long,HashMap<UUID,HashMap<String,Boolean>>> geocacheData = new HashMap<>();
    public Scheduler scheduler;
    public Cause genericCause;

    @Listener
    public void onServerStart(GameStartedServerEvent event){
        logger.info("Geocaching v" + pC.getVersion().get() + " starting...");
        geocacheData.put(0l,new HashMap<>());
        scheduler = Sponge.getScheduler();
        genericCause = Cause.of(NamedCause.of("PluginContainer",pC));
    }

    @Listener
    public void geocacheInteract(InteractBlockEvent.Secondary.MainHand event){

        Location<World> blk = event.getTargetBlock().getLocation().get();
        if(blk.getBlock().getType() == BlockTypes.CHEST || false) {
            TileEntity te = blk.getTileEntity().get();
            Inventory inv = ((TileEntityCarrier) te).getInventory();
            if(inv.getName().get().contains("§cCACHE#")){
                event.setCancelled(true);
                Task.Builder upcoming = scheduler.createTaskBuilder();
                Player openee = (Player) event.getCause().root();
                String cacheID =inv.getName().get().replace("§cCACHE#","");
                updateGeocacheLocation(cacheID,event.getTargetBlock().getLocation().get());
                upcoming.execute(() ->{
                    openee.sendBookView(GeoLog.create(cacheID,openee,this));
                }).delayTicks(1).submit(this);

            }
        }
    }
    //public void updateGeocacheLocation(String cacheID, )
    public void updateGeocacheLocation(String cacheID,Location<World> splenda){
        try {
            CommentedConfigurationNode root = privateConfig.load();
            CommentedConfigurationNode location = root.getNode("caches",cacheID,"location");
            location.getNode("worldUUID").setValue(splenda.getExtent().getUniqueId().toString());
            CommentedConfigurationNode pos = location.getNode("position");
            pos.getNode("x").setValue(splenda.getBlockX());
            pos.getNode("y").setValue(splenda.getBlockY());
            pos.getNode("z").setValue(splenda.getBlockZ());
            privateConfig.save(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Location<World> getGeocacheLocation(String cacheID) throws Exception {
        try {
            CommentedConfigurationNode root = privateConfig.load();

            CommentedConfigurationNode unparsed = root.getNode("caches",cacheID,"location");
            UUID worldUUID = UUID.fromString(unparsed.getNode("worldUUID").getString());
            Optional<World> crossFingers = Sponge.getServer().getWorld(worldUUID);
            if(!crossFingers.isPresent())
                return null;
            return new Location<World>(crossFingers.get()
                    ,unparsed.getNode("position","x").getInt()
                    ,unparsed.getNode("position","y").getInt()
                    ,unparsed.getNode("position","z").getInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
