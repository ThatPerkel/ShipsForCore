package org.ships.vessel.common.types;

import org.core.CorePlugin;
import org.core.entity.LiveEntity;
import org.core.schedule.Scheduler;
import org.core.schedule.unit.TimeUnit;
import org.core.utils.Bounds;
import org.core.vector.type.Vector2;
import org.core.vector.type.Vector3;
import org.core.world.ChunkExtent;
import org.core.world.Extent;
import org.core.world.direction.Direction;
import org.core.world.direction.FourFacingDirection;
import org.core.world.position.Positionable;
import org.core.world.position.block.BlockType;
import org.core.world.position.block.BlockTypes;
import org.core.world.position.impl.BlockPosition;
import org.core.world.position.impl.sync.SyncBlockPosition;
import org.core.world.position.impl.sync.SyncPosition;
import org.ships.config.configuration.ShipsConfig;
import org.ships.movement.MovementContext;
import org.ships.plugin.ShipsPlugin;
import org.ships.vessel.common.flag.VesselFlag;
import org.ships.vessel.structure.PositionableShipsStructure;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Vessel extends Positionable<BlockPosition> {

    String getName();
    PositionableShipsStructure getStructure();
    void setStructure(PositionableShipsStructure pss);
    ShipType<? extends Vessel> getType();

    <T extends VesselFlag<?>> Optional<T> get(Class<T> clazz);
    <T> Vessel set(Class<? extends VesselFlag<T>> flag, T value);
    Vessel set(VesselFlag<?> flag);

    int getMaxSpeed();
    int getAltitudeSpeed();

    Optional<Integer> getMaxSize();
    int getMinSize();

    Vessel setMaxSize(Integer size);
    Vessel setMinSize(Integer size);

    Vessel setMaxSpeed(int speed);
    Vessel setAltitudeSpeed(int speed);

    void moveTowards(int x, int y, int z, MovementContext context, Consumer<Throwable> exception);
    void moveTowards(Vector3<Integer> vector, MovementContext context, Consumer<Throwable> exception);
    void moveTo(SyncPosition<? extends Number> location, MovementContext context, Consumer<Throwable> exception);
    void rotateRightAround(SyncPosition<? extends Number> location, MovementContext context, Consumer<Throwable> exception);
    void rotateLeftAround(SyncPosition<? extends Number> location, MovementContext context, Consumer<Throwable> exception);

    void setLoading(boolean check);
    boolean isLoading();

    void save();

    @Override
    default SyncBlockPosition getPosition(){
        return getStructure().getPosition();
    }

    default <V, F extends VesselFlag<V>> Optional<V> getValue(Class<F> flagClass){
         Optional<F> opFlag = get(flagClass);
         if (opFlag.isPresent()){
             return opFlag.get().getValue();
         }
         return Optional.empty();
    }

    default Collection<LiveEntity> getEntities(){
        return getEntities(e -> true);
    }

    default <X extends LiveEntity> Collection<X> getEntities(Class<X> clazz){
        return (Collection<X>) getEntities(clazz::isInstance);
    }

    default Collection<LiveEntity> getEntities(Predicate<LiveEntity> check){
        Bounds<Integer> bounds = this.getStructure().getBounds();
        Set<LiveEntity> entities = new HashSet<>();
        this.getStructure().getChunks().stream().map(Extent::getEntities).forEach(entities::addAll);
        entities = entities.stream().filter(e -> {
            Optional<SyncBlockPosition> opTo = e.getAttachedTo();
            if(!opTo.isPresent()){
                return false;
            }
            return bounds.contains(opTo.get().getPosition());
        }).collect(Collectors.toSet());
        Collection<SyncBlockPosition> blocks = getStructure().getPositions();
        return entities.stream()
                .filter(check)
                .filter(e -> {
                    Optional<SyncBlockPosition> opBlock = e.getAttachedTo();
                    return opBlock.filter(syncBlockPosition -> blocks.parallelStream().anyMatch(b -> b.equals(syncBlockPosition))).isPresent();
                })
                .collect(Collectors.toSet());

    }

    default void getEntitiesOvertime(int limit, Predicate<LiveEntity> predicate, Consumer<LiveEntity> single, Consumer<Collection<LiveEntity>> output){
        System.out.println("Getting entities overtime");
        ShipsConfig config = ShipsPlugin.getPlugin().getConfig();
        Set<LiveEntity> entities = new HashSet<>();
        List<LiveEntity> entities2 = new ArrayList<>();
        Set<ChunkExtent> chunks = this.getStructure().getChunks();
        chunks.forEach(c -> entities2.addAll(c.getEntities()));

        Scheduler sched = CorePlugin.createSchedulerBuilder().setDisplayName("Ignore").setDelay(0).setDelayUnit(TimeUnit.MINECRAFT_TICKS).setExecutor(() -> {}).build(ShipsPlugin.getPlugin());
        double fin = entities2.size() / (double)limit;
        if(fin != ((int)fin)){
            fin++;
        }
        if (fin == 0) {
            output.accept(entities);
            return;
        }
        Collection<SyncBlockPosition> pss = getStructure().getPositions();
        System.out.println("Sorting schedule. Structure: " + pss.size() + "| Chunks: " + chunks.size() + "| EntityCount: " + entities2.size());
        for(int A = 0; A < fin; A++){
            final int B = A;
            sched = CorePlugin.createSchedulerBuilder().setDisplayName("\tentity getter " + A).setDelay(1).setDelayUnit(TimeUnit.MINECRAFT_TICKS).setExecutor(() -> {
                int c = (B*limit);
                for(int to = 0; to < limit; to++) {
                    if((c + to) >= entities2.size()){
                        break;
                    }
                    LiveEntity e = entities2.get(c + to);
                    if (!predicate.test(e)) {
                        continue;
                    }
                    Optional<SyncBlockPosition> opPosition = e.getAttachedTo();
                    if (!opPosition.isPresent()) {
                        continue;
                    }
                    if (pss.stream().anyMatch(b -> b.equals(opPosition.get()))) {
                        single.accept(e);
                        entities.add(e);
                    }else if(!e.isOnGround()){
                        SyncBlockPosition bPos = e.getPosition().toBlockPosition();
                        if (pss.stream().noneMatch(b -> bPos.isInLineOfSight(b.getPosition(), FourFacingDirection.DOWN))){
                            continue;
                        }
                        single.accept(e);
                        entities.add(e);
                    }
                }
                if(B == 0){
                    output.accept(entities);
                }
            }).setToRunAfter(sched).build(ShipsPlugin.getPlugin());
        }
        sched.run();
    }

    default Integer[] getVesselArea(){
        Collection<SyncBlockPosition> blocks = this.getStructure().getPositions();
        Integer minX = null, minZ = null, maxX = null, maxZ = null;
        for (SyncBlockPosition block : blocks) {
            Integer posX = block.getX();
            Integer posZ = block.getZ();
            if (minX == null){
                minX = maxX = posX;
                minZ = maxZ = posZ;
            } else {
                if (posX < minX)
                    minX = posX;

                if (posZ < minZ)
                    minZ = posZ;

                if (posX > maxX)
                    minX = posX;

                if (posZ > maxZ)
                    minZ = posZ;
            }
        }
        return new Integer[]{minX, minZ, maxX, maxZ};
    }

    default void rotateAnticlockwiseAround(SyncPosition<? extends Number> location, MovementContext context, Consumer<Throwable> exception){
        this.rotateRightAround(location, context, exception);
    }

    default void rotateClockwiseAround(SyncPosition<? extends Number> location, MovementContext context, Consumer<Throwable> exception){
        this.rotateLeftAround(location, context, exception);
    }

    default boolean isInWater(){
        return !getWaterLevel().isPresent();
    }

    default <T> Optional<Integer> getWaterLevel(Function<T, BlockPosition> function, Collection<T> collection){
        Map<Vector2<Integer>, Integer> height = new HashMap<>();
        Direction[] directions = FourFacingDirection.getFourFacingDirections();
        for (T value : collection){
            BlockPosition position = function.apply(value);
            for(Direction direction : directions){
                BlockType type = position.getRelative(direction).getBlockType();
                if(type.equals(BlockTypes.WATER.get())){
                    Vector2<Integer> vector = Vector2.valueOf(position.getX() + direction.getAsVector().getX(), position.getZ() + direction.getAsVector().getZ());
                    if(height.containsKey(vector)){
                        if(height.getOrDefault(vector, -1) < position.getY()) {
                            height.replace(vector, position.getY());
                            continue;
                        }
                    }
                    height.put(vector, position.getY());
                }
            }
        }
        if(height.isEmpty()){
            return Optional.empty();
        }
        Map<Integer, Integer> mean = new HashMap<>();
        height.values().forEach(value -> {
            if(mean.containsKey(value)){
                mean.replace(value, mean.get(value) + 1);
            }else{
                mean.put(value, 1);
            }
        });
        Map.Entry<Integer, Integer> best = null;
        for(Map.Entry<Integer, Integer> entry : mean.entrySet()){
            if(best == null){
                best = entry;
                continue;
            }
            if(best.getValue() < entry.getValue()){
                best = entry;
            }
        }
        if(best == null){
            return Optional.empty();
        }
        return Optional.of(best.getKey());
    }

    default Optional<Integer> getWaterLevel(){
        PositionableShipsStructure pss = getStructure();
        return getWaterLevel(p -> p, pss.getPositions());
    }

}
