package com.pranit.workspace.portfolio.application.service;

import com.pranit.workspace.portfolio.domain.dto.BlogResponse;
import com.pranit.workspace.portfolio.domain.dto.CreateBlogRequest;
import com.pranit.workspace.portfolio.domain.dto.UpdateBlogRequest;
import com.pranit.workspace.portfolio.domain.enums.PostStatus;
import com.pranit.workspace.shared.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.NonNull;

import java.time.LocalDate;

public interface BlogService {

    void createNewBlogPost(@Valid CreateBlogRequest request);

    BlogResponse getBlogById(@NonNull Long id);

    PageResponse<BlogResponse> getBlogs
            (@Min(0) int page, @Min(1) @Max(5) int size, String keyword, PostStatus status, LocalDate publishDate, String sortBy, String sortDirection);

    BlogResponse updateBlogPost(@NonNull Long id, @Valid UpdateBlogRequest request);

    void deleteBlogPost(@NonNull Long id);
}
