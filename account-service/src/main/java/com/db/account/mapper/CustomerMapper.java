package com.db.account.mapper;

import com.db.account.entity.AccountEntity;
import com.db.account.entity.CustomerEntity;
import com.digital.backend.model.Customer;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    CustomerEntity toEntity(Customer customerDto, @Context CycleAvoidingMappingContext mappingContext);

    @Mapping(target = "accountNumbers", expression = "java(extractAccountNumber(customerEntity.getCustomerAccounts()))")
    Customer toDto(CustomerEntity customerEntity, @Context CycleAvoidingMappingContext mappingContext);

    default List<Long> extractAccountNumber(List<AccountEntity> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return new ArrayList<>();
        }
        return accounts.stream()
                .map(AccountEntity::getAccountNumber)
                .toList();
    }


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "customerAccounts", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Named("updateFromDtoPartially")
    void updateFromDtoPartially(
            Customer customerDto, @MappingTarget CustomerEntity customerEntity);
}
