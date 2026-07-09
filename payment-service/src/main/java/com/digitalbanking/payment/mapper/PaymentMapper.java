package com.digitalbanking.payment.mapper;


import com.digitalbanking.payment.dto.Payment;
import com.digitalbanking.payment.entity.PaymentEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
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

    Payment toDto(PaymentEntity payment, @Context CycleAvoidMappingContext context);
}
