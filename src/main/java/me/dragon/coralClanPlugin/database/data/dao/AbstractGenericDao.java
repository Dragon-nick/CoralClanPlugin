package me.dragon.coralClanPlugin.database.data.dao;

import lombok.Getter;
import lombok.Setter;
import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.database.DatabaseManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AbstractGenericDao is an abstract class that provides common functionality and utilities for generic data access
 * objects.
 *
 * @param <B> The type of the entity.
 */
@Getter
@Setter
public abstract class AbstractGenericDao<B> {
	private static final Logger LOGGER = Bukkit.getLogger();
	private static int DEFAULT_INTEGER = 0;
	private static final String NOT_AVAILABLE = "Not available";
	private static final String UNIMPLEMENTED = "Unimplemented";

	private final DatabaseManager databaseManager;
	private boolean error;

	protected AbstractGenericDao() {
		this.databaseManager = CoralClanPlugin
			.getInstance()
			.getDatabase();
	}

	protected static String getString(@NotNull final ResultSet pResultSet, @NotNull final String pColumnName) throws SQLException {
		return DatabaseManager.columnNotExists(pResultSet, pColumnName) ?
			StringUtils.EMPTY:
			pResultSet.getString(pColumnName);
	}

	protected static int getInt(@NotNull final ResultSet pResultSet, @NotNull final String pColumnName) throws SQLException {
		return DatabaseManager.columnNotExists(pResultSet, pColumnName) ?
			DEFAULT_INTEGER:
			pResultSet.getInt(pColumnName);
	}

	protected static void setParametersOnStatement(
		@NotNull final PreparedStatement statement,
		@Nullable final Object... pParameters
	) throws SQLException {
		if (pParameters != null) {
			int index = 1;
			for (final Object parameter : pParameters) {
				switch (parameter) {
					case null -> statement.setNull(index, Types.NULL);
					case final String value -> statement.setString(index, value);
					case final Enum<?> value -> statement.setString(index, value.name());
					default -> statement.setString(index, parameter.toString());
				}

				index++;
			}
		}
	}

	protected void executeQuery(@NotNull final String pQuery, @Nullable final Object... pParameters) {
		this.setError(false);

		if (pParameters != null) {
			final int questionMarkCount = StringUtils.countMatches(pQuery, '?');
			if (questionMarkCount != pParameters.length) {
				LOGGER.log(Level.SEVERE, String.format("Wrong number of parameters. Required: [%d] | Provided: [%d]",
					questionMarkCount, pParameters.length));
				return;
			}
		}

		LOGGER.log(Level.INFO, "Executing query: " + pQuery);

		try (final Connection connection = this.databaseManager.getConnection();
			final PreparedStatement statement = connection.prepareStatement(pQuery)) {
			setParametersOnStatement(statement, pParameters);

			statement.executeUpdate();
		} catch (final SQLException exception) {
			this.setError(true);
			LOGGER.log(Level.SEVERE, "Error executing query: " + pQuery, exception);
		}
	}

	/**
	 * Executes a batch of queries with different parameter sets efficiently using JDBC batch processing. This
	 * method is
	 * optimized for bulk operations and provides better performance than individual query execution.
	 *
	 * @param pQuery          The SQL query string to execute for all parameter sets
	 * @param pParametersList List of parameter arrays, where each array represents parameters for one execution
	 * @throws IllegalArgumentException if pParametersList is null or empty
	 */
	protected void executeBatchQuery(@NotNull final String pQuery, @NotNull final List<Object[]> pParametersList) {
		if (pParametersList.isEmpty()) {
			LOGGER.log(Level.SEVERE, "Empty parameters list. Failed to prepare batch query");
			return;
		}

		this.setError(false);

		LOGGER.log(Level.INFO, String.format("Executing batch query: %s with %d parameter sets", pQuery,
			pParametersList.size()));

		try (final Connection connection = this.databaseManager.getConnection();
			final PreparedStatement statement = connection.prepareStatement(pQuery)) {

			// Disable auto-commit for better batch performance
			connection.setAutoCommit(false);

			for (final Object[] parameters : pParametersList) {
				setParametersOnStatement(statement, parameters);
				statement.addBatch();
			}

			// Execute all batched statements
			final int[] results = statement.executeBatch();
			connection.commit();

			LOGGER.log(Level.INFO, String.format("Batch execution completed. %d statements executed successfully",
				results.length));
		} catch (final SQLException exception) {
			this.setError(true);
			LOGGER.log(Level.SEVERE, "Error executing batch query: " + pQuery, exception);
		}
	}

