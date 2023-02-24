CREATE TABLE test1217_person (
    oid VARCHAR(8) NOT NULL,
    name VARCHAR(60) NOT NULL,
    PRIMARY KEY (oid)
)
GO

CREATE TABLE test1217_base (
    oid VARCHAR(8) NOT NULL,
    name VARCHAR(60) NOT NULL,
    PRIMARY KEY (oid)
)
GO

CREATE TABLE test1217_extended (
    oid VARCHAR(8) NOT NULL,
    exnum VARCHAR(20) NOT NULL,
    PRIMARY KEY (oid)
)
GO
ALTER TABLE test1217_extended
ADD FOREIGN KEY (oid)
REFERENCES test1217_base (oid)
GO

CREATE TABLE test1217_product (
    oid VARCHAR(8) NOT NULL,
    code VARCHAR(12) NOT NULL,
    price NUMERIC(12,2) NOT NULL,
    company VARCHAR(8) NOT NULL,
    part VARCHAR(8) NOT NULL,
    PRIMARY KEY (oid)
)
GO
ALTER TABLE test1217_product
ADD FOREIGN KEY (company)
REFERENCES test1217_person (oid)
GO
ALTER TABLE test1217_product
ADD FOREIGN KEY (part)
REFERENCES test1217_base (oid)
GO
