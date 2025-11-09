package com.softManager.project_management_system.repository;

import com.softManager.project_management_system.model.TaskDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDetailsRepository extends JpaRepository<TaskDetails,Long> {
}
