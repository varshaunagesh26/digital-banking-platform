package com.db.transaction.client;

import com.digital.backend.exceptions.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class AccountServiceErrorDecoder implements ErrorDecoder  {

    private final ErrorDecoder errorDecoder = new Default();

    private final ObjectMapper mapper =
            new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .registerModule(new JavaTimeModule());

    @SneakyThrows
    @Override
    public Exception decode(String methodKey, Response response){
        Map<String, Object> map;
        try (InputStream bodyInputStream = response.body().asInputStream()) {
            map = mapper.readValue(bodyInputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }

        AccountServiceErrorResponse customError = mapper.convertValue(map, AccountServiceErrorResponse.class);
        ErrorResponse error = mapper.convertValue(map, ErrorResponse.class);

        return switch (response.status()) {
            case 400 -> handleBadRequest(customError, error, response);
            case 404 -> // Not found
                    new EntityNotFoundException(
                            StringUtils.isNotEmpty(error.getMessage())
                                    ? error.getMessage()
                                    : "Not found");
            case 409 -> // Conflict
                    handleConflict(customError, error);
            default -> handleDefaultError(customError, error, methodKey, response);
        };
    }

    private Exception handleBadRequest(AccountServiceErrorResponse customError, ErrorResponse error, Response response) throws IOException {
        if (hasInvalidInput(customError)) {
            return createInvalidEntityException(customError);
        } else if (hasBadRequestMessage(error)) {
            return new BadRequestException(error.getMessage());
        } else {
            String msg = new String(response.body().asInputStream().readAllBytes(), Charset.defaultCharset());
            return new BadRequestException(msg);
        }
    }

    private boolean hasInvalidInput(AccountServiceErrorResponse customError) {
        return Objects.nonNull(customError) && CollectionUtils.isNotEmpty(customError.getErrorMessage());
    }

    private InvalidEntityException createInvalidEntityException(AccountServiceErrorResponse customError) {
        InvalidEntityException invalidEntityException = new InvalidEntityException("");
        for (AccountServiceErrorModel errorModel : customError.getErrorMessage()) {
            String rejectedValueString = Optional.ofNullable(errorModel.getRejectedMessage())
                    .map(Object::toString)
                    .orElse("Unknown");
            invalidEntityException.addValidationError(errorModel.getFieldName(), rejectedValueString, errorModel.getMessageError());
        }
        return invalidEntityException;
    }

    private boolean hasBadRequestMessage(ErrorResponse error) {
        return Objects.nonNull(error) && StringUtils.isNotEmpty(error.getMessage());
    }

    private Exception handleConflict(AccountServiceErrorResponse customError, ErrorResponse error) {
        if (hasInvalidInput(customError)) {
            return new EntityAlreadyExistsException(customError.getErrorMessage().getFirst().getMessageError());
        }
        return new EntityAlreadyExistsException(error.getMessage());
    }

    private Exception handleDefaultError(AccountServiceErrorResponse customError, ErrorResponse error, String methodKey, Response response) throws IOException {
        if (StringUtils.isNotEmpty(error.getMessage())) {
            return new AccountServiceFeignException(error.getMessage());
        } else if (hasInvalidInput(customError)) {
            return new AccountServiceFeignException(mapper.writeValueAsString(customError.getErrorMessage()));
        } else {
            return errorDecoder.decode(methodKey, response);
        }
    }
}
