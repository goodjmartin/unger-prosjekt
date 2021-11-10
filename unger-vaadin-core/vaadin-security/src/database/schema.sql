------------------------------------------------------------------------------------------------------------------------
-- Table definitions
------------------------------------------------------------------------------------------------------------------------

CREATE TABLE [dbo].[Person] (
	[pk] bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_Person_pk PRIMARY KEY,
	[id] varchar(50) NOT NULL CONSTRAINT UQ_Person_id UNIQUE,
	[name] varchar(50) NULL,
	[disabled] tinyint null,
	[password] varchar(50) NULL,
	[email] varchar(255) NULL,
	[loginFailures] SMALLINT NOT NULL DEFAULT (0),
	[roles] text NULL,
	[DTYPE] varchar (255) NULL,
	[optimisticLock] int NOT NULL CONSTRAINT DF_Person_optimisticLock DEFAULT (0)
) ON [PRIMARY]

CREATE TABLE [dbo].[AccessRole] (
	[pk] bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_AccessRole_pk PRIMARY KEY,
	[id] varchar(50) NOT NULL CONSTRAINT UQ_AccessRole_id UNIQUE,
	[description] varchar(50) NULL,
	[disabled] tinyint null,
	[optimisticLock] int NOT NULL CONSTRAINT DF_AccessRole_optimisticLock DEFAULT (0)
) ON [PRIMARY]

CREATE TABLE [dbo].[Person_AccessRole] (
	[person_pk] bigint NOT NULL,
	[accessRole_pk] bigint NOT NULL,
	[optimisticLock] int NOT NULL CONSTRAINT DF_Person_AccessRole_optimisticLock DEFAULT (0),
	CONSTRAINT UQ_Person_AccessRole_personPk_accessRolePk UNIQUE (person_pk, accessRole_pk)
) ON [PRIMARY]

CREATE TABLE [dbo].[AccessRole_AccessFunction] (
	[accessRole_pk] bigint NOT NULL,
	[accessFunctionIds] varchar(100) NOT NULL,
	[optimisticLock] int NOT NULL CONSTRAINT DF_AccessRole_AccessFunction_optimisticLock DEFAULT (0),
	CONSTRAINT UQ_AccessRole_AccessFunction_accessRolePk_accessFunctionIds UNIQUE (accessRole_pk, accessFunctionIds)
) ON [PRIMARY]

CREATE TABLE [dbo].[PersonnelClass] (
	[pk] bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_PersonnelClass_pk PRIMARY KEY,
	[id] varchar(50) NOT NULL CONSTRAINT UQ_PersonnelClass_id UNIQUE,
	[name] varchar(255) NULL,
	[description] varchar(255) NULL,
	[disabled] tinyint null,
	[optimisticLock] int NOT NULL CONSTRAINT DF_PersonnelClass_optimisticLock DEFAULT (0)
) ON [PRIMARY]

CREATE TABLE [dbo].[Person_PersonnelClass] (
	[person_pk] bigint NOT NULL,
	[personnelClass_pk] bigint NOT NULL,
	[optimisticLock] int NOT NULL CONSTRAINT DF_Person_PersonnelClass_optimisticLock DEFAULT (0),
	CONSTRAINT UQ_Person_PersonnelClass_personPk_personnelClassPk UNIQUE (person_pk, personnelClass_pk)
) ON [PRIMARY]


------------------------------------------------------------------------------------------------------------------------
-- Foreign keys, Constraints, indexes, etc.
------------------------------------------------------------------------------------------------------------------------

ALTER TABLE [dbo].[Person_AccessRole] ADD CONSTRAINT FK_Person_AccessRole_personPk FOREIGN KEY ([person_pk]) REFERENCES [dbo].[Person] ([pk])
ALTER TABLE [dbo].[Person_AccessRole] ADD CONSTRAINT FK_Person_AccessRole_accessRolePk FOREIGN KEY ([accessRole_pk]) REFERENCES [dbo].[AccessRole] ([pk])
ALTER TABLE [dbo].[AccessRole_AccessFunction] ADD CONSTRAINT FK_AccessRole_AccessFunction_accessRolePk FOREIGN KEY ([accessRole_pk]) REFERENCES [dbo].[AccessRole] ([pk])
ALTER TABLE [dbo].[Person_PersonnelClass] ADD CONSTRAINT FK_Person_PersonnelClass_personPk FOREIGN KEY ([person_pk]) REFERENCES [dbo].[Person] ([pk])
ALTER TABLE [dbo].[Person_PersonnelClass] ADD CONSTRAINT FK_Person_PersonnelClass_personnelClassPk FOREIGN KEY ([personnelClass_pk]) REFERENCES [dbo].[PersonnelClass] ([pk])


------------------------------------------------------------------------------------------------------------------------
-- Changes
------------------------------------------------------------------------------------------------------------------------

--2013-02-19: Ny kolonne for deaktivering etter oppgradering av persistence-core
alter table Person add disabled tinyint null;
alter table AccessRole add disabled tinyint null;
alter table PersonnelClass add disabled tinyint null;

--2013-03-16: Ny kolonne for navn 
alter table PersonnelClass add name varchar(255) null;
alter table PersonnelClass modify optimisticLock int not null DEFAULT 0;

ALTER TABLE [dbo].[Person_PersonnelClass] ADD CONSTRAINT FK_Person_PersonnelClass_personPk FOREIGN KEY ([person_pk]) REFERENCES [dbo].[Person] ([pk])
ALTER TABLE [dbo].[Person_PersonnelClass] ADD CONSTRAINT FK_Person_PersonnelClass_personnelClassPk FOREIGN KEY ([personnelClass_pk]) REFERENCES [dbo].[PersonnelClass] ([pk])
ALTER TABLE [dbo].[Person] ADD CONSTRAINT UQ_Person_id UNIQUE (id)

--2015-03-30: Nytt felt for e-post, for glemt-passord-funksjon
ALTER TABLE Person ADD email varchar(255) NULL
