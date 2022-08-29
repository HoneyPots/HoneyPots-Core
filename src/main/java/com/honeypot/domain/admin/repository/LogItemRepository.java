package com.honeypot.domain.admin.repository;

import com.honeypot.domain.admin.dto.ActiveUsersKpiResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogItemRepository {

    private final EntityManager entityManager;

    public List getActiveUsersKpi(LocalDate fromDate, LocalDate toDate) {
        return entityManager.createNativeQuery(
                        """
                                SELECT date, GROUP_CONCAT(memberId) AS ids, COUNT(*) AS count 
                                  FROM ( 
                                        SELECT SUBSTR(log_date_time, 0 ,10) AS date, 
                                               CONVERT(memberId, signed) AS memberId, 
                                               COUNT(*) AS cnt 
                                          FROM ( 
                                                SELECT 
                                                       DATEADD('hour', 9, log_date_time) AS log_date_time, 
                                                       SUBSTR(SUBSTR(message, 29), 0, INSTR(SUBSTR(message, 29), '''') -1) AS memberId, 
                                                       SUBSTR(message, INSTR(message, 'uri:')+5) AS uri 
                                                  FROM log_item 
                                                 WHERE logger = 'c.h.common.filter.AuthenticationFilter' 
                                                       AND message LIKE 'Authentication information%' 
                                                       AND DATEADD('hour', 9, log_date_time) BETWEEN :fromDate AND :toDate 
                                               ) A 
                                         GROUP BY SUBSTR(log_date_time, 0 ,10), memberId  
                                         ORDER BY memberId, date, cnt DESC 
                                       ) B 
                                 GROUP BY date 
                                 ORDER BY date 
                                 """
                )
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(ActiveUsersKpiResponse.class))
                .getResultList();
    }

}
