-- testdata for dashboards
/*
delete from SeriesConfig
delete from AxisConfig
delete from PanelConfig
delete from DashboardConfig
delete from FetcherConfig
*/

insert into FetcherConfig(id, refreshIntervalInSeconds, dtype, datasourceName, query) values ('Sine0@mes', 30, 'DbSeriesFetcherConfig', 'mes',
'select SE.timestamp, SE.value from Signal S inner join SignalEvent SE on SE.signal_pk = S.pk
where S.id = ''Sine0@mes''
and SE.timestamp > ''FROM_DATE''
order by SE.timestamp asc')

insert into FetcherConfig(id, refreshIntervalInSeconds, dtype, url, databaseName, username, password, query) values ('Sine1@influx', 3, 'InfluxDbFetcherConfig',
'http://localhost:8086', 'messy', 'mes', 'goodtech1234', '
SELECT time, value FROM tagvalue
WHERE tagname = ''[GenericSimulator]_Meta:Sine/Sine1''
AND time > FROM_DATE
ORDER BY time')

insert into FetcherConfig(id, refreshIntervalInSeconds, dtype, url, databaseName, username, password, query) values ('Ramp0@influx', 3, 'InfluxDbFetcherConfig',
'http://localhost:8086', 'messy', 'mes', 'goodtech1234', '
SELECT time, value FROM tagvalue
WHERE tagname = ''[GenericSimulator]_Meta:Ramp/Ramp0''
AND time > FROM_DATE
ORDER BY time')

insert into FetcherConfig(id, refreshIntervalInSeconds, dtype, minValue, maxValue) values ('rand1@simulator', 3, 'SimulatorFetcherConfig', 0, 100)

insert into DashboardConfig(id, title, numRows, numColumns, refreshIntervalInSeconds) values ('DASH1', 'Dashboard 1', 3, 3, 5)
insert into PanelConfig(dtype, title, startRow, endRow, startColumn, endColumn, periodLengthInMinutes, dashboardConfig_pk) select 'ChartConfig', 'Panel 1', 0, 0, 0, 0, 10, pk from DashboardConfig where id = 'DASH1'
insert into PanelConfig(dtype, title, startRow, endRow, startColumn, endColumn, periodLengthInMinutes, dashboardConfig_pk) select 'ChartConfig', 'Panel 2', 0, 1, 1, 2, 10, pk from DashboardConfig where id = 'DASH1'
insert into PanelConfig(dtype, title, startRow, endRow, startColumn, endColumn, periodLengthInMinutes, dashboardConfig_pk) select 'ChartConfig', 'Panel 3', 2, 2, 0, 2, 10, pk from DashboardConfig where id = 'DASH1'

insert into AxisConfig(id, name, minvalue, maxvalue, chartConfig_pk) select 'AxisA', 'm3', -20, 10, pk from PanelConfig where title = 'Panel 1'
insert into AxisConfig(id, name, minvalue, maxvalue, opposite, chartConfig_pk) select 'AxisB', 'km/h', 100, 800, 1, pk from PanelConfig where title = 'Panel 1'

declare @fetcherConfig1_pk bigint
declare @axisConfig1_pk bigint
select @axisConfig1_pk=pk from AxisConfig where id = 'AxisA'
select @fetcherConfig1_pk=pk from FetcherConfig where id = 'Sine1@influx'
insert into SeriesConfig(id, fetcherConfig_pk,  axisConfig_pk, chartConfig_pk) select 'Sine1@influx', @fetcherConfig1_pk, @axisConfig1_pk, pk from PanelConfig where title = 'Panel 1'

declare @fetcherConfig2_pk bigint
declare @axisConfig2_pk bigint
select @axisConfig2_pk=pk from AxisConfig where id = 'AxisB'
select @fetcherConfig2_pk=pk from FetcherConfig where id = 'Ramp0@influx'
insert into SeriesConfig(id, fetcherConfig_pk, axisConfig_pk, chartConfig_pk) select 'Ramp0@influx', @fetcherConfig2_pk, @axisConfig2_pk, pk from PanelConfig where title = 'Panel 1'

declare @fetcherConfig3_pk bigint
select @fetcherConfig3_pk=pk from FetcherConfig where id = 'Sine0@mes'
insert into SeriesConfig(id, fetcherConfig_pk, chartConfig_pk) select 'Sine0@mes', @fetcherConfig3_pk, pk from PanelConfig where title = 'Panel 2'

declare @fetcherConfig4_pk bigint
select @fetcherConfig4_pk=pk from FetcherConfig where id = 'Sine1@influx'
insert into SeriesConfig(id, fetcherConfig_pk, chartConfig_pk) select 'Sine1@influx', @fetcherConfig4_pk, pk from PanelConfig where title = 'Panel 3'

declare @fetcherConfig5_pk bigint
select @fetcherConfig5_pk=pk from FetcherConfig where id = 'rand1@simulator'
insert into SeriesConfig(id, fetcherConfig_pk, chartConfig_pk) select 'rand1@simulator', @fetcherConfig5_pk, pk from PanelConfig where title = 'Panel 3'
