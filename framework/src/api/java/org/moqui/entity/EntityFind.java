/*
 * This software is in the public domain under CC0 1.0 Universal.
 * 
 * To the extent possible under law, the author(s) have dedicated all
 * copyright and related and neighboring rights to this software to the
 * public domain worldwide. This software is distributed without any
 * warranty.
 * 
 * You should have received a copy of the CC0 Public Domain Dedication
 * along with this software (see the LICENSE.md file). If not, see
 * <http://creativecommons.org/publicdomain/zero/1.0/>.
 */
package org.moqui.entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Used to setup various options for an entity find (query).
 *
 * All methods to set options modify the option then return this modified object to allow method call chaining. It is
 * important to note that this object is not immutable and is modified internally, and returning EntityFind is just a
 * self reference for convenience.
 *
 * Even after a query a find object can be modified and then used to perform another query.
 */
public interface EntityFind extends java.io.Serializable {

    /** The Name of the Entity to use, as defined in an entity XML file.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind entity(String entityName);
    String getEntity();

    /** Make a dynamic view object to use instead of the entity name (if used the entity name will be ignored).
     *
     * If called multiple times will return the same object.
     *
     * @return EntityDynamicView object to add view details to.
     */
    EntityDynamicView makeEntityDynamicView();

    // ======================== Conditions (Where and Having) =================

    /** Add a field to the find (where clause).
     * If a field has been set with the same name, this will replace that field's value.
     * If any other constraints are already in place this will be ANDed to them.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind condition(String fieldName, Object value);

    /** Compare the named field to the value using the operator. */
    EntityFind condition(String fieldName, EntityCondition.ComparisonOperator operator, Object value);
    EntityFind condition(String fieldName, String operator, Object value);

    /** Compare a field to another field using the operator. */
    EntityFind conditionToField(String fieldName, EntityCondition.ComparisonOperator operator, String toFieldName);

    /** Add a Map of fields to the find (where clause).
     * If a field has been set with the same name and any of the Map keys, this will replace that field's value.
     * Fields set in this way will be combined with other conditions (if applicable) just before doing the query.
     *
     * This will do conversions if needed from Strings to field types as needed, and will only get keys that match
     * entity fields. In other words, it does the same thing as:
     * <code>EntityValue.setFields(fields, true, null, null)</code>
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind condition(Map<String, Object> fields);

    /** Add a EntityCondition to the find (where clause).
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind condition(EntityCondition condition);

    /** Add conditions for the standard effective date query pattern including from field is null or earlier than
     * or equal to compareStamp and thru field is null or later than or equal to compareStamp.
     */
    EntityFind conditionDate(String fromFieldName, String thruFieldName, java.sql.Timestamp compareStamp);

    /** Add a EntityCondition to the having clause of the find.
     * If any having constraints are already in place this will be ANDed to them.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind havingCondition(EntityCondition condition);

    /** Get the current where EntityCondition. */
    EntityCondition getWhereEntityCondition();

    /** Get the current having EntityCondition. */
    EntityCondition getHavingEntityCondition();

    /** Adds conditions for the fields found in the inputFieldsMapName Map.
     *
     * The fields and special fields with suffixes supported are the same as the *-find fields in the XML
     * Forms. This means that you can use this to process the data from the various inputs generated by XML
     * Forms. The suffixes include things like *_op for operators and *_ic for ignore case.
     *
     * For historical reference, this does basically what the Apache OFBiz prepareFind service does.
     *
     * @param inputFieldsMapName The map to get form fields from. If empty will look at the ec.web.parameters map if
     *        the web facade is available, otherwise the current context (ec.context).
     * @param defaultOrderBy If there is not an orderByField parameter this is used instead.
     * @param alwaysPaginate If true pagination offset/limit will be set even if there is no pageIndex parameter.
     * @return Returns this for chaining of method calls.
     */
    EntityFind searchFormInputs(String inputFieldsMapName, String defaultOrderBy, boolean alwaysPaginate);
    EntityFind searchFormMap(Map inf, String defaultOrderBy, boolean alwaysPaginate);

    // ======================== General/Common Options ========================

    /** The field of the named entity to get from the database.
     * If any select fields have already been specified this will be added to the set.
     * @return Returns this for chaining of method calls.
     */
    EntityFind selectField(String fieldToSelect);

    /** The fields of the named entity to get from the database; if empty or null all fields will be retrieved.
     * @return Returns this for chaining of method calls.
     */
    EntityFind selectFields(Collection<String> fieldsToSelect);
    List<String> getSelectFields();

