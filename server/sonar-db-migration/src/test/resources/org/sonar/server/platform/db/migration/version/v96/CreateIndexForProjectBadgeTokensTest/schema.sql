CREATE TABLE "PROJECT_BADGE_TOKEN"(
    "UUID" CHARACTER VARYING(40) NOT NULL,
    "TOKEN" CHARACTER VARYING(255) NOT NULL,
    "PROJECT_UUID" CHARACTER VARYING(40) NOT NULL,
    "CREATED_AT" BIGINT NOT NULL,
    "UPDATED_AT" BIGINT NOT NULL
);
ALTER TABLE "PROJECT_BADGE_TOKEN" ADD CONSTRAINT "PK_PROJECT_BADGE_TOKEN" PRIMARY KEY("UUID");
