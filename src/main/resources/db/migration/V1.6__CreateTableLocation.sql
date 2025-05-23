CREATE SCHEMA IF NOT EXISTS hr;

CREATE TABLE hr.regions (
	region_id SERIAL PRIMARY KEY,
	region_name CHARACTER VARYING (25)
);

CREATE TABLE hr.countries (
	country_id CHARACTER (2) PRIMARY KEY,
	country_name CHARACTER VARYING (40),
	region_id INTEGER NOT NULL,
	FOREIGN KEY (region_id) REFERENCES hr.regions (region_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE hr.locations (
	location_id SERIAL PRIMARY KEY,
	street_address CHARACTER VARYING (40),
	postal_code CHARACTER VARYING (12),
	city CHARACTER VARYING (30) NOT NULL,
	state_province CHARACTER VARYING (25),
	country_id CHARACTER (2) NOT NULL,
	FOREIGN KEY (country_id) REFERENCES hr.countries (country_id) ON UPDATE CASCADE ON DELETE CASCADE
);

alter table hr.regions
add column created_date timestamp default current_timestamp,
add column modified_date timestamp;

alter table hr.countries
add column created_date timestamp default current_timestamp,
add column modified_date timestamp;

alter table hr.locations
add column created_date timestamp default current_timestamp,
add column modified_date timestamp;