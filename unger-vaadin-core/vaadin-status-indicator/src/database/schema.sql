------------------------------------------------------------------------------------------------------------------------
-- Table definitions
------------------------------------------------------------------------------------------------------------------------

CREATE TABLE StatusIndicator (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_DashboardConfig_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	id varchar(50) NOT NULL UNIQUE,
	name varchar(255),
	disabled tinyint,
	optimisticLock int not null default 0
)
CREATE TABLE StatusLogEntry (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_DashboardConfig_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	message varchar(255),
	details varchar(6144),
	stateType int NOT NULL,
	created datetime NOT NULL,
	statusIndicator_pk int NOT NULL,
	disabled tinyint,
	optimisticLock int NOT NULL DEFAULT 0
)

--varsling for status-indikatorer
CREATE TABLE StatusIndicatorSubscription (
	pk bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_StatusIndicatorSubscription_pk PRIMARY KEY,
	id varchar(50) NOT NULL CONSTRAINT UQ_StatusIndicatorSubscription_id UNIQUE,
	description varchar(255) NULL,
	emailRecipients varchar(1024) NULL,
	maxLogEntryAge int NULL,
	cronExpression varchar(255) NULL,
	startTime datetime NULL,
	runningIntervalInMillis int NULL,
	disabled tinyint NULL,
	optimisticLock int NOT NULL CONSTRAINT DF_StatusIndicatorSubscription_optimisticLock DEFAULT (0)
) ON [PRIMARY]
--for MySQL:
/*
CREATE TABLE StatusIndicatorSubscription (
	pk bigint NOT NULL AUTO_INCREMENT,
	id VARCHAR(50) NOT NULL,
	description varchar(255) NULL,
	emailRecipients varchar(1024) NULL,
	maxLogEntryAge int NULL,
	cronExpression varchar(255) NULL,
	disabled tinyint NULL,
	optimisticLock int not null default 0
	PRIMARY KEY (pk), 
	UNIQUE INDEX (id)
) ENGINE = InnoDB;
*/

CREATE TABLE StatusIndicatorSubscription_StatusIndicator (
	statusIndicatorSubscription_pk bigint NOT NULL,
	statusIndicator_pk bigint NOT NULL,
	optimisticLock int NOT NULL CONSTRAINT DF_StatusIndicatorSubscription_StatusIndicator_optimisticLock DEFAULT (0)
	CONSTRAINT UQ_StatusIndicatorSubscription_StatusIndicator_statusIndicatorSubscriptionPk_statusIndicatorPk UNIQUE (statusIndicatorSubscription_pk, statusIndicator_pk)
) ON [PRIMARY]
GO
--for MySQL:
/*
CREATE TABLE StatusIndicatorSubscription_StatusIndicator (
	statusIndicatorSubscription_pk bigint NOT NULL,
	statusIndicator_pk bigint NOT NULL,
	optimisticLock int not null default 0,
	CONSTRAINT UQ_StatusIndicatorSubscription_StatusIndicator_pk UNIQUE (StatusIndicatorSubscription_pk, statusIndicator_pk)
) ENGINE = InnoDB;
GO
*/


------------------------------------------------------------------------------------------------------------------------
-- Foreign keys, Constraints, indexes, etc.
------------------------------------------------------------------------------------------------------------------------

-- 2011-12-13: Fremmednøkkel-constraint loggeverdi -> statusindikator
ALTER TABLE StatusLogEntry ADD CONSTRAINT FK_StatusLogEntry_statusIndicator_pk FOREIGN KEY (statusIndicator_pk) REFERENCES StatusIndicator (pk)

ALTER TABLE StatusIndicatorSubscription_StatusIndicator ADD CONSTRAINT FK_StatusIndicatorSubscription_StatusIndicator_subscription_pk
FOREIGN KEY (statusIndicatorSubscription_pk) REFERENCES StatusIndicatorSubscription (pk)

ALTER TABLE StatusIndicatorSubscription_StatusIndicator ADD CONSTRAINT FK_StatusIndicatorSubscription_StatusIndicator_indicator_pk
FOREIGN KEY (statusIndicator_pk) REFERENCES StatusIndicator (pk)

CREATE index ix_created_statetype_statusIndicatorPk ON StatusLogEntry (created, statetype, statusIndicator_pk)

INSERT INTO StatusIndicator (id, name) VALUES ('PROSESS: STATUSVARSLING', 'PROSESS: STATUSVARSLING');


------------------------------------------------------------------------------------------------------------------------
-- Changes
------------------------------------------------------------------------------------------------------------------------

insert into StatusIndicator (id, name, optimisticLock)
values ('statusIndicatorSubscriptionLogger', 'E-post-varsling for status-indikatorer', 0);

--2011-12-23: Konfigurering vha. cron i stedet for start-tidspunkt + intervall
alter table StatusIndicatorSubscription	add cronExpression varchar(255) NULL
alter table StatusIndicatorSubscription	drop column startTime
alter table StatusIndicatorSubscription	drop column runningIntervalInMillis
GO

--2012-01-26: Glemte fremmednøkkel-constraint i koblingstabell for varslings-abonnement på status-indikator 
ALTER TABLE StatusIndicatorSubscription_StatusIndicator ADD CONSTRAINT FK_StatusIndicatorSubscription_StatusIndicator_subscription_pk 
FOREIGN KEY (statusIndicatorSubscription_pk) REFERENCES StatusIndicatorSubscription (pk)

ALTER TABLE StatusIndicatorSubscription_StatusIndicator ADD CONSTRAINT FK_StatusIndicatorSubscription_StatusIndicator_indicator_pk 
FOREIGN KEY (statusIndicator_pk) REFERENCES StatusIndicator (pk)
GO

/***************************************************
 *** Det over er med i Vaadin-Core -versjon 1.68 ***
 ***************************************************/

update StatusIndicator set id = 'PROSESS: STATUSVARSLING', name = 'PROSESS: STATUSVARSLING' where id = 'statusIndicatorSubscriptionLogger'

/***************************************************
 *** Det over er med i Vaadin-Core -versjon 1.89 ***
 ***************************************************/

--2013-10-17: Indeks for å få raskere responstid på status-side
create index ix_created_statetype_statusIndicatorPk ON StatusLogEntry (created, statetype, statusIndicator_pk)
 
--2013-02-19: Ny kolonne for deaktivering etter oppgradering av persistence-core 
alter table statusindicator add disabled tinyint null;
alter table statuslogentry add disabled tinyint null;
alter table statusindicatorsubscription add disabled tinyint null;
