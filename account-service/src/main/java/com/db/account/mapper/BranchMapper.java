package com.db.account.mapper;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.BranchEntity;
import com.db.account.model.BranchDto;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    BranchEntity toEntity(BranchDto branchDto, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "accountNumbers", expression = "java(extractAccountNumber(branchEntity.getAccountEntities()))")
    BranchDto toDto(BranchEntity branchEntity, @Context CycleAvoidingMappingContext context);

    default List<Long> extractAccountNumber(List<AccountEntity> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return new ArrayList<>();
        }
        return accounts.stream()
                .map(AccountEntity::getAccountNumber)
                .collect(Collectors.toList());
    }


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchCode", ignore = true)
    @Mapping(target = "branchIFSC", ignore = true)
    @Mapping(target = "accountEntities", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Named("updateFromDtoPartially")
    void updateFromDtoPartially(
            BranchDto branchDto, @MappingTarget BranchEntity branchEntity);
}
