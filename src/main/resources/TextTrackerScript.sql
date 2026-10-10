drop schema if exists Tax_payers_db;


create schema Tax_payers_db;
use Tax_payers_db;


CREATE TABLE users (
    user_id INT auto_increment PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    area VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(100),
    pin_code VARCHAR(6),
    mobile_number VARCHAR(10) unique
);



CREATE TABLE transactions (
    txn_id INT PRIMARY KEY auto_increment,
    organization_name VARCHAR(100),
    amount DOUBLE,
    tax_amount DOUBLE,
    type VARCHAR(50),
    txn_date DATE,
    financial_year VARCHAR(20),
    
    user_id INT,
    CONSTRAINT fk_transaction_user
        FOREIGN KEY (user_id)
        REFERENCES uers(user_id)
        ON DELETE CASCADE
);

CREATE TABLE form_90c (
    form_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    mobile_number VARCHAR(15),
    financial_year VARCHAR(9),
    status VARCHAR(50),

    user_id INT UNIQUE,
    CONSTRAINT fk_form90c_user
            FOREIGN KEY (user_id) 
            REFERENCES users(user_id)
            ON DELETE CASCADE
);

CREATE TABLE form_90c_transactions (
    txn_id INT AUTO_INCREMENT PRIMARY KEY,
    organization_name VARCHAR(150),
    amount DOUBLE,
    tax_amount DOUBLE,
    type VARCHAR(50),
    txn_date DATE,

    form_id INT NOT NULL,
    CONSTRAINT fk_form90c_txn
            FOREIGN KEY (form_id) 
            REFERENCES form_90c(form_id)
            ON DELETE CASCADE
);

CREATE TABLE documents (
    document_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150),
    data LONGBLOB,

    form_id INT,
    CONSTRAINT fk_documents_form90c
        FOREIGN KEY (form_id)
        REFERENCES form_90c(form_id)
        ON DELETE CASCADE
);