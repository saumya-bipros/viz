package com.vizzionnaire.server.dao.service;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.UUIDBased;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.exception.IncorrectParameterException;

import java.util.List;
import java.util.UUID;

public class Validator {

    /**
     * This method validate <code>EntityId</code> entity id. If entity id is invalid than throw
     * <code>IncorrectParameterException</code> exception
     *
     * @param entityId          the entityId
     * @param errorMessage the error message for exception
     */
    public static void validateEntityId(EntityId entityId, String errorMessage) {
        if (entityId == null || entityId.getId() == null) {
            throw new IncorrectParameterException(errorMessage);
        }
    }

    /**
     * This method validate <code>String</code> string. If string is invalid than throw
     * <code>IncorrectParameterException</code> exception
     *
     * @param val          the val
     * @param errorMessage the error message for exception
     */
    public static void validateString(String val, String errorMessage) {
        if (val == null || val.isEmpty()) {
            throw new IncorrectParameterException(errorMessage);
        }
    }


    /**
     * This method validate <code>String</code> string. If string is invalid than throw
     * <code>IncorrectParameterException</code> exception
     *
     * @param val          the val
     * @param errorMessage the error message for exception
     */
    public static void validatePositiveNumber(long val, String errorMessage) {
        if (val <= 0) {
            throw new IncorrectParameterException(errorMessage);
        }
    }

    /**
     * This method validate <code>UUID</code> id. If id is null than throw
     * <code>IncorrectParameterException</code> exception
     *
     * @param id           the id
     * @param errorMessage the error message for exception
     */
    public static void validateId(UUID id, String errorMessage) {
        if (id == null) {
            throw new IncorrectParameterException(errorMessage);
        }
    }


    /**
     * This method validate <code>UUIDBased</code> id. If id is null than throw
     * <code>IncorrectParameterException</code> exception
     *
     * @param id           the id
     * @param errorMessage the error message for exception
     */
    public static void validateId(UUIDBased id, String errorMessage) {
        if (id == null || id.getId() == null) {
            throw new IncorrectParameterException(errorMessage);
        }
    }

    /**
     * This method validate list of <code>UUIDBased</code> ids. If at least one of the ids is null than throw
     * <code>IncorrectParameterException</code> exception
     *
     * @param ids          the list of ids
     * @param errorMessage the error message for exception
     */
    public static void validateIds(List<? extends UUIDBased> ids, String errorMessage) {
        if (ids == null || ids.isEmpty()) {
            throw new IncorrectParameterException(errorMessage);
        } else {
            for (UUIDBased id : ids) {
                validateId(id, errorMessage);
            }
        }
    }

    /**
     * This method validate <code>PageLink</code> page link. If pageLink is invalid than throw
     * <code>IncorrectParameterException</code> exception
     *
     * @param pageLink     the page link
     */
    public static void validatePageLink(PageLink pageLink) {
        if (pageLink == null) {
            throw new IncorrectParameterException("Page link must be specified.");
        } else if (pageLink.getPageSize() < 1) {
            throw new IncorrectParameterException("Incorrect page link page size '"+pageLink.getPageSize()+"'. Page size must be greater than zero.");
        } else if (pageLink.getPage() < 0) {
            throw new IncorrectParameterException("Incorrect page link page '"+pageLink.getPage()+"'. Page must be positive integer.");
        }
    }

}
