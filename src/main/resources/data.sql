DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS location;
CREATE TABLE location (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(20) NOT NULL,
    address VARCHAR(250),
    phone_number BIGINT
);
CREATE TABLE item (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    location_id BIGINT NOT NULL,
    FOREIGN KEY(location_id) REFERENCES location(id)
);
insert into location(id, state, address, phone_number) values (1,'Ciudad de Mexico', 'Palacio Regional, edificio 200, planta baja,Centro, Cuauhtemoc, Codigo Postal 06060, Ciudad de Mexico', 50934900);
insert into location(id, state, address, phone_number) values (2,'Queretaro', 'Blvd. Bernardo Quintana Arrioja Sn, Villas del Parque, 86140 Santiago de Queretaro, Qro.', 4422194149);
insert into location(id, state, address, phone_number) values (3,'Nuevo Leon', 'Ignacio Allende 200, Centro, 74720 Monterrey, N.L.', 8120205600);
insert into location(id, state, address, phone_number) values (4,'Yucatan', 'C. 35 SN, Centro, 87000 Merida, Yuc.', 9999303100);
insert into item (name, description, location_id) values ('Laptop', 'Powerful laptop for work and gaming',1);
insert into item (name, description, location_id) values ('Smartphone', 'Latest model smartphone with high-resolution camera',1);
insert into item (name, description, location_id) values ('Wireless Headphones', 'Noise-canceling headphones for immersive audio',1);
insert into item (name, description, location_id) values ('Smart Watch', 'Track your fitness and stay connected with your smartwatch',2);
insert into item (name, description, location_id) values ('Gaming Console', 'Experience the latest games with this powerful console',2);
insert into item (name, description, location_id) values ('Television', 'Enjoy stunning visuals on this high-definition TV',3);
insert into item (name, description, location_id) values ('Tablet', 'Portable tablet for entertainment and productivity',3);
insert into item (name, description, location_id) values ('Camera', 'Capture lifes moments with this versatile camera',3);
insert into item (name, description, location_id) values ('Speaker', 'Fill your space with rich sound with this wireless speaker',3);
insert into item (name, description, location_id) values ('Router', 'Reliable Wi-Fi coverage for your home or office',3);
insert into item (name, description, location_id) values ('Mouse', 'Ergonomic mouse for comfortable computing',3);
insert into item (name, description, location_id) values ('Keyboard', 'Mechanical keyboard for precise typing',4);
insert into item (name, description, location_id) values ('Printer', 'Print high-quality documents and photos with this printer',4);
insert into item (name, description, location_id) values ('Scanner', 'Scan documents and images easily with this scanner',4);
insert into item (name, description, location_id) values ('External Hard Drive', 'Expand your storage with this portable hard drive',4);
insert into item (name, description, location_id) values ('Power Bank', 'Keep your devices charged on the go with this power bank',4);
insert into item (name, description, location_id) values ('Headphones', 'Enjoy your music with these comfortable headphones',4);
insert into item (name, description, location_id) values ('Controller', 'Game with precision using this controller',4);
