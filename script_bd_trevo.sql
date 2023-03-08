CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE MANAGER CASCADE;
DROP TABLE CLIENT CASCADE;
DROP TABLE PRODUCT CASCADE;
DROP TABLE ORDER_ITEM;
DROP TABLE CULTURE;
DROP TABLE IMAGE;

CREATE TABLE CLIENT (
	client_id uuid DEFAULT uuid_generate_v4 (),
	client_name varchar(50) NOT NULL,
	email varchar(50) NOT NULL UNIQUE,
	phone varchar(30) NOT NULL UNIQUE,
	PRIMARY KEY (client_id)
);

CREATE TABLE MANAGER (
	manager_id uuid DEFAULT uuid_generate_v4 (),
	username varchar(50) NOT NULL UNIQUE,
	manager_password varchar NOT NULL,
	PRIMARY KEY (manager_id)
);

CREATE TABLE PRODUCT (
	product_id uuid DEFAULT uuid_generate_v4 (),
	manager_id uuid NOT NULL,
	product_name varchar(50) NOT NULL UNIQUE,
	area_size real,
	description text NOT NULL,
	create_at date DEFAULT CURRENT_DATE NOT NULL,
	status boolean NOT NULL,
	PRIMARY KEY (product_id),
	FOREIGN KEY (manager_id) REFERENCES MANAGER (manager_id)
);



CREATE TABLE ORDER_ITEM (
	order_item_id uuid DEFAULT uuid_generate_v4 (),
	product_id uuid NOT NULL,
	client_id uuid NOT NULL,
	quantity int NOT NULL,
	PRIMARY KEY (order_item_id),
	FOREIGN KEY (product_id) REFERENCES PRODUCT (product_id),
	FOREIGN KEY (client_id) REFERENCES CLIENT (client_id)
);

CREATE TABLE CULTURE (
	culture_id uuid DEFAULT uuid_generate_v4 (),
	product_id uuid NOT NULL,
	culture_name varchar(50) NOT NULL,
	PRIMARY KEY (culture_id),
	FOREIGN KEY (product_id) REFERENCES PRODUCT (product_id)
);

CREATE TABLE IMAGE (
	image_id uuid DEFAULT uuid_generate_v4 (),
	product_id uuid NOT NULL,
	img bytea NOT NULL,
	PRIMARY KEY (image_id),
	FOREIGN KEY (product_id) REFERENCES PRODUCT (product_id)
);

INSERT INTO MANAGER (username, manager_password) VALUES ('admin','$2a$12$//unR2JTZisNleMtuYYFS.Ewh.vXLqK.gwpC9JyAfCKpLqhLOvlnW');

	




