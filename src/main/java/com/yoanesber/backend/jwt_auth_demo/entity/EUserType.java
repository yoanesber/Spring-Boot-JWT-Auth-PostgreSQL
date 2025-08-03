package com.yoanesber.backend.jwt_auth_demo.entity;

/**
 * EUserType is an enumeration that represents the type of user account.
 * It can be either a SERVICE_ACCOUNT or a USER_ACCOUNT.
 * This enum is used to categorize user accounts in the application.
 * The @Getter and @Setter annotations from Lombok are used to generate getters and setters for the enum values.
 */

public enum EUserType {
    SERVICE_ACCOUNT,
    USER_ACCOUNT
}
