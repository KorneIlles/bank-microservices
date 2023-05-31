CREATE TABLE client (
                        id SERIAL PRIMARY KEY,
                        first_name VARCHAR(255) NOT NULL,
                        last_name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        date_of_birth DATE NOT NULL,
                        address VARCHAR(255) NOT NULL,
                        phone_number VARCHAR(255) NOT NULL,
                        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        is_active BOOLEAN NOT NULL,
                        UNIQUE(email)
);

CREATE TABLE account (
                         id SERIAL PRIMARY KEY,
                         account_number VARCHAR(255) NOT NULL,
                         balance NUMERIC(10, 2) NOT NULL,
                         is_active BOOLEAN NOT NULL,
                         account_type VARCHAR(255) NOT NULL,
                         iban VARCHAR(255) NOT NULL,
                         start_date DATE NOT NULL,
                         end_date DATE,
                         account_name VARCHAR(255) NOT NULL,
                         client_id BIGINT REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE card (
                      id SERIAL PRIMARY KEY,
                      cardholder_name VARCHAR(255) NOT NULL,
                      pan VARCHAR(255) NOT NULL,
                      bank_name VARCHAR(255) NOT NULL,
                      card_type VARCHAR(255) NOT NULL,
                      cvv VARCHAR(255) NOT NULL,
                      account_id BIGINT REFERENCES account(id)
);