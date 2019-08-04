package org.ships.vessel.common.types.typical;

import org.core.CorePlugin;
import org.core.utils.Identifable;
import org.core.vector.types.Vector3Int;
import org.core.world.boss.ServerBossBar;
import org.core.world.position.BlockPosition;
import org.core.world.position.ExactPosition;
import org.core.world.position.Position;
import org.core.world.position.block.entity.LiveTileEntity;
import org.core.world.position.block.entity.sign.LiveSignTileEntity;
import org.ships.algorthum.movement.BasicMovement;
import org.ships.config.blocks.BlockListable;
import org.ships.exceptions.MoveException;
import org.ships.exceptions.NoLicencePresent;
import org.ships.movement.Movement;
import org.ships.plugin.ShipsPlugin;
import org.ships.vessel.common.assits.CrewStoredVessel;
import org.ships.vessel.common.assits.FileBasedVessel;
import org.ships.vessel.common.assits.SignBasedVessel;
import org.ships.vessel.common.assits.WritableNameVessel;
import org.ships.vessel.common.flag.VesselFlag;
import org.ships.vessel.sign.LicenceSign;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ShipsVessel extends SignBasedVessel, org.ships.vessel.common.assits.VesselRequirement, CrewStoredVessel, WritableNameVessel, BlockListable, FileBasedVessel, Identifable {

    Map<String, String> getExtraInformation();
    Collection<VesselFlag<?>> getFlags();

    @Override
    default LiveSignTileEntity getSign() throws NoLicencePresent {
        Optional<LiveTileEntity> opTile = this.getPosition().getTileEntity();
        if(!opTile.isPresent()){
            throw new NoLicencePresent(this);
        }
        LiveTileEntity tile = opTile.get();
        if(!(tile instanceof LiveSignTileEntity)){
            throw new NoLicencePresent(this);
        }
        LiveSignTileEntity sign = (LiveSignTileEntity)tile;
        LicenceSign licenceSign = ShipsPlugin.getPlugin().get(LicenceSign.class).get();
        if(!licenceSign.isSign(sign)){
            throw new NoLicencePresent(this);
        }
        return sign;
    }

    @Override
    default ShipsVessel setName(String name) throws NoLicencePresent{
        getSign().setLine(2, CorePlugin.buildText(name));
        return this;
    }

    @Override
    default String getName() {
        try {
            return getSign().getLine(2).get().toPlain();
        } catch (NoLicencePresent e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    default void moveTowards(int x, int y, int z, BasicMovement movement, ServerBossBar bar) throws MoveException {
        Movement.MidMovement.ADD_TO_POSITION.move(this, x, y, z, movement, bar);
    }

    @Override
    default void moveTowards(Vector3Int vector, BasicMovement movement, ServerBossBar bar) throws MoveException{
        Movement.MidMovement.ADD_TO_POSITION.move(this, vector, movement, bar);
    }

    @Override
    default void moveTo(Position<? extends Number> location, BasicMovement movement, ServerBossBar bar) throws MoveException{
        BlockPosition position = location instanceof BlockPosition ? (BlockPosition)location : ((ExactPosition)location).toBlockPosition();
        Movement.MidMovement.TELEPORT_TO_POSITION.move(this, position, movement, bar);
    }

    @Override
    default void rotateRightAround(Position<? extends Number> location, BasicMovement movement, ServerBossBar bar) throws MoveException{
        BlockPosition position = location instanceof BlockPosition ? (BlockPosition)location : ((ExactPosition)location).toBlockPosition();
        Movement.MidMovement.ROTATE_RIGHT_AROUND_POSITION.move(this, position, movement, bar);
    }

    @Override
    default void rotateLeftAround(Position<? extends Number> location, BasicMovement movement, ServerBossBar bar) throws MoveException{
        BlockPosition position = location instanceof BlockPosition ? (BlockPosition)location : ((ExactPosition)location).toBlockPosition();
        Movement.MidMovement.ROTATE_LEFT_AROUND_POSITION.move(this, position, movement, bar);
    }

    @Override
    default String getId(){
        return getType().getId() + "." + getName().toLowerCase();
    }
}
