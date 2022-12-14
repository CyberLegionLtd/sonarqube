  CREATE TABLE "ALM_SETTINGS"(
  "UUID" VARCHAR(40) NOT NULL,
  "ALM_ID" VARCHAR(40) NOT NULL,
  "KEE" VARCHAR(200) NOT NULL,
  "URL" VARCHAR(2000),
  "APP_ID" VARCHAR(80),
  "PRIVATE_KEY" VARCHAR(2000),
  "PAT" VARCHAR(2000),
  "UPDATED_AT" BIGINT NOT NULL,
  "CREATED_AT" BIGINT NOT NULL,
  "CLIENT_ID" VARCHAR(80),
  "CLIENT_SECRET" VARCHAR(80)
  );
  ALTER TABLE "ALM_SETTINGS" ADD CONSTRAINT "PK_ALM_SETTINGS" PRIMARY KEY("UUID");
  CREATE UNIQUE INDEX "UNIQ_ALM_SETTINGS" ON "ALM_SETTINGS"("KEE");