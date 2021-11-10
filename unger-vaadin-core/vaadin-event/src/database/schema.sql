------------------------------------------------------------------------------------------------------------------------
-- Table definitions
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE [dbo].[EventType] (
	[pk] bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_EventType_pk PRIMARY KEY,
	[ownerClass] VARCHAR(256) NOT NULL,
	[id] VARCHAR(50) NOT NULL,
	[disabled] tinyint NULL,
	[optimisticLock] int NOT NULL CONSTRAINT DF_EventType_optimisticLock DEFAULT (0)
) ON [PRIMARY]

CREATE TABLE [dbo].[Event] (
	[pk] bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_Event_pk PRIMARY KEY,
	[eventype_pk] bigint NOT NULL,
	[ownerClass] VARCHAR(256) NOT NULL,
	[ownerPk] BIGINT NOT NULL,
	[changedBy] VARCHAR(256) NOT NULL,
	[eventDetail] VARCHAR(8000) NOT NULL,
	[created] datetime NOT NULL,
	[disabled] tinyint NULL,
	[optimisticLock] int NOT NULL CONSTRAINT DF_Event_optimisticLock DEFAULT 0
) ON [PRIMARY]

------------------------------------------------------------------------------------------------------------------------
-- Foreign keys, Constraints, indexes, etc.
------------------------------------------------------------------------------------------------------------------------
ALTER TABLE Event ADD CONSTRAINT fk_Event_EventType FOREIGN KEY (eventype_pk) REFERENCES EventType (pk)

------------------------------------------------------------------------------------------------------------------------
-- Changes
------------------------------------------------------------------------------------------------------------------------
--2015.01.22
ALTER TABLE Event ALTER COLUMN eventDetail VARCHAR (512) NOT NULL;

--2017.05.03
ALTER table Event ALTER COLUMN eventDetail VARCHAR (8000) NOT NULL