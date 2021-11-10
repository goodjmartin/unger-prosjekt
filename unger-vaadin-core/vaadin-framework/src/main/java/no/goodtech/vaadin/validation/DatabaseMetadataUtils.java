package no.goodtech.vaadin.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * Fetches column size for string columns from database schema
 */
public class DatabaseMetadataUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMetadataUtils.class);
	private final JdbcTemplate template;
	private static final Set<Integer> STRING_TYPES = new HashSet<>();

	static {
		STRING_TYPES.addAll(Arrays.asList(Types.VARCHAR, Types.CLOB, Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NVARCHAR, Types.NCLOB, Types.SQLXML));
	}

	public DatabaseMetadataUtils(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}

	public Map<String, Integer> getColumnLengths(String tableName) {
		final Map<String, Integer> columnLengths = new LinkedHashMap<>();

		String sql = String.format("select * from %s", tableName);
		template.setMaxRows(1);
		try {
			return (template.query(sql, new ResultSetExtractor<Map<String, Integer>>() {

				@Override
				public Map<String, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						if (STRING_TYPES.contains(metaData.getColumnType(i))) {
							columnLengths.put(metaData.getColumnName(i), metaData.getColumnDisplaySize(i));
						}
					}
					return columnLengths;
				}
			}));
		} catch (BadSqlGrammarException e) {
			LOGGER.debug(e.getMessage());
			return columnLengths;
		}
	}

	public Integer getColumnLength(String tableName, String columnName) {
		String sql = String.format("select * from %s", tableName);
		template.setMaxRows(1);
		Integer length = template.query(sql, new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {

				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				for(int i = 1 ; i <= columnCount ; i++) {
					if (columnName.toLowerCase().equals(metaData.getColumnName(i).toLowerCase())) {
						return metaData.getColumnDisplaySize(i);
					}
				}
				return null;
			}
		});
		return length;
	}

//  for testing
//	public static void main(String[] args) {
//		DataSource dataSource = new DriverManagerDataSource("jdbc:jtds:sqlserver://kh-dev-03/BioMarPDB_Myre_OEMY", "pdb", "R0ckme");
//		final DatabaseMetadataUtils metadata = new DatabaseMetadataUtils(dataSource);
//		System.out.println(metadata.getColumnLength("MaterialLot", "Description"));
//		System.out.println(metadata.getColumnLength("delete_TraceLink", "serieNr"));
//
//		DataSource dataSource2 = new DriverManagerDataSource("jdbc:jtds:sqlserver://172.19.11.62/UNGER;instance=SQLSERVER2014", "mesServer", "R0ckme");
//		final DatabaseMetadataUtils metadata2 = new DatabaseMetadataUtils(dataSource);
//		System.out.println(metadata.getColumnLength("MaterialLot", "Id"));
//
//	}
}
