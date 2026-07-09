package com.db.transaction.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Context;
import org.mapstruct.TargetType;

import java.util.IdentityHashMap;
import java.util.Map;

/***
 * A type to be used as {@link Context} parameter to track cycles in graphs
 *
 * <p>Depending on the actual use case,the 2 methods below can also be changed to only accept
 * certain argument types, e.g. base class of graph nodes, avoiding the need to capture any other
 * object that wouldn't necessarily result in cycles</p>
 */
public class CycleAvoidMappingContext {
    private final Map<Object, Object> knownInstances = new IdentityHashMap<>();

    @BeforeMapping
    public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
        return targetType.cast(knownInstances.get(source));
    }

    @BeforeMapping
    public void storeMappedInstance(Object source, @TargetType Object target) {
        knownInstances.put(source, target);
    }
}
