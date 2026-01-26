package com.deliveryapp.catchabite.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.deliveryapp.catchabite.entity.DelivererFeeRule;

// 거리 매칭 rule 찾기 -> distanceM이 min<=d<max(또는 max null) 조건으로 1개를 찾는 것.
public interface DelivererFeeRuleRepository extends JpaRepository<DelivererFeeRule, Long> {
     
    @Query("""
                select r
                from DelivererFeeRule r
                where r.deliverer.delivererId = :delivererId
                and r.activeYn = 'Y'
                and :distanceM >= r.minM
                and (r.maxM is null or :distanceM < r.maxM)
                order by r.minM desc
            """)
    List<DelivererFeeRule> findMatchingRules(Long delivererId, Long distanceM);

    default Optional<DelivererFeeRule> findMatching(Long delivererId, Long distanceM) {
        List<DelivererFeeRule> list = findMatchingRules(delivererId, distanceM);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // (추가/수정시) 구간 overlap 체크용
    // overlap 판정 공식 : '구간이 겹치지 않으려면 r.max <= newMin OR newMax <= r.min 이어야 함'의 반대 조건 
    @Query("""
            select count(r)
            from DelivererFeeRule r
            where r.deliverer.delivererId = :delivererId
            and r.activeYn = 'Y'
            and (:excludeRuleId is null or r.ruleId <> :excludeRuleId)
            and (
                 (r.maxM is null or r.maxM > :newMinM)
                and (:newMaxM is null or r.minM < :newMaxM)
                )
            """)
    long countOverlaps(Long delivererId, Long newMinM, Integer newMaxM, Long excludeRuleId);
}
