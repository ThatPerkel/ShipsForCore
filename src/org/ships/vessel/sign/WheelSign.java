package org.ships.vessel.sign;

import org.core.CorePlugin;
import org.core.entity.living.human.player.LivePlayer;
import org.core.source.viewer.CommandViewer;
import org.core.text.Text;
import org.core.text.TextColours;
import org.core.world.position.BlockPosition;
import org.core.world.position.block.BlockTypes;
import org.core.world.position.block.entity.sign.SignTileEntity;
import org.core.world.position.block.entity.sign.SignTileEntitySnapshot;
import org.ships.algorthum.movement.BasicMovement;
import org.ships.exceptions.MoveException;
import org.ships.exceptions.load.UnableToFindLicenceSign;
import org.ships.movement.result.FailedMovement;
import org.ships.plugin.ShipsPlugin;
import org.ships.vessel.common.loader.shipsvessel.ShipsUpdateBlockLoader;
import org.ships.vessel.common.types.Vessel;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WheelSign implements ShipsSign {

    @Override
    public boolean isSign(SignTileEntity entity) {
        Optional<Text> opValue = entity.getLine(0);
        return opValue.isPresent() && opValue.get().equals(getFirstLine());
    }

    @Override
    public SignTileEntitySnapshot changeInto(SignTileEntity sign) {
        SignTileEntitySnapshot stes = sign.getSnapshot();
        stes.setLine(0, CorePlugin.buildText(TextColours.YELLOW + "[Wheel]"));
        stes.setLine(1, CorePlugin.buildText(TextColours.RED + "\\\\||//"));
        stes.setLine(2, CorePlugin.buildText(TextColours.RED + "==||=="));
        stes.setLine(3, CorePlugin.buildText(TextColours.RED + "//||\\\\"));
        return stes;
    }

    @Override
    public Text getFirstLine() {
        return CorePlugin.buildText(TextColours.YELLOW + "[Wheel]");
    }

    @Override
    public boolean onPrimaryClick(LivePlayer player, BlockPosition position){
        if(player.isSneaking()){
            return false;
        }
        try{
            Vessel vessel = new ShipsUpdateBlockLoader(position).load();
            BasicMovement movement = ShipsPlugin.getPlugin().getConfig().getDefaultMovement();
            try{
                vessel.rotateLeftAround(vessel.getPosition(), movement);
            }catch (MoveException e){
                sendErrorMessage(player, e.getMovement(), e.getMovement().getValue().orElse(null));
            }
        }catch (UnableToFindLicenceSign e1){
            player.sendMessage(CorePlugin.buildText(TextColours.RED + e1.getReason()));
            e1.getFoundStructure().getPositions().forEach(bp -> bp.setBlock(BlockTypes.BEDROCK.get().getDefaultBlockDetails(), player));
            CorePlugin.createSchedulerBuilder().setDelay(5).setDelayUnit(TimeUnit.SECONDS).setExecutor(() -> e1.getFoundStructure().getPositions().forEach(bp -> bp.resetBlock(player))).build(ShipsPlugin.getPlugin()).run();
        }catch (IOException e){
            player.sendMessage(CorePlugin.buildText(TextColours.RED + e.getMessage()));
            return false;
        }
        return false;
    }

    @Override
    public boolean onSecondClick(LivePlayer player, BlockPosition position) {
        if(player.isSneaking()){
            return false;
        }
        try{
            Vessel vessel = new ShipsUpdateBlockLoader(position).load();
            BasicMovement movement = ShipsPlugin.getPlugin().getConfig().getDefaultMovement();
            try{
                vessel.rotateRightAround(vessel.getPosition(), movement);
            }catch (MoveException e){
                sendErrorMessage(player, e.getMovement(), e.getMovement().getValue().orElse(null));
            }
        }catch (UnableToFindLicenceSign e1){
            e1.getFoundStructure().getPositions().forEach(bp -> bp.setBlock(BlockTypes.BEDROCK.get().getDefaultBlockDetails(), player));
            CorePlugin.createSchedulerBuilder().setDelay(5).setDelayUnit(TimeUnit.SECONDS).setExecutor(() -> e1.getFoundStructure().getPositions().forEach(bp -> bp.resetBlock(player))).build(ShipsPlugin.getPlugin()).run();
        }catch (IOException e){
            player.sendMessage(CorePlugin.buildText(TextColours.RED + e.getMessage()));
            return false;
        }
        return false;
    }

    @Override
    public String getId() {
        return "ships:wheel_sign";
    }

    @Override
    public String getName() {
        return "Wheel Sign";
    }

    private <T> void sendErrorMessage(CommandViewer viewer, FailedMovement<T> movement, Object value){
        movement.sendMessage(viewer, (T)value);
    }
}
