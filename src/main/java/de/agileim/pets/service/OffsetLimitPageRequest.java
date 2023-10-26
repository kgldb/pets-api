package de.agileim.pets.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public class OffsetLimitPageRequest implements Pageable {
    private final int limit;
    private final int offset;

    private final Sort sort = Sort.by(Sort.Direction.ASC, "id");

    public OffsetLimitPageRequest(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitPageRequest(getPageSize(), (int) (getOffset() + getPageSize()));
    }

    public Pageable previous() {
        return hasPrevious() ?
                new OffsetLimitPageRequest(getPageSize(), (int) (getOffset() - getPageSize())) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetLimitPageRequest(getPageSize(), 0);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetLimitPageRequest(getPageSize(), pageNumber * offset);
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffsetLimitPageRequest that = (OffsetLimitPageRequest) o;
        return limit == that.limit && offset == that.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, offset);
    }
}