	protected Optional<B> executeReadQuerySingle(@NotNull final String pQuery,
		@Nullable final Object... pParameters) {
		this.setError(false);

		if (pParameters != null) {
			final int questionMarkCount = StringUtils.countMatches(pQuery, '?');
			if (questionMarkCount != pParameters.length) {
				LOGGER.log(Level.SEVERE, String.format("Wrong number of parameters. Required: [%d] | Provided: [%d]",
					questionMarkCount, pParameters.length));
				return Optional.empty();
			}
		}

		LOGGER.log(Level.INFO, "Executing query: " + pQuery);

		Optional<B> optItem = Optional.empty();

		try (final Connection connection = this.databaseManager.getConnection();
			final PreparedStatement statement = connection.prepareStatement(pQuery)) {

			if (pParameters != null) {
				setParametersOnStatement(statement, pParameters);
			}

			final ResultSet result = statement.executeQuery();

			optItem = result.next() ?
				Optional.ofNullable(this.mapper(result)):
				Optional.empty();

			result.close();
		} catch (final SQLException | IllegalArgumentException | UnsupportedOperationException exception) {
			this.setError(true);
			LOGGER.log(Level.SEVERE, "Error executing query: " + pQuery, exception);
		}

		return optItem;
	}

	protected List<B> executeReadQueryList(@NotNull final String pQuery, @Nullable final Object... pParameters) {
		this.setError(false);

		if (pParameters != null) {
			final int questionMarkCount = StringUtils.countMatches(pQuery, '?');
			if (questionMarkCount != pParameters.length) {
				LOGGER.log(Level.SEVERE, String.format("Wrong number of parameters. Required: [%d] | Provided: [%d]",
					questionMarkCount, pParameters.length));
				return Collections.emptyList();
			}
		}

		LOGGER.log(Level.INFO, "Executing query: " + pQuery);

		final List<B> itemList = new LinkedList<>(Collections.emptyList());

		try (final Connection connection = this.databaseManager.getConnection();
			final PreparedStatement statement = connection.prepareStatement(pQuery)) {

			if (pParameters != null) {
				setParametersOnStatement(statement, pParameters);
			}

			final ResultSet result = statement.executeQuery();
			while (result.next()) {
				itemList.add(this.mapper(result));
			}

			result.close();
		} catch (final SQLException | IllegalArgumentException | UnsupportedOperationException exception) {
			this.setError(true);
			LOGGER.log(Level.SEVERE, "Error executing query: " + pQuery, exception);
		}

		return Collections.unmodifiableList(itemList);
	}

	/**
	 * Maps a database ResultSet row to a domain object.
	 * <p>
	 * This method serves as the primary transformation layer between database records and application objects.
	 * Implementations should extract data from the ResultSet and populate a new instance of type B with the
	 * corresponding field values.
	 * </p>
	 * <p>
	 * <strong>Note:</strong> This method must be overridden by subclasses that perform
	 * database operations. For non-database implementations (e.g., remote API DAOs), this method may remain
	 * unimplemented.
	 * </p>
	 *
	 * @param pResultSet the ResultSet positioned at the row to map
	 * @return a new instance of type B populated with data from the ResultSet, or null if mapping fails
	 * @throws UnsupportedOperationException if the method is not overridden in a database-backed implementation
	 */
	protected @Nullable B mapper(@NotNull final ResultSet pResultSet) {
		final UnsupportedOperationException exception = new UnsupportedOperationException(NOT_AVAILABLE);
		LOGGER.log(Level.SEVERE, UNIMPLEMENTED + ". Mapper must be overridden", exception);
		throw exception;
	}

