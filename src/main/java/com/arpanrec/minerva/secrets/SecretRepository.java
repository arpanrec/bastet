package com.arpanrec.minerva.secrets;

import com.arpanrec.minerva.api.Secrets;
import org.springframework.data.repository.CrudRepository;

public interface SecretRepository extends CrudRepository<Secrets, Long> {

}
