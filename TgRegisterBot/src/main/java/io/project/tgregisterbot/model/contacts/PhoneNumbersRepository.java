package io.project.tgregisterbot.model.contacts;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PhoneNumbersRepository extends CrudRepository<PhoneNumbers, Long> {
    List<PhoneNumbers> findByAddress_Id(Long id);
}