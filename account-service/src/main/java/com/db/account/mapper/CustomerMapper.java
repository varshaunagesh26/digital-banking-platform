package com.db.account.mapper;

import com.db.account.entity.CustomerEntity;
import com.db.account.model.CustomerDto;
import org.mapstruct.*;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = CONSTRUCTOR,
        uses = {
                AccountMapper.class,
                BranchMapper.class
        },
        imports = {
                CycleAvoidingMappingContext.class
        }
)
public interface CustomerMapper {

    CustomerEntity toEntity(CustomerDto customerDto, @Context CycleAvoidingMappingContext mappingContext);
    CustomerDto toDto(CustomerEntity customerEntity,  @Context CycleAvoidingMappingContext mappingContext);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    public abstract void updatePartiallyFromDto(
            CustomerDto customerDto, @MappingTarget CustomerEntity customerEntity);
}
