package com.db.account.mapper;

import com.db.account.entity.AccountEntity;
import com.db.account.model.AccountDto;
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

   AccountEntity toEntity(AccountDto accountDto, @Context CycleAvoidingMappingContext context);

    AccountDto toDto(AccountEntity accountEntity, @Context CycleAvoidingMappingContext context);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber" , ignore = true)
    public abstract void updateFromDtoPartially(
            AccountDto accountDto, @MappingTarget AccountEntity accountEntity);
}




