INSERT INTO client (first_name, last_name)
VALUES ('Marjaana', 'Sulkeerinen'),
       ('Pertti', 'Keinonen');

INSERT INTO specialist (first_name, last_name, profession)
VALUES ('Irmeli ', 'Römppönen', 'mekaanikko'),
       ('Jorma', 'Kuikelo', 'hammashygienisti');


INSERT INTO appointment (description, start_time, end_time, status, client_id, specialist_id)
VALUES ('tyristorin vaihto', '2027-1-1  16:00:00+00'::timestamp, '2027-1-1  18:00:00+00'::timestamp, 'reserved',
        (SELECT id from client where last_name = 'Sulkeerinen'),
        (SELECT id from specialist where last_name = 'Römppönen'));


INSERT INTO appointment (description, start_time, end_time, status, client_id, specialist_id)
VALUES ('ienten rappaus', '2021-1-1  12:00:00+00'::timestamp, '2021-1-1  13:00:00+00'::timestamp, 'no-show',
        (SELECT id from client where last_name = 'Keinonen'),
        (SELECT id from specialist where last_name = 'Kuikelo'))