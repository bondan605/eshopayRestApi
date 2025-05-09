ALTER TABLE oe.categories 
ALTER COLUMN category_id SET NOT NULL,
ALTER COLUMN category_id ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE oe.suppliers 
ALTER COLUMN supplier_id SET NOT NULL,
ALTER COLUMN supplier_id ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE oe.shippers 
ALTER COLUMN shipper_id SET NOT NULL,
ALTER COLUMN shipper_id ADD GENERATED ALWAYS AS IDENTITY;

alter sequence oe.categories_category_id_seq
restart with 9 increment by 1;

alter sequence oe.shippers_shipper_id_seq
restart with 7 increment by 1;

alter sequence oe.suppliers_supplier_id_seq
restart with 30 increment by 1;
