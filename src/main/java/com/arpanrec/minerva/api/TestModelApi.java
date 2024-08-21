package com.arpanrec.minerva.api;

import com.arpanrec.minerva.models.TestModel;
import com.arpanrec.minerva.repositories.TestModelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/testmodel")
public class TestModelApi implements CommandLineRunner {

    public TestModelApi(TestModelRepository testModelRepository) {
        this.testModelRepository = testModelRepository;
    }

    TestModelRepository testModelRepository;

    @PostMapping
    public void save(@RequestBody TestModel testModel) {
        testModelRepository.save(testModel);
    }

    @GetMapping
    public Iterable<TestModel> getAll() {
        return testModelRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public TestModel getById(@PathVariable Long id) {
        return testModelRepository.findById(id).orElse(null);
    }

    @Override
    public void run(String... args) throws Exception {
        TestModel testModel = new TestModel();
        long lastName = "Arpan".chars().sum();
        testModel.setFirstName("Arpan");
        testModel.setLastName(lastName);
        testModelRepository.save(testModel);
    }
}
