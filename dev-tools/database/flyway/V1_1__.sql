CREATE SCHEMA IF NOT EXISTS todo;

CREATE TABLE todo.task
(
    id     SERIAL  NOT NULL
        CONSTRAINT pk_task
            PRIMARY KEY,
    description VARCHAR NOT NULL,
    done BOOLEAN DEFAULT FALSE
);