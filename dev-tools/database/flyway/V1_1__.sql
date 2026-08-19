CREATE SCHEMA IF NOT EXISTS scheduler;

CREATE TABLE scheduler.appointment
(
    id     SERIAL  NOT NULL
        CONSTRAINT pk_appointment
            PRIMARY KEY,
    description VARCHAR NOT NULL,
    done BOOLEAN DEFAULT FALSE
);