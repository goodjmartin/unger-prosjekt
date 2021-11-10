------------------------------------------------------------------------------------------------------------------------
-- Table definitions
------------------------------------------------------------------------------------------------------------------------
/*
drop table SeriesConfig
drop table AxisConfig
drop table PanelConfig
drop table DashboardConfig
drop table FetcherConfig
drop table Category
*/

create table Category(
--mssql:	pk bigint IDENTITY(1, 1) NOT NULL CONSTRAINT PK_Category_pk PRIMARY KEY,
--mysql:	pk bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
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

CREATE TABLE DashboardConfig (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_DashboardConfig_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	id VARCHAR(50) NOT NULL,
	title VARCHAR(255) NULL,
	numRows int NOT NULL DEFAULT 1,
	numColumns int NOT NULL DEFAULT 1,
	refreshIntervalInSeconds int NULL,
	area_pk int,
	disabled tinyint NULL,
	optimisticLock int NOT NULL DEFAULT 0
)

CREATE TABLE PanelConfig (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_PanelConfig_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	dtype VARCHAR(255) NOT NULL,
	title VARCHAR(255) NULL,
	startRow int NOT NULL,
	endRow int NOT NULL,
	startColumn int NOT NULL,
	endColumn int NOT NULL,
	periodLengthInMinutes int NOT NULL,
	alignTicks tinyint,
	timeShift tinyint,
	dashboardConfig_pk int NOT NULL,
	runningAverageSeries_pk int,
	runningAverageMaxValueCount smallint,
	runningAveragePrecision tinyint,
	disabled tinyint,
	optimisticLock int NOT NULL DEFAULT 0
)

CREATE TABLE AxisConfig (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_AxisConfig_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	name VARCHAR(255) NULL,
	minValue decimal(19, 6),
	maxValue decimal(19, 6),
	tickInterval decimal(19, 6),
	lineWidth decimal(19, 6),
	tickWidth decimal(19, 6),
	gridLineWidth int,
	showMarker tinyint,
	opposite tinyint,
	hideAlternateGrid tinyint,
	panelConfig_pk int NOT NULL,
	disabled tinyint,
	optimisticLock int NOT NULL DEFAULT 0
)

CREATE TABLE FetcherConfig (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_FetcherConfig_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	id VARCHAR(50) NOT NULL CONSTRAINT UQ_FetcherConfig_id UNIQUE,
	dtype VARCHAR(255) NOT NULL,
	fullFetch tinyInt,
	refreshIntervalInSeconds int,
	url VARCHAR(255),
	datasourceName VARCHAR(255),
	databaseName VARCHAR(255),
	tagWebId VARCHAR(255),
	username VARCHAR(50),
	password VARCHAR(50),
	query VARCHAR(8000),
	intervals int,
	minValue decimal(19, 6),
	maxValue decimal(19, 6),
	disabled tinyint NULL,
	optimisticLock int NOT NULL DEFAULT 0
)

CREATE TABLE SeriesConfig (
-- mssql: pk int IDENTITY(1, 1) NOT NULL CONSTRAINT PK_SeriesConfig_pk PRIMARY KEY,
-- mysql: pk int NOT NULL auto_increment PRIMARY KEY,
	id VARCHAR(50) NOT NULL,
	name VARCHAR(255) NULL,
	showMarker tinyint,
	plotband tinyint,
	axisConfig_pk int,
	panelConfig_pk int NOT NULL,
	fetcherConfig_pk int NOT NULL,
	connectedSeriesConfig_pk int,
	fixedValue decimal(19, 6),
	limitType tinyint,
	sourceType tinyint,
	color VARCHAR(50),
	lineWidth tinyInt,
	disabled tinyint,
	optimisticLock int NOT NULL DEFAULT 0
)

------------------------------------------------------------------------------------------------------------------------
-- Foreign keys, Constraints, indexes, etc.
------------------------------------------------------------------------------------------------------------------------
ALTER TABLE DashboardConfig ADD CONSTRAINT FK_DashboardConfig_area_pk FOREIGN KEY (area_pk) REFERENCES Category (pk)
ALTER TABLE SeriesConfig ADD CONSTRAINT FK_SeriesConfig_axisConfig_pk FOREIGN KEY (axisConfig_pk) REFERENCES AxisConfig (pk)
ALTER TABLE SeriesConfig ADD CONSTRAINT FK_SeriesConfig_panelConfig_pk FOREIGN KEY (panelConfig_pk) REFERENCES PanelConfig (pk)
ALTER TABLE SeriesConfig ADD CONSTRAINT FK_SeriesConfig_fetcherConfig_pk FOREIGN KEY (fetcherConfig_pk) REFERENCES FetcherConfig (pk)
ALTER TABLE SeriesConfig ADD CONSTRAINT FK_SeriesConfig_connectedSeriesConfig_pk FOREIGN KEY (connectedSeriesConfig_pk) REFERENCES SeriesConfig (pk)
ALTER TABLE AxisConfig ADD CONSTRAINT FK_AxisConfig_panelConfig_pk FOREIGN KEY (panelConfig_pk) REFERENCES PanelConfig (pk)
ALTER TABLE PanelConfig ADD CONSTRAINT FK_PanelConfig_dashboardConfig_pk FOREIGN KEY (dashboardConfig_pk) REFERENCES DashboardConfig (pk)
ALTER TABLE PanelConfig ADD CONSTRAINT FK_PanelConfig_runningAverageSeries_pk FOREIGN KEY (runningAverageSeries_pk) REFERENCES SeriesConfig (pk)

------------------------------------------------------------------------------------------------------------------------
-- Default data
------------------------------------------------------------------------------------------------------------------------
insert into PropertyClass(id, description) values ('DashboardConfig', 'Egenskap for dashboard')

------------------------------------------------------------------------------------------------------------------------
-- Changes
------------------------------------------------------------------------------------------------------------------------

-- 2018-03-16: Config for series that represents limits
alter table SeriesConfig add connectedSeriesConfig_pk int
alter table SeriesConfig add limitType tinyint
ALTER TABLE SeriesConfig ADD CONSTRAINT FK_SeriesConfig_connectedSeriesConfig_pk FOREIGN KEY (connectedSeriesConfig_pk) REFERENCES SeriesConfig (pk)

-- 2018-03-23: Configurable series color
alter table SeriesConfig add color VARCHAR(50)

--2018-03-27: ForIT need this to decide where to pick data for series from, and also if we need to align timestamps
alter table SeriesConfig add sourceType tinyint
alter table PanelConfig add timeShift tinyint

-- 2018-04-16: We don't need this
alter table axisconfig drop column ID

-- 2018-05-04: Support for limit/target-series with fixed value -> horizontal plotlines
alter table SeriesConfig add fixedValue decimal(19, 6)

-- 2018-05-24: Connect dashboard to line
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
ALTER TABLE DashboardConfig ADD area_pk int
ALTER TABLE DashboardConfig ADD CONSTRAINT FK_DashboardConfig_area_pk FOREIGN KEY (area_pk) REFERENCES Category (pk)

-- 2018-06-22: Nå kan dashboards ha egenskaper
insert into PropertyClass(id, description) values ('DashboardConfig', 'Egenskap for dashboard')

-- 2018-07-09: Now you can adjust line with on a series
alter table SeriesConfig add lineWidth tinyint
