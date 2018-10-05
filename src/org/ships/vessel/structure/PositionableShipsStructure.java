package org.ships.vessel.structure;

import org.core.world.position.BlockPosition;
import org.core.world.position.Positionable;
import org.core.world.position.block.BlockType;
import org.core.world.position.block.entity.TileEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public interface PositionableShipsStructure extends ShipsStructure, Positionable {

    @Override
    public BlockPosition getPosition();

    default Collection<BlockPosition> getAll(BlockType type){
        return Collections.unmodifiableCollection(getPositions(this::getPosition).stream().filter(p -> p.getBlockType().equals(type)).collect(Collectors.toSet()));
    }

    default Collection<BlockPosition> getAll(Class<? extends TileEntity> class1){
        return Collections.unmodifiableCollection(getPositions(this::getPosition).stream().filter(p -> p.getTileEntity().isPresent()).filter(p -> class1.isInstance(p.getTileEntity().get())).collect(Collectors.toSet()));
    }
}
