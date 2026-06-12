package com.digital.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Generated;

import java.beans.ConstructorProperties;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountServiceErrorResponse {

    private List<AccountServiceErrorModel> errorMessage;

    @Generated
    public static AccountServiceResponseBuilder builder() {
        return new AccountServiceResponseBuilder();
    }

    @Generated
    public List<AccountServiceErrorModel> getErrorMessage() {
        return this.errorMessage;
    }

    @Generated
    public void setErrorMessage(final List<AccountServiceErrorModel> errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AccountServiceErrorResponse)) {
            return false;
        } else {
            AccountServiceErrorResponse other = (AccountServiceErrorResponse) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$errorMessage = this.getErrorMessage();
                Object other$errorMessage = other.getErrorMessage();
                if (this$errorMessage == null && other$errorMessage != null) {
                    return false;
                } else if (!this$errorMessage.equals(other$errorMessage)) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof AccountServiceErrorResponse;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $errorMessage = this.getErrorMessage();
        result = result * PRIME + ($errorMessage == null ? 43 : $errorMessage.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AccountServiceErrorResponse(errorMessage =" + String.valueOf(this.getErrorMessage()) + ")";
    }

    @Generated
    public AccountServiceErrorResponse() {
    }

    @ConstructorProperties({"errorMessage"})
    @Generated
    public AccountServiceErrorResponse(final List<AccountServiceErrorModel> errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Generated
    public static class AccountServiceResponseBuilder {

        @Generated
        private List<AccountServiceErrorModel> errorMessage;

        @Generated
        AccountServiceResponseBuilder() {
        }

        @Generated
        public AccountServiceResponseBuilder errorMessage(final List<AccountServiceErrorModel> errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        @Generated
        public AccountServiceErrorResponse build() {
            return new AccountServiceErrorResponse(this.errorMessage);
        }

        @Generated
        public String toString() {
            return "AccountServiceErrorResponse.AccountServiceResponseBuilder(errorMessage=" + String.valueOf(this.errorMessage) + ")";
        }
    }
}
