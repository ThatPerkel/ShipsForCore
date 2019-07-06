package org.ships.vessel.common.flag;

import org.core.configuration.parser.Parser;

import java.util.Optional;

public class AltitudeLockFlag implements VesselFlag<Boolean> {

    protected boolean value;

    public AltitudeLockFlag(){
        this(false);
    }

    public AltitudeLockFlag(boolean value){
        this.value = value;
    }

    @Override
    public Optional<Boolean> getValue() {
        return Optional.of(this.value);
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public Parser<String, Boolean> getParser() {
        return Parser.STRING_TO_BOOLEAN;
    }

    @Override
    public String getId() {
        return "ships:altitude_lock";
    }

    @Override
    public String getName() {
        return "Altitude Lock";
    }
}
