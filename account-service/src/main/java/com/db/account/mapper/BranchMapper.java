package com.db.account.mapper;

import com.db.account.entity.BranchEntity;
import com.db.account.model.BranchDto;
import org.mapstruct.*;
import org.yaml.snakeyaml.constructor.Constructor;
import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = CONSTRUCTOR,
        uses = {
                AccountMapper.class,
                CustomerMapper.class,
        },
        imports = {
                CycleAvoidingMappingContext.class
        }
)
public interface BranchMapper {

    BranchEntity toEntity(BranchDto branchDto, @Context CycleAvoidingMappingContext context);
    BranchDto toDto(BranchEntity branchEntity, @Context CycleAvoidingMappingContext context);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchCode", ignore = true)
    @Mapping(target = "branchIFSC", ignore = true)
    public abstract void updatePartiallyFromDto(
            BranchDto branchDto,@MappingTarget BranchEntity branchEntity);
}
