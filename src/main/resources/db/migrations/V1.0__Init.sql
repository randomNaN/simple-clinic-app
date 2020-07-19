CREATE TABLE clinic (
    clinic_id SERIAL PRIMARY KEY,
    clinic_name VARCHAR(60) NOT NULL,
    clinic_addr VARCHAR(100),
    clinic_phone VARCHAR(11)
);

CREATE TABLE diagnostic (
    diagnostic_id SERIAL PRIMARY KEY,
    diagnostic_name VARCHAR(60) NOT NULL,
    diagnostic_cat VARCHAR(60)
);

CREATE TABLE price_list (
    clinic_id SERIAL REFERENCES clinic(clinic_id) ON DELETE CASCADE,
    diagnostic_id SERIAL REFERENCES diagnostic(diagnostic_id) ON DELETE CASCADE,
    price NUMERIC NOT NULL CHECK(price > 0),
    PRIMARY KEY (clinic_id, diagnostic_id)
);