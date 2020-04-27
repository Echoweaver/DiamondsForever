package net.echoweaver.diamondsforever;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = DiamondsForever.MODID, name = DiamondsForever.NAME, version = DiamondsForever.VERSION)
public class DiamondsForever
{
    public static final String MODID = "diamondsforever";
    public static final String NAME = "Diamonds Are Forever";
    public static final String VERSION = "${version}";

    @Mod.Instance(MODID)
    public static DiamondsForever INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register( new EntityInteractHandler() );
    }
}
