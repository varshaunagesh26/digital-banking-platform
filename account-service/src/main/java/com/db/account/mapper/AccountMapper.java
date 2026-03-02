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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    AccountEntity toEntity(AccountDto accountDto, @Context CycleAvoidingMappingContext context);

    @Named("toDto")
    AccountDto toDto(AccountEntity accountEntity, @Context CycleAvoidingMappingContext context);


    /**
     * patch: type, bal, holder and branch
     *
     * @param accountDto
     * @param accountEntity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "accountHolder", qualifiedByName = "updateFromDtoPartially")
    @Mapping(target = "accountBranch", qualifiedByName = "updateFromDtoPartially")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Named("updateFromDtoPartially")
    public abstract void updateFromDtoPartially(
            AccountDto accountDto, @MappingTarget AccountEntity accountEntity);
}