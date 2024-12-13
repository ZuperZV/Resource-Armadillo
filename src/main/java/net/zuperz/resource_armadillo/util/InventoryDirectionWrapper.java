package net.zuperz.resource_armadillo.util;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class InventoryDirectionWrapper {
    public Map<Direction, Optional<WrappedHandler>> directionsMap;

    public InventoryDirectionWrapper(Lazy<IItemHandler> lazyHandler, InventoryDirectionEntry... entries) {
        directionsMap = new HashMap<>();

        IItemHandler handler = lazyHandler.get();

        if (handler instanceof IItemHandlerModifiable modifiableHandler) {
            for (var x : entries) {
                directionsMap.put(x.direction,
                        Optional.of(((Supplier<WrappedHandler>) () -> new WrappedHandler(modifiableHandler,
                                (i) -> Objects.equals(i, x.slotIndex),
                                (i, s) -> x.canInsert)).get())
                );
            }
        } else {
            throw new IllegalArgumentException("Handler must be of type IItemHandlerModifiable");
        }
    }
}