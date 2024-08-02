DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS location;
CREATE TABLE location (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(20) NOT NULL,
    address VARCHAR(250),
    phone_number VARCHAR(10)
);
CREATE TABLE item (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    location_id INTEGER NOT NULL,
    FOREIGN KEY(location_id) REFERENCES location(id)
);
insert into location(id, state, address, phone_number) values (1, 'Ciudad de Mexico', 'Palacio Regional, edificio 200, planta baja,Centro, Cuauhtemoc, Codigo Postal 06060, Ciudad de Mexico', 50934900);
insert into location(id, state, address, phone_number) values (2, 'Queretaro', 'Blvd. Bernardo Quintana Arrioja Sn, Villas del Parque, 86140 Santiago de Queretaro, Qro.', 4422194149);
insert into location(id, state, address, phone_number) values (3, 'Nuevo Leon', 'Ignacio Allende 200, Centro, 74720 Monterrey, N.L.', 8120205600);
insert into location(id, state, address, phone_number) values (4, 'Yucatan', 'C. 35 SN, Centro, 87000 Merida, Yuc.', 9999303100);
insert into location(id, state) values (5, 'Jalisco');
insert into location(id, state) values (6, 'Puebla');
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
insert into item (name, description, location_id) values ('Mouse', 'Ergonomic mouse for comfortable computing',4);
insert into item (name, description, location_id) values ('Keyboard', 'Mechanical keyboard for precise typing',4);
insert into item (name, description, location_id) values ('Printer', 'Print high-quality documents and photos with this printer',4);
insert into item (name, description, location_id) values ('Scanner', 'Scan documents and images easily with this scanner',5);
insert into item (name, location_id) values ('External Hard Drive',5);
insert into item (name, location_id) values ('Power Bank',5);
insert into item (name, location_id) values ('Headphones',6);
insert into item (name, location_id) values ('Controller',6);
insert into item (name, description, location_id) values ('Laptop', 'Powerful laptop for work and gaming',3);
insert into item (name, description, location_id) values ('Smartphone', 'Latest model smartphone with high-resolution camera',2);
insert into item (name, description, location_id) values ('Wireless Headphones', 'Noise-canceling headphones for immersive audio',2);
insert into item (name, description, location_id) values ('Smart Watch', 'Track your fitness and stay connected with your smartwatch',1);
insert into item (name, description, location_id) values ('Gaming Console', 'Experience the latest games with this powerful console',1);
insert into item (name, description, location_id) values ('Television', 'Enjoy stunning visuals on this high-definition TV',6);
insert into item (name, description, location_id) values ('Tablet', 'Portable tablet for entertainment and productivity',6);
insert into item (name, description, location_id) values ('Camera', 'Capture lifes moments with this versatile camera',6);
insert into item (name, description, location_id) values ('Speaker', 'Fill your space with rich sound with this wireless speaker',6);
insert into item (name, description, location_id) values ('Router', 'Reliable Wi-Fi coverage for your home or office',2);
insert into item (name, description, location_id) values ('Laptop', 'Powerful laptop for work and gaming',2);
insert into item (name, description, location_id) values ('Smartphone', 'Latest model smartphone with high-resolution camera',6);
insert into item (name, description, location_id) values ('Wireless Headphones', 'Noise-canceling headphones for immersive audio',6);
insert into item (name, description, location_id) values ('Smart Watch', 'Track your fitness and stay connected with your smartwatch',5);
insert into item (name, description, location_id) values ('Gaming Console', 'Experience the latest games with this powerful console',5);
insert into item (name, description, location_id) values ('Television', 'Enjoy stunning visuals on this high-definition TV',5);
insert into item (name, description, location_id) values ('Tablet', 'Portable tablet for entertainment and productivity',5);
insert into item (name, description, location_id) values ('Camera', 'Capture lifes moments with this versatile camera',3);
insert into item (name, description, location_id) values ('Speaker', 'Fill your space with rich sound with this wireless speaker',4);
insert into item (name, description, location_id) values ('Router', 'Reliable Wi-Fi coverage for your home or office',4);