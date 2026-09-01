package lk.ac.kln.unimart.category;

import lk.ac.kln.unimart.category.entity.Category;
import lk.ac.kln.unimart.category.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CategoryDataInitializer.class);
    private final CategoryRepository categoryRepository;

    public CategoryDataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        List<String> defaultCategories = List.of(
                "Electronics",
                "Books",
                "Clothing",
                "Furniture",
                "Other"
        );

        for (String name : defaultCategories) {
            categoryRepository.findByNameIgnoreCase(name).ifPresentOrElse(
                    category -> {},
                    () -> {
                        Category category = new Category();
                        category.setName(name);
                        category.setActive(true);
                        categoryRepository.save(category);
                        log.info("Initialized default category: {}", name);
                    }
            );
        }
    }
}
