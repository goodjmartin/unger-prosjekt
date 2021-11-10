------------------------------------------------------------------------------------------------------------------------
-- Table definitions
------------------------------------------------------------------------------------------------------------------------

CREATE TABLE Attachment (
--mssql:
	[pk] bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_Attachment_pk PRIMARY KEY,
--mysql:
	pk bigint NOT NULL auto_increment PRIMARY KEY,
	
	ownerClass VARCHAR(256) NOT NULL,
	ownerPk BIGINT NOT NULL,
	filePath VARCHAR(256) NOT NULL,
	fileDescription VARCHAR(256) NOT NULL,
	changedBy VARCHAR(256) NOT NULL,
	fileName VARCHAR(256) NOT NULL,
	created datetime NOT NULL,
	disabled tinyint NULL,
	optimisticLock int NOT NULL DEFAULT 0
)

------------------------------------------------------------------------------------------------------------------------
-- Foreign keys, Constraints, indexes, etc.
------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------
-- Changes
------------------------------------------------------------------------------------------------------------------------

