CREATE TABLE oe.products (
    product_id SERIAL PRIMARY KEY,
    product_name varchar(40) NOT NULL,
    supplier_id smallint,
    category_id smallint,
    quantity_per_unit varchar(20),
    unit_price real,
    units_in_stock smallint,
    units_on_order smallint,
    reorder_level smallint,
    discontinued integer NOT NULL,
	created_date  timestamp default current_timestamp,
    modified_date timestamp,
    FOREIGN KEY (supplier_id) REFERENCES oe.suppliers (supplier_id) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (category_id) REFERENCES oe.categories (category_id) ON UPDATE CASCADE ON DELETE CASCADE
);