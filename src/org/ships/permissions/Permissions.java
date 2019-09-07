package org.ships.permissions;

import org.ships.vessel.common.types.ShipType;

public interface Permissions {

    String ABSTRACT_SHIP_MOVE = "ships.move.own";
    String ABSTRACT_SHIP_MOVE_OTHER = "ships.move.other";
    String ABSTRACT_SHIP_MAKE = "ships.make";
    String SHIP_REMOVE_OTHER = "ships.remove.other";
    String CMD_INFO  = "ships.cmd.info";
    String CMD_BLOCK_INFO = "ships.cmd.blockinfo";
    String CMD_SHIPTYPE_CREATE = "ships.cmd.shiptype.create";
    String CMD_CONFIG_SET = "ships.cmd.config.set";

    static String getMakePermission(ShipType type){
        return ABSTRACT_SHIP_MAKE + "." + type.getId().replace(":", ".").toLowerCase();
    }

    static String getMovePermission(ShipType type){
        return ABSTRACT_SHIP_MOVE + "." + type.getId().replace(":", ".").toLowerCase();
    }

    static String getOtherMovePermission(ShipType type){
        return ABSTRACT_SHIP_MOVE_OTHER + "." + type.getId().replace(":", ".").toLowerCase();
    }
}
