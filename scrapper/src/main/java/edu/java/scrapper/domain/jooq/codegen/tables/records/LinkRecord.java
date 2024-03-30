/*
 * This file is generated by jOOQ.
 */
package edu.java.scrapper.domain.jooq.codegen.tables.records;


import edu.java.scrapper.domain.jooq.codegen.tables.Link;

import jakarta.validation.constraints.Size;

import java.beans.ConstructorProperties;
import java.time.OffsetDateTime;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class LinkRecord extends UpdatableRecordImpl<LinkRecord> implements Record5<Long, String, OffsetDateTime, OffsetDateTime, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>LINK.ID</code>.
     */
    public void setId(@Nullable Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>LINK.ID</code>.
     */
    @Nullable
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>LINK.URL</code>.
     */
    public void setUrl(@NotNull String value) {
        set(1, value);
    }

    /**
     * Getter for <code>LINK.URL</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 1000000000)
    @NotNull
    public String getUrl() {
        return (String) get(1);
    }

    /**
     * Setter for <code>LINK.LAST_CHECK_TIME</code>.
     */
    public void setLastCheckTime(@NotNull OffsetDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>LINK.LAST_CHECK_TIME</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public OffsetDateTime getLastCheckTime() {
        return (OffsetDateTime) get(2);
    }

    /**
     * Setter for <code>LINK.CREATED_AT</code>.
     */
    public void setCreatedAt(@NotNull OffsetDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>LINK.CREATED_AT</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public OffsetDateTime getCreatedAt() {
        return (OffsetDateTime) get(3);
    }

    /**
     * Setter for <code>LINK.CREATED_BY</code>.
     */
    public void setCreatedBy(@NotNull String value) {
        set(4, value);
    }

    /**
     * Getter for <code>LINK.CREATED_BY</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 1000000000)
    @NotNull
    public String getCreatedBy() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row5<Long, String, OffsetDateTime, OffsetDateTime, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    @NotNull
    public Row5<Long, String, OffsetDateTime, OffsetDateTime, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    @NotNull
    public Field<Long> field1() {
        return Link.LINK.ID;
    }

    @Override
    @NotNull
    public Field<String> field2() {
        return Link.LINK.URL;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field3() {
        return Link.LINK.LAST_CHECK_TIME;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field4() {
        return Link.LINK.CREATED_AT;
    }

    @Override
    @NotNull
    public Field<String> field5() {
        return Link.LINK.CREATED_BY;
    }

    @Override
    @Nullable
    public Long component1() {
        return getId();
    }

    @Override
    @NotNull
    public String component2() {
        return getUrl();
    }

    @Override
    @NotNull
    public OffsetDateTime component3() {
        return getLastCheckTime();
    }

    @Override
    @NotNull
    public OffsetDateTime component4() {
        return getCreatedAt();
    }

    @Override
    @NotNull
    public String component5() {
        return getCreatedBy();
    }

    @Override
    @Nullable
    public Long value1() {
        return getId();
    }

    @Override
    @NotNull
    public String value2() {
        return getUrl();
    }

    @Override
    @NotNull
    public OffsetDateTime value3() {
        return getLastCheckTime();
    }

    @Override
    @NotNull
    public OffsetDateTime value4() {
        return getCreatedAt();
    }

    @Override
    @NotNull
    public String value5() {
        return getCreatedBy();
    }

    @Override
    @NotNull
    public LinkRecord value1(@Nullable Long value) {
        setId(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value2(@NotNull String value) {
        setUrl(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value3(@NotNull OffsetDateTime value) {
        setLastCheckTime(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value4(@NotNull OffsetDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value5(@NotNull String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord values(@Nullable Long value1, @NotNull String value2, @NotNull OffsetDateTime value3, @NotNull OffsetDateTime value4, @NotNull String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LinkRecord
     */
    public LinkRecord() {
        super(Link.LINK);
    }

    /**
     * Create a detached, initialised LinkRecord
     */
    @ConstructorProperties({ "id", "url", "lastCheckTime", "createdAt", "createdBy" })
    public LinkRecord(@Nullable Long id, @NotNull String url, @NotNull OffsetDateTime lastCheckTime, @NotNull OffsetDateTime createdAt, @NotNull String createdBy) {
        super(Link.LINK);

        setId(id);
        setUrl(url);
        setLastCheckTime(lastCheckTime);
        setCreatedAt(createdAt);
        setCreatedBy(createdBy);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised LinkRecord
     */
    public LinkRecord(edu.java.scrapper.domain.jooq.codegen.tables.pojos.Link value) {
        super(Link.LINK);

        if (value != null) {
            setId(value.getId());
            setUrl(value.getUrl());
            setLastCheckTime(value.getLastCheckTime());
            setCreatedAt(value.getCreatedAt());
            setCreatedBy(value.getCreatedBy());
            resetChangedOnNotNull();
        }
    }
}
