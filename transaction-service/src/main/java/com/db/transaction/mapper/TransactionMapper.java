package com.db.transaction.mapper;

import com.db.transaction.entity.TransactionEntity;
import com.digital.backend.model.Transaction;
import org.mapstruct.*;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = CONSTRUCTOR,
        imports = {
                CycleAvoidMappingContext.class
        }
)
public interface TransactionMapper {

    @Mapping( target = "id", ignore = true)
    TransactionEntity toEntity(com.digital.backend.model.Transaction transaction, @Context CycleAvoidMappingContext context);

    Transaction toDto(TransactionEntity transaction, @Context CycleAvoidMappingContext context);
}
