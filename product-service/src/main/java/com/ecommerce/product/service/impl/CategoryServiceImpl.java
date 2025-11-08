package com.ecommerce.product.service.impl;

import com.ecommerce.library.exception.DuplicateException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.ReqCreateCategoryDTO;
import com.ecommerce.product.dto.ReqUpdateCategoryDTO;
import com.ecommerce.product.dto.ReqUpdateCategoryStatusDTO;
import com.ecommerce.product.dto.ResCategoryDTO;
import com.ecommerce.product.dto.ResCategorySearchDTO;
import com.ecommerce.product.entity.Category;
import com.ecommerce.library.enumeration.CategoryStatus;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public void createCategory(ReqCreateCategoryDTO request) {
        categoryRepository.findByCategoryName(request.getName())
                .ifPresent(category -> {
                    throw new DuplicateException(MessageError.CATEGORY_NAME_EXISTS);
                });

        Category category = Category.builder()
                .categoryName(request.getName())
                .description(request.getDescription())
                .categoryStatus(CategoryStatus.ACTIVE)
                .build();

        if (FnCommon.isNotNull(request.getParentCategoryId())) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new NotFoundException(MessageError.CATEGORY_NOT_FOUND));
            category.setParentCategory(parentCategory);
        }

        categoryRepository.save(category);
    }

    @Override
    public ResCategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(MessageError.CATEGORY_NOT_FOUND));
        List<ResCategoryDTO> childCategories = category.getSubCategories().stream()
                .map(childCategory -> ResCategoryDTO.builder()
                        .categoryId(childCategory.getCategoryId())
                        .categoryName(childCategory.getCategoryName())
                        .description(childCategory.getDescription())
                        .categoryStatus(childCategory.getCategoryStatus())
                        .createdAt(childCategory.getCreatedAt())
                        .updatedAt(childCategory.getUpdatedAt())
                        .build())
                .toList();
        Category parentCategory = category.getParentCategory();
        return ResCategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .categoryStatus(category.getCategoryStatus())
                .subCategories(childCategories)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .countChildren(childCategories.size())
                .parentCategory(parentCategory != null ? ResCategoryDTO.builder()
                        .categoryId(parentCategory.getCategoryId())
                        .categoryName(parentCategory.getCategoryName())
                        .description(parentCategory.getDescription())
                        .categoryStatus(parentCategory.getCategoryStatus())
                        .createdAt(parentCategory.getCreatedAt())
                        .updatedAt(parentCategory.getUpdatedAt())
                        .build() : null)
                .build();
    }



    @Override
    public void updateCategory(Long categoryId, ReqUpdateCategoryDTO request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(MessageError.CATEGORY_NOT_FOUND));

        if (!category.getCategoryName().equals(request.getCategoryName())) {
            categoryRepository.findByCategoryName(request.getCategoryName())
                    .ifPresent(existingCategory -> {
                        throw new DuplicateException(MessageError.CATEGORY_NAME_EXISTS);
                    });

        }
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());

        if (FnCommon.isNotNull(request.getParentCategoryId())) {
            if(categoryRepository.isValidParentCategory(category.getCategoryId(), request.getParentCategoryId())>0) {
                throw new IllegalArgumentException(MessageError.INVALID_PARENT_CATEGORY);
            }
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new NotFoundException(MessageError.CATEGORY_NOT_FOUND));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        categoryRepository.save(category);
    }

    @Override
    public void updateCategoryStatus(Long categoryId, ReqUpdateCategoryStatusDTO request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(MessageError.CATEGORY_NOT_FOUND));

        category.setCategoryStatus(request.getCategoryStatus());
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ResCategorySearchDTO> searchCategories(String keyword, CategoryStatus status,
                                                               int pageNo, int pageSize,
                                                               String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Category> categoriesPage = categoryRepository.searchCategories(keyword, status, pageable);

        return buildPageResponse(categoriesPage);
    }

    private PageResponse<ResCategorySearchDTO> buildPageResponse(Page<Category> categoriesPage) {
        List<ResCategorySearchDTO> categoryResponses = categoriesPage.getContent().stream()
                .map(this::convertToCategorySearchDTO)
                .collect(Collectors.toList());

        return PageResponse.<ResCategorySearchDTO>builder()
                .pageNo(categoriesPage.getNumber())
                .pageSize(categoriesPage.getSize())
                .totalElements(categoriesPage.getTotalElements())
                .totalPages(categoriesPage.getTotalPages())
                .hasNextPage(categoriesPage.hasNext())
                .hasPreviousPage(categoriesPage.hasPrevious())
                .data(categoryResponses)
                .build();
    }

    private ResCategorySearchDTO convertToCategorySearchDTO(Category category) {
        return ResCategorySearchDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .categoryStatus(category.getCategoryStatus())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .parentCategoryName(category.getParentCategory() != null
                        ? category.getParentCategory().getCategoryName()
                        : null)
                .build();
    }

}

