package com.db.account.mapper;

import com.db.account.entity.AccountEntity;
import com.digital.backend.model.Account;
import org.mapstruct.*;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = CONSTRUCTOR,
        uses = {
                BranchMapper.class,
                CustomerMapper.class
        },
        imports = {
                CycleAvoidingMappingContext.class
        }
)
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    AccountEntity toEntity(Account accountDto, @Context CycleAvoidingMappingContext context);

    @Named("toDto")
    Account toDto(AccountEntity accountEntity, @Context CycleAvoidingMappingContext context);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "accountHolder", qualifiedByName = "updateFromDtoPartially")
    @Mapping(target = "accountBranch", qualifiedByName = "updateFromDtoPartially")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Named("updateFromDtoPartially")
    void updateFromDtoPartially(
            Account accountDto, @MappingTarget AccountEntity accountEntity);
}