------------------------------------------------------------------------------------------------------------------------
-- Table definitions
------------------------------------------------------------------------------------------------------------------------

CREATE TABLE Property (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_Property_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	id varchar(50) NOT NULL,
	name varchar(50) NULL,
	description varchar(255) NULL,
	dataType varchar(255) NOT NULL,
	options varchar(4096) NULL,
	formatPattern varchar(50) NULL,
	unitOfMeasure varchar(50) NULL,
	disabled tinyint NULL,
	optimisticLock int NOT NULL DEFAULT 0,
	CONSTRAINT UQ_Property_id UNIQUE (id)
)

CREATE TABLE PropertyValue (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_PropertyValue_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	property_pk int NOT NULL,
	ownerPk bigint NOT NULL,
	ownerClass varchar(255) NOT NULL,
	value varchar(8000) NULL,
	description varchar(8000) NULL,
	numericValue float NULL,
	disabled tinyint NULL,
	optimisticLock int NOT NULL DEFAULT 0,
	CONSTRAINT UQ_PropertyValue_ownerPk_ownerClass_propertyPk UNIQUE (ownerPk, ownerClass, property_pk)
)

CREATE TABLE PropertyClass (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_PropertyClass_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	id varchar(50) NOT null,
	description varchar(255) NULL,
	disabled tinyint NULL,
	optimisticLock int NOT NULL DEFAULT 0,
	CONSTRAINT UQ_PropertyClass_id UNIQUE (id)
)

CREATE TABLE PropertyMembership (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_PropertyMembership_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
    property_pk int NOT NULL,
	propertyClass_pk int NOT NULL,
	indexNo smallint NULL,
	required tinyint null,
	editable tinyint null,
	showInCrosstab tinyint null,
	disabled tinyint NULL,
	optimisticLock int NOT NULL DEFAULT 0,
	CONSTRAINT UQ_PropertyMembership_propertyPk_propertyClassPk UNIQUE (property_pk, propertyClass_pk)
)

------------------------------------------------------------------------------------------------------------------------
-- Foreign keys, Constraints, indexes, etc.
------------------------------------------------------------------------------------------------------------------------

ALTER TABLE PropertyValue ADD CONSTRAINT FK_PropertyValue_propertyPk FOREIGN KEY (property_pk) REFERENCES Property (pk)
ALTER TABLE PropertyMembership ADD CONSTRAINT FK_PropertyMembership_propertyPk FOREIGN KEY (property_pk) REFERENCES Property (pk)
ALTER TABLE PropertyMembership ADD CONSTRAINT FK_PropertyMembership_propertyClass_pk FOREIGN KEY (propertyClass_pk) REFERENCES PropertyClass (pk)
CREATE unique INDEX idx_Property_id ON Property (id);
CREATE index fk_PropertyValue_Property ON PropertyValue (property_pk);
CREATE UNIQUE INDEX idx_PropertyValue_ownerPk_ownerClass_property_pk ON PropertyValue (ownerPk, ownerClass, property_pk);


------------------------------------------------------------------------------------------------------------------------
-- Changes
------------------------------------------------------------------------------------------------------------------------

--for testing
insert into property (id, datatype) values ('FOTOGRAF', 'java.lang.String');
insert into property (id, datatype) values ('SKAL-DIGITALISERES', 'java.lang.Boolean');
insert into PropertyValue (property_pk, ownerPk, ownerClass, value) values (1, 8971, 'LogistikkObjekt', 'Rune Sandersen');

--2013-01-29: Nytt felt for kortnavn på Property
alter table property add name varchar(50) null;

--2013-02-05: Innstramming av lovlige datatyper
update Property set datatype = 'java.lang.Long' where datatype = 'java.lang.Integer';
update Property set datatype = 'java.lang.Long' where datatype = 'java.lang.Short';
update Property set datatype = 'java.lang.Long' where datatype = 'java.lang.Byte';
update Property set datatype = 'java.lang.Double' where datatype = 'java.lang.Float';

--2013-02-06: Nytt felt for tallverdier
alter table PropertyValue add numericValue float null;

--2013-02-06: Konvertere eksisterende tallverdier: Boolske
update PropertyValue set numericValue = 1
where PropertyValue.property_pk in (select p.pk from Property p where p.datatype = 'java.lang.Boolean')
and value = 'true';

update PropertyValue set numericValue = 0
where PropertyValue.property_pk in (select p.pk from Property p where p.datatype = 'java.lang.Boolean')
and value = 'false';

 --2013-12-10 Fjern kolonner som ikke er i bruk - khol
ALTER TABLE PropertyValue DROP COLUMN parent_pk
ALTER TABLE PropertyValue DROP COLUMN path
ALTER TABLE PropertyValue DROP COLUMN override
ALTER TABLE Property DROP COLUMN optionDescriptions

--2013-02-19: Ny kolonne for deaktivering etter oppgradering av persistence-core 
alter table property add disabled tinyint null;
alter table propertyvalue add disabled tinyint null;

alter table PropertyValue alter column value varchar(8000) null

--2014-08-21: To tabeller for å håndtere gruppering av egenskaper (Properties)
CREATE TABLE PropertyClass (
	pk bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_PropertyClass_pk PRIMARY KEY,
	id varchar(50) NOT NULL CONSTRAINT UQ_PropertyClass_id UNIQUE,
	description varchar(255) NULL,
	disabled tinyint null,
	optimisticLock int not null default 0
);
CREATE TABLE Property_PropertyClass (
	property_pk bigint NOT NULL,
	propertyClass_pk bigint NOT NULL,
	optimisticLock int not null default 0,
	CONSTRAINT UQ_Property_PropertyClass_propertyPk_propertyClassPk UNIQUE (property_pk, propertyClass_pk)
);

--2014-08-26: Endret koblingstabellen Property_PropertyClass til å kunne håndtere krav for hver property (editerbar og påkrevd)
DROP TABLE Property_PropertyClass;

CREATE TABLE PropertyGroup (
    pk bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_PropertyGroup_pk PRIMARY KEY,
    property_pk bigint NOT NULL,
	propertyClass_pk bigint NOT NULL,
	required tinyint null,
	editable tinyint null,
	disabled tinyint null,
	optimisticLock int not null default 0,
	CONSTRAINT UQ_PropertyGroup_propertyPk_propertyClassPk UNIQUE (property_pk, propertyClass_pk)
);

--2014-08-28: Koblingstabellen har fått nytt navn til PropertyMembership
CREATE TABLE PropertyMembership (
    pk bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_PropertyMembership_pk PRIMARY KEY,
    property_pk bigint NOT NULL,
	propertyClass_pk bigint NOT NULL,
	required tinyint null,
	editable tinyint null,
	disabled tinyint null,
	optimisticLock int not null default 0,
	CONSTRAINT UQ_PropertyMembership_propertyPk_propertyClassPk UNIQUE (property_pk, propertyClass_pk)
);

--2015-01-13: Fant ut at noen har lagt inn description på PropertyValue
alter table PropertyValue add description varchar(255) NULL;
	
-- 2015-11-17: Styring av rekkefølge på egenskaper
alter TABLE PropertyMembership add indexNo smallint NULL;

-- 2016-07-25: New field to control which properties to show in tables
alter TABLE PropertyMembership add showInCrosstab tinyint null;
