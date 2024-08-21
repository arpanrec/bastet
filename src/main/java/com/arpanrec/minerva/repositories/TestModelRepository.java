package com.arpanrec.minerva.repositories;

import com.arpanrec.minerva.models.TestModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestModelRepository extends JpaRepository<TestModel, Long> {
}
