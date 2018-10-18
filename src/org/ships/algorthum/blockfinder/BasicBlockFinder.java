package org.ships.algorthum.blockfinder;

import org.core.world.position.BlockPosition;
import org.ships.algorthum.Algorthum;
import org.ships.vessel.common.types.ShipsVessel;
import org.ships.vessel.structure.PositionableShipsStructure;

import java.util.Optional;

public interface BasicBlockFinder extends Algorthum {

    PositionableShipsStructure getConnectedBlocks(BlockPosition position);
    void getConnectedBlocksOvertime(BlockPosition position, OvertimeBlockFinderUpdate runAfterFullSearch);
    int getBlockLimit();
    BasicBlockFinder setBlockLimit(int limit);
    Optional<ShipsVessel> getConnectedVessel();
    BasicBlockFinder setConnectedVessel(ShipsVessel vessel);

}