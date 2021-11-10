------------------------------------------------------------------------------------------------------------------------
-- Table definitions
------------------------------------------------------------------------------------------------------------------------
create table FrontPageCard(
  pk bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_FrontPageCard_pk PRIMARY KEY, --mssql
--mysql:	pk bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
	person_pk bigint NOT NULL,
	panel varchar(255) NOT NULL,
	args varchar(255) NULL,
	indexNo smallint NULL,
	disabled tinyint null,
	optimisticLock int not null default 0
);

ALTER TABLE FrontPageCard ADD CONSTRAINT FK_FrontPageCard_personPk FOREIGN KEY (person_pk) REFERENCES Person (pk);