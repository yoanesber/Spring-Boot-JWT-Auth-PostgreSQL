package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

/**
 * EShowType is an enumeration that represents the type of show.
 * It can be either a MOVIE or a TV_SHOW.
 * This enum is used to categorize shows in the application.
 * The @Getter and @Setter annotations from Lombok are used to generate getters and setters for the enum values.
 */

public enum EShowType {
    MOVIE,
    TV_SHOW
}