	/**
	 * Creates a new record in the data store.
	 * <p>
	 * In database implementations, this persists a new record to the database. In remote API implementations, this
	 * sends a POST/CREATE request to the remote service.
	 * </p>
	 * <p>
	 * <strong>Default behavior:</strong> Throws an UnsupportedOperationException.
	 * Subclasses must override this method to provide create functionality.
	 * </p>
	 *
	 * @param pBean the object to be created/persisted
	 * @throws UnsupportedOperationException if the method is not overridden
	 */
	public void create(final @NotNull B pBean) {
		AbstractGenericDao.unavailable();
	}

	/**
	 * Retrieves a single record from the data store.
	 * <p>
	 * In database implementations, this executes a SELECT query to find a matching record. In remote API
	 * implementations, this sends a GET request to retrieve the resource. The provided bean typically contains the
	 * criteria (e.g., ID or search parameters) used to locate the desired record.
	 * </p>
	 * <p>
	 * <strong>Default behavior:</strong> Returns an empty Optional after logging an error.
	 * Subclasses must override this method to provide read functionality.
	 * </p>
	 *
	 * @param pBean the object containing search criteria for the record to retrieve
	 * @return an Optional containing the found record, or empty if not found
	 * @throws UnsupportedOperationException if the method is not overridden
	 */
	public Optional<B> read(@NotNull final B pBean) {
		AbstractGenericDao.unavailable();
		return Optional.empty();
	}

	/**
	 * Retrieves all records from the data store.
	 * <p>
	 * In database implementations, this executes a SELECT query to fetch all records. In remote API implementations,
	 * this sends a GET request to list all resources.
	 * </p>
	 * <p>
	 * <strong>Default behavior:</strong> Returns an empty list after logging an error.
	 * Subclasses must override this method to provide list functionality.
	 * </p>
	 * <p>
	 * <strong>Warning:</strong> Use with caution on large datasets as this retrieves
	 * all records without pagination.
	 * </p>
	 *
	 * @return a list of all records; empty list if none exist or operation is unsupported
	 * @throws UnsupportedOperationException if the method is not overridden
	 */
	public List<B> readList() {
		AbstractGenericDao.unavailable();
		return Collections.emptyList();
	}

	/**
	 * Updates an existing record in the data store.
	 * <p>
	 * In database implementations, this executes an UPDATE query to modify the record. In remote API implementations,
	 * this sends a PUT/PATCH request to update the resource. The provided bean should contain both the identifier and
	 * the new field values.
	 * </p>
	 * <p>
	 * <strong>Default behavior:</strong> Throws an UnsupportedOperationException.
	 * Subclasses must override this method to provide update functionality.
	 * </p>
	 *
	 * @param pBean the object containing updated data to be persisted
	 * @throws UnsupportedOperationException if the method is not overridden
	 */
	public void update(final @NotNull B pBean) {
		AbstractGenericDao.unavailable();
	}

	/**
	 * Deletes a record from the data store.
	 * <p>
	 * In database implementations, this executes a DELETE query to remove the record. In remote API implementations,
	 * this sends a DELETE request to remove the resource. The provided bean should contain the identifier of the
	 * record
	 * to be deleted.
	 * </p>
	 * <p>
	 * <strong>Default behavior:</strong> Throws an UnsupportedOperationException.
	 * Subclasses must override this method to provide delete functionality.
	 * </p>
	 *
	 * @param pBean the object identifying the record to be deleted
	 * @throws UnsupportedOperationException if the method is not overridden
	 */
	public void delete(final @NotNull B pBean) {
		AbstractGenericDao.unavailable();
	}

	private static void unavailable() {
		final UnsupportedOperationException exception = new UnsupportedOperationException(NOT_AVAILABLE);
		LOGGER.log(Level.SEVERE, UNIMPLEMENTED, exception);
		throw exception;
	}
}
