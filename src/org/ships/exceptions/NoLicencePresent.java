package org.ships.exceptions;

import org.ships.vessel.common.types.Vessel;

import java.io.IOException;

public class NoLicencePresent extends IOException {

    public NoLicencePresent(Vessel vessel){
        super("Could not find Ships licence sign at " + vessel.getPosition().getX() + ", " + vessel.getPosition().getY() + ", " + vessel.getPosition().getZ());
    }
}
