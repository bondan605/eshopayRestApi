CREATE SEQUENCE IF NOT EXISTS oe.carts_cart_id_seq;


CREATE TABLE IF NOT EXISTS oe.carts
(
    cart_id integer NOT NULL DEFAULT nextval('oe.carts_cart_id_seq'::regclass),
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    user_id integer,
    CONSTRAINT pk_carts PRIMARY KEY (cart_id),
    CONSTRAINT uq_carts_user UNIQUE (user_id),
    created_date  timestamp default current_timestamp,
    modified_date timestamp
);


CREATE TABLE IF NOT EXISTS oe.cart_items
(
    cart_id integer NOT NULL,
    product_id integer NOT NULL,
    quantity smallint,
    unit_price real,
    discount decimal(2,2),
	created_date  timestamp default current_timestamp,
    modified_date timestamp,
    CONSTRAINT pk_cart_items PRIMARY KEY (cart_id, product_id),
    CONSTRAINT fk_cart_cart_items FOREIGN KEY (cart_id)
        REFERENCES oe.carts (cart_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE SCHEMA IF NOT EXISTS person;

CREATE TABLE IF NOT EXISTS person.users(
   user_id SERIAL PRIMARY KEY,
   user_name varchar(15),
   user_email varchar(80) unique,
   user_password varchar(125),
   user_handphone varchar(15) unique,
   created_date timestamp default current_timestamp,
   modified_date timestamp
);

CREATE SCHEMA IF NOT EXISTS fintech;

drop table if EXISTS fintech.fintechs;

create table fintech.fintechs(
   fint_code varchar(3) constraint pk_fint_code primary key,
   fint_name varchar(125),
   fint_short_name varchar(15),
   fint_type varchar(10) check (fint_type in ('BANK','FINTECH','E-Wallet','P-GateAway')),
   created_date timestamp default current_timestamp,
   modified_date timestamp
);

CREATE TABLE IF NOT EXISTS oe.order_details (
    order_id smallint NOT NULL,
    product_id smallint NOT NULL,
    unit_price real NOT NULL,
    quantity smallint NOT NULL,
    discount real NOT NULL
);

CREATE TABLE IF NOT EXISTS oe.orders
(
    order_id SERIAL PRIMARY KEY,
    order_date date,
    required_date date,
    shipped_date date,
    ship_via smallint,
    freight real,
    ship_name varchar(40),
    total_discount decimal(5,2),
    total_amount decimal(8,2),
    payment_type varchar(15),
    transac_no varchar(25),
    transac_date timestamp without time zone,
    location_id integer,
    user_id integer,
    fint_code varchar(3),
	created_date  timestamp default current_timestamp,
    modified_date timestamp,
    CONSTRAINT fk_order_location FOREIGN KEY (location_id)
        REFERENCES hr.locations (location_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id)
        REFERENCES person.users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_order_bank_code FOREIGN KEY (fint_code)
        REFERENCES fintech.fintechs (fint_code) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_orders_shippers FOREIGN KEY (ship_via)
        REFERENCES oe.shippers (shipper_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT orders_payment_type_check CHECK (payment_type::text = ANY (ARRAY['DEBIT'::varchar::text, 'CREDIT'::varchar::text, 'QRIS'::varchar::text, 'TRANSFER'::varchar::text]))
);