package lk.ac.kln.unimart.category.controller;

import lk.ac.kln.unimart.category.entity.Category;
import lk.ac.kln.unimart.category.repository.CategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }
}
