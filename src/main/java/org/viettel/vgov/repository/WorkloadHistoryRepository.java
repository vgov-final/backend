package org.viettel.vgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.viettel.vgov.model.WorkloadHistory;

import java.util.List;

@Repository
public interface WorkloadHistoryRepository extends JpaRepository<WorkloadHistory, Long> {
    List<WorkloadHistory> findByProjectMember_IdOrderByChangeTimestampDesc(Long projectMemberId);
}