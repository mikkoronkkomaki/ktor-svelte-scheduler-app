CREATE SCHEMA IF NOT EXISTS scheduler;

CREATE TYPE appointment_status AS ENUM ('reserved', 'cancelled', 'done', 'no-show');


CREATE TABLE scheduler.appointment
(
    id     SERIAL  NOT NULL
        CONSTRAINT pk_appointment
            PRIMARY KEY,
    description VARCHAR NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status appointment_status NOT NULL,
    client_id integer,
    specialist_id integer
);

CREATE TABLE scheduler.client
(
    id     SERIAL  NOT NULL
        CONSTRAINT pk_client
            PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL
);

CREATE TABLE scheduler.specialist
(
    id     SERIAL  NOT NULL
        CONSTRAINT pk_specialist
            PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    profession VARCHAR NOT NULL
);

