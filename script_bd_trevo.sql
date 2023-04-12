CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE ACCOUNT CASCADE;
DROP TABLE PRODUCT CASCADE;
DROP TABLE ORDER_ITEM;
DROP TABLE CULTURE;
DROP TABLE IMAGE;

CREATE TABLE ACCOUNT (
	account_id uuid DEFAULT uuid_generate_v4 (),
	account_name varchar(50) NOT NULL,
	email varchar(50) NOT NULL UNIQUE,
	account_password varchar NOT NULL,
	create_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	account_role varchar(50) NOT NULL,
	PRIMARY KEY (account_id)
);


CREATE TABLE PRODUCT (
	product_id uuid DEFAULT uuid_generate_v4 (),
	account_id uuid NOT NULL,
	product_name varchar(50) NOT NULL UNIQUE,
	area_size real,
	price real,
	description text NOT NULL,
	create_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	status varchar(50) NOT NULL,
	category varchar(50) NOT NULL,
	PRIMARY KEY (product_id),
	FOREIGN KEY (account_id) REFERENCES ACCOUNT (account_id)
);


CREATE TABLE ORDER_ITEM (
	order_item_id uuid DEFAULT uuid_generate_v4 (),
	product_id uuid NOT NULL,
	account_id uuid NOT NULL,
	quantity int NOT NULL,
	PRIMARY KEY (order_item_id),
	FOREIGN KEY (product_id) REFERENCES PRODUCT (product_id),
	FOREIGN KEY (account_id) REFERENCES ACCOUNT (account_id)
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

INSERT INTO ACCOUNT (email, account_name, account_password, account_role) VALUES ('admin@gmail.com', 'admin','$2a$12$//unR2JTZisNleMtuYYFS.Ewh.vXLqK.gwpC9JyAfCKpLqhLOvlnW', 'ADMINISTRADOR');
	




