package org.ships.vessel.common.types.typical.airship;

import org.array.utils.ArrayUtils;
import org.core.CorePlugin;
import org.core.config.ConfigurationStream;
import org.core.inventory.item.ItemTypes;
import org.core.inventory.item.type.post.ItemTypes1V13;
import org.core.platform.Plugin;
import org.core.world.position.block.BlockType;
import org.core.world.position.block.BlockTypes;
import org.core.world.position.block.entity.sign.SignTileEntity;
import org.core.world.position.block.grouptype.versions.BlockGroups1V13;
import org.core.world.position.impl.sync.SyncBlockPosition;
import org.ships.plugin.ShipsPlugin;
import org.ships.vessel.common.assits.FuelSlot;
import org.ships.vessel.common.assits.shiptype.CloneableShipType;
import org.ships.vessel.common.assits.shiptype.FuelledShipType;
import org.ships.vessel.common.assits.shiptype.SpecialBlockShipType;
import org.ships.vessel.common.types.ShipType;
import org.ships.vessel.common.types.typical.AbstractShipType;

import java.io.File;

public class AirshipType extends AbstractShipType<Airship> implements CloneableShipType<Airship>, SpecialBlockShipType<Airship>, FuelledShipType<Airship> {

    public AirshipType(){
        this("Airship", new File(ShipsPlugin.getPlugin().getShipsConigFolder(), "/Configuration/ShipType/Airship." + CorePlugin.getPlatform().getConfigFormat().getFileType()[0]));
    }

    public AirshipType(String name, File file){
        this(ShipsPlugin.getPlugin(), name, CorePlugin.createConfigurationFile(file, CorePlugin.getPlatform().getConfigFormat()), BlockTypes.AIR.get());
    }

    public AirshipType(Plugin plugin, String displayName, ConfigurationStream.ConfigurationFile file, BlockType... types) {
        super(plugin, displayName, file, types);
    }

    public boolean isUsingBurner(){
        return this.file.getBoolean(BURNER_BLOCK).get();
    }

    @Override
    public CloneableShipType<Airship> cloneWithName(File file, String name) {
        return new AirshipType(name, file);
    }

    @Override
    public CloneableShipType<Airship> getOriginType() {
        return ShipType.AIRSHIP;
    }

    @Override
    protected void createDefault(ConfigurationStream.ConfigurationFile file) {
        this.file.set(BURNER_BLOCK, true);
        this.file.set(SPECIAL_BLOCK_PERCENT, 60.0f);
        this.file.set(SPECIAL_BLOCK_TYPE, ArrayUtils.ofSet(BlockGroups1V13.WOOL.getGrouped()));
        this.file.set(FUEL_CONSUMPTION, 1);
        this.file.set(FUEL_SLOT, FuelSlot.BOTTOM);
        this.file.set(FUEL_TYPES, ArrayUtils.ofSet(ItemTypes.COAL, ItemTypes1V13.CHARCOAL));
        this.file.set(MAX_SPEED, 10);
        this.file.set(ALTITUDE_SPEED, 5);
    }

    @Override
    public Airship createNewVessel(SignTileEntity ste, SyncBlockPosition bPos) {
        return new Airship(this, ste, bPos);
    }
}
