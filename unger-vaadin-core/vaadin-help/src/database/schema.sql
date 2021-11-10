------------------------------------------------------------------------------------------------------------------------
-- Table definitions
------------------------------------------------------------------------------------------------------------------------

--MySQL
CREATE TABLE IF NOT EXISTS HelpText (
	pk INT NOT NULL AUTO_INCREMENT,
	id VARCHAR(255) NOT NULL unique,
	text VARCHAR(8192) NULL,
	optimisticLock int not null default 0,
	disabled tinyint null,
	PRIMARY KEY (pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--MSSQL
CREATE TABLE HelpText (
	[pk] bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_HelpText_pk PRIMARY KEY,
	[id] VARCHAR(255) NOT NULL UNIQUE,
	[text] VARCHAR(8000) NULL,
	[optimisticLock] int NOT NULL DEFAULT 0,
	[disabled] tinyint NULL,
)

------------------------------------------------------------------------------------------------------------------------
-- Foreign keys, Constraints, indexes, etc.
------------------------------------------------------------------------------------------------------------------------


------------------------------------------------------------------------------------------------------------------------
-- Changes
------------------------------------------------------------------------------------------------------------------------
