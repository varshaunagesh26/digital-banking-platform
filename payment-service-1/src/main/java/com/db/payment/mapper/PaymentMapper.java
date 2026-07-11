package com.db.payment.mapper;


import com.digital.backend.model.paymentservice.Payment;
import com.db.payment.entity.PaymentEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = CONSTRUCTOR,
        imports = {
                CycleAvoidMappingContext.class
        }
)
public interface PaymentMapper {

    @Mapping(target = "paymentAccountInfo.fromAccountNumber", source = "fromAccountNumber")
    @Mapping(target = "paymentAccountInfo.fromIFSCCode", source = "fromIFSCCode")
    @Mapping(target = "paymentAccountInfo.toAccountNumber", source = "toAccountNumber")
    @Mapping(target = "paymentAccountInfo.toIFSCCode", source = "toIFSCCode")
    Payment toDto(PaymentEntity payment, @Context CycleAvoidMappingContext context);
}
