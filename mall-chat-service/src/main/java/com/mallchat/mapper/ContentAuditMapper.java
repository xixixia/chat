package com.mallchat.mapper;

import com.mallchat.model.ContentAudit;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * Content audit mapper.
 */
public interface ContentAuditMapper {
    /**
     * Insert audit record.
     *
     * @param audit audit record
     * @return rows affected
     */
    int insert(ContentAudit audit);

    /**
     * List audits by status.
     *
     * @param status status filter
     * @param offset offset
     * @param limit limit
     * @return audit list
     */
    List<ContentAudit> listByStatus(@Param("status") Integer status,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    /**
     * Count audits by status.
     *
     * @param status status filter
     * @return count
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * Update audit status.
     *
     * @param id audit id
     * @param status status
     * @param reason reason
     * @param operatorId operator
     * @return rows affected
     */
    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("reason") String reason,
                     @Param("operatorId") Long operatorId);

    /**
     * Find audit by id.
     *
     * @param id audit id
     * @return audit
     */
    ContentAudit findById(@Param("id") Long id);
}
