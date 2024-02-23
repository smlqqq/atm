CREATE TABLE IF NOT EXISTS atms (
                                   id SERIAL PRIMARY KEY,
                                   name VARCHAR(25) UNIQUE ,
                                   location VARCHAR(255) NOT NULL
);