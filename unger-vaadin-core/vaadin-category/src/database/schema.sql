------------------------------------------------------------------------------------------------------------------------
-- Table definitions
------------------------------------------------------------------------------------------------------------------------
create table Category(
--mssql:	pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_Category_pk PRIMARY KEY,
--mysql:	pk int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	id varchar(50) NOT NULL,
	name varchar(255) NOT NULL,
	indexNo smallint NOT NULL,
	owner varchar(255) NOT NULL,
	description varchar(8000) NULL,
	color int NULL,
	iconName varchar(255) NULL,
	disabled tinyint null,
	optimisticLock int not null default 0,
	CONSTRAINT UQ_Category_id_owner UNIQUE (id, owner)
)

------------------------------------------------------------------------------------------------------------------------
-- Foreign keys, Constraints, indexes, etc.
------------------------------------------------------------------------------------------------------------------------


------------------------------------------------------------------------------------------------------------------------
-- Changes
------------------------------------------------------------------------------------------------------------------------

--30.09.2014 MesStatus added and renamed to Category to make it more usable
sp_rename MesStatus, Category

--2015-07-24 IndexNo too tiny for country codes
alter table category modify column indexNo smallint not null;
