package org.ships.vessel.common.types.typical.opship;

import org.core.configuration.ConfigurationFile;
import org.core.configuration.ConfigurationNode;
import org.core.world.position.impl.sync.SyncBlockPosition;
import org.core.world.position.block.entity.sign.LiveSignTileEntity;
import org.core.world.position.block.entity.sign.SignTileEntity;
import org.ships.movement.autopilot.FlightPath;
import org.ships.vessel.common.assits.AirType;
import org.ships.vessel.common.assits.FlightPathType;
import org.ships.vessel.common.types.ShipType;
import org.ships.vessel.common.types.typical.AbstractShipsVessel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Deprecated
public class OPShip extends AbstractShipsVessel implements AirType, FlightPathType {

    protected FlightPath flightPath;

    public OPShip(LiveSignTileEntity licence, ShipType origin) {
        super(licence, origin);
    }

    public OPShip(SignTileEntity ste, SyncBlockPosition position, ShipType origin){
        super(ste, position, origin);
    }

    @Override
    public Map<ConfigurationNode, Object> serialize(ConfigurationFile file) {
        return new HashMap<>();
    }

    @Override
    public AbstractShipsVessel deserializeExtra(ConfigurationFile file) {
        return this;
    }

    @Override
    public Map<String, String> getExtraInformation() {
        return new HashMap<>();
    }

    @Override
    public Optional<FlightPath> getFlightPath() {
        return Optional.ofNullable(this.flightPath);
    }

    @Override
    public FlightPathType setFlightPath(FlightPath path) {
        this.flightPath = path;
        return this;
    }
}