    /** A field of the find entity to order the query by. Optionally add a " ASC" to the end or "+" to the
     *     beginning for ascending, or " DESC" to the end of "-" to the beginning for descending.
     * If any other order by fields have already been specified this will be added to the end of the list.
     * The String may be a comma-separated list of field names. Only fields that actually exist on the entity will be
     *     added to the order by list.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind orderBy(String orderByFieldName);

    /** Each List entry is passed to the orderBy(String orderByFieldName) method, see it for details.
     * @return Returns this for chaining of method calls.
     */
    EntityFind orderBy(List<String> orderByFieldNames);
    List<String> getOrderBy();

    /** Look in the cache before finding in the datasource.
     * Defaults to setting on entity definition.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind useCache(Boolean useCache);
    boolean getUseCache();

    // ======================== Advanced Options ==============================

    /** Specifies whether the values returned should be filtered to remove duplicate values.
     * Default is false.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind distinct(boolean distinct);
    boolean getDistinct();

    /** The offset, ie the starting row to return. Default (null) means start from the first actual row.
     * Only applicable for list() and iterator() finds.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind offset(Integer offset);
    /** Specify the offset in terms of page index and size. Actual offset is pageIndex * pageSize. */
    EntityFind offset(int pageIndex, int pageSize);
    Integer getOffset();

    /** The limit, ie max number of rows to return. Default (null) means all rows.
     * Only applicable for list() and iterator() finds.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind limit(Integer limit);
    Integer getLimit();

    /** For use with searchFormInputs when paginated. Equals offset (default 0) divided by page size. */
    int getPageIndex();
    /** For use with searchFormInputs when paginated. Equals limit (default 20; exists for consistency/convenience along with getPageIndex()). */
    int getPageSize();

    /** Lock the selected record so only this transaction can change it until it is ended.
     * If this is set when the find is done the useCache setting will be ignored as this will always get the data from
     *     the database.
     * Default is false.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind forUpdate(boolean forUpdate);
    boolean getForUpdate();

    // ======================== JDBC Options ==============================

    /** Specifies how the ResultSet will be traversed. Available values: ResultSet.TYPE_FORWARD_ONLY,
     *      ResultSet.TYPE_SCROLL_INSENSITIVE (default) or ResultSet.TYPE_SCROLL_SENSITIVE. See the java.sql.ResultSet JavaDoc for
     *      more information. If you want it to be fast, use the common option: ResultSet.TYPE_FORWARD_ONLY.
     *      For partial results where you want to jump to an index make sure to use TYPE_SCROLL_INSENSITIVE.
     * Defaults to ResultSet.TYPE_SCROLL_INSENSITIVE.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind resultSetType(int resultSetType);
    int getResultSetType();

    /** Specifies whether or not the ResultSet can be updated. Available values:
     *      ResultSet.CONCUR_READ_ONLY (default) or ResultSet.CONCUR_UPDATABLE. Should pretty much always be
     *      ResultSet.CONCUR_READ_ONLY with the Entity Facade since updates are generally done as separate operations.
     * Defaults to CONCUR_READ_ONLY.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind resultSetConcurrency(int resultSetConcurrency);
    int getResultSetConcurrency();

    /** The JDBC fetch size for this query. Default (null) will fall back to datasource settings.
     * This is not the fetch as in the OFFSET/FETCH SQL clause (use limit for that), and is rather the JDBC fetch to
     * determine how many rows to get back on each round-trip to the database.
     *
     * Only applicable for list() and iterator() finds.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind fetchSize(Integer fetchSize);
    Integer getFetchSize();

    /** The JDBC max rows for this query. Default (null) will fall back to datasource settings.
     * This is the maximum number of rows the ResultSet will keep in memory at any given time before releasing them
     * and if requested they are retrieved from the database again.
     *
     * Only applicable for list() and iterator() finds.
     *
     * @return Returns this for chaining of method calls.
     */
    EntityFind maxRows(Integer maxRows);
    Integer getMaxRows();


    // ======================== Run Find Methods ==============================

    EntityFind disableAuthz();

    /** Runs a find with current options to get a single record by primary key.
     * This method ignores the cache setting and always gets results from the database.
     */
    EntityValue one() throws EntityException;

    /** Runs a find with current options to get a list of records.
     * This method ignores the cache setting and always gets results from the database.
     */
    EntityList list() throws EntityException;

    /** Runs a find with current options and returns an EntityListIterator object.
     * This method ignores the cache setting and always gets results from the database.
     */
    EntityListIterator iterator() throws EntityException;

    /** Runs a find with current options to get a count of matching records.
     * This method ignores the cache setting and always gets results from the database.
     */
    long count() throws EntityException;

    /** Update a set of values that match a condition.
     *
     * @param fieldsToSet The fields of the named entity to set in the database
     * @return long representing number of rows effected by this operation
     * @throws EntityException
     */
    long updateAll(Map<String, ?> fieldsToSet) throws EntityException;

    /** Delete entity records that match a condition.
     *
     * @return long representing number of rows effected by this operation
     * @throws EntityException
     */
    long deleteAll() throws EntityException;
}
