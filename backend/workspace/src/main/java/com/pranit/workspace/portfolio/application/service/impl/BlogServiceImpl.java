package com.pranit.workspace.portfolio.application.service.impl;

import com.pranit.workspace.portfolio.application.repository.BlogRepository;
import com.pranit.workspace.portfolio.application.repository.TagRepository;
import com.pranit.workspace.portfolio.application.service.BlogService;
import com.pranit.workspace.portfolio.application.specification.EntitySpecification;
import com.pranit.workspace.portfolio.domain.dto.BlogResponse;
import com.pranit.workspace.portfolio.domain.dto.CreateBlogRequest;
import com.pranit.workspace.portfolio.domain.dto.UpdateBlogRequest;
import com.pranit.workspace.portfolio.domain.entity.BlogPost;
import com.pranit.workspace.portfolio.domain.entity.Tag;
import com.pranit.workspace.portfolio.domain.enums.PostStatus;
import com.pranit.workspace.shared.exception.DuplicateResourceException;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import com.pranit.workspace.shared.helper.Pagination;
import com.pranit.workspace.shared.helper.SecurityContext;
import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewBlogPost(final CreateBlogRequest request) {
        if (this.blogRepository.existsByTitleOrSlug(request.title(), request.slug())) {
            throw new DuplicateResourceException("A blog post with this title or slug already exists.");
        }
        final UUID currentUserId = SecurityContext.getCurrentUserId();
        final List<Tag> tags = resolveTags(request.tagIds());
        final BlogPost post = BlogPost.builder()
                .userId(currentUserId)
                .title(request.title())
                .slug(request.slug())
                .excerpt(request.excerpt())
                .contentMarkdown(request.contentMarkdown())
                .publishDate(request.publishDate())
                .status(request.status())
                .tags(new ArrayList<>(tags))
                .externalLink(request.externalLink())
                .displayOrder(request.displayOrder())
                .build();
        this.blogRepository.save(post);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public BlogResponse getBlogById(final @NonNull Long id) {
        final BlogPost post = this.validateAndFetchBlogById(id);
        return BlogResponse.from(post);
    }

    private BlogPost validateAndFetchBlogById(final Long id) {
        return this.blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found."));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<BlogResponse> getBlogs
            (final int page, final int size, final String keyword,
             final PostStatus status, final LocalDate publishDate,
             final String sortBy, final String sortDirection) {
        final Specification<BlogPost> specification = Specification
                .where(EntitySpecification.searchPostKeyword(keyword))
                .and(EntitySpecification.hasPostStatus(status))
                .and(EntitySpecification.hasPublishDateAfter(publishDate))
                .and(EntitySpecification.hasPublishDateBefore(publishDate));
        return Pagination.paginate
                (this.blogRepository, specification, page, size, sortBy, sortDirection, BlogResponse::from);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BlogResponse updateBlogPost(final @NonNull Long id, final UpdateBlogRequest request) {
        final BlogPost post = this.validateAndFetchBlogById(id);
        if (request.title() != null) post.setTitle(request.title());
        if (request.slug() != null) post.setSlug(request.slug());
        if (request.excerpt() != null) post.setExcerpt(request.excerpt());
        if (request.contentMarkdown() != null) post.setContentMarkdown(request.contentMarkdown());
        if (request.publishDate() != null) post.setPublishDate(request.publishDate());
        if (request.status() != null) post.setStatus(request.status());
        if (request.tagIds() != null) {
            final List<Tag> tags = resolveTags(request.tagIds());
            post.getTags().clear();
            post.getTags().addAll(tags);
        }
        if (request.externalLink() != null) post.setExternalLink(request.externalLink());
        if (request.displayOrder() != null) post.setDisplayOrder(request.displayOrder());
        return BlogResponse.from(post);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBlogPost(final @NonNull Long id) {
        final BlogPost post = this.validateAndFetchBlogById(id);
        this.blogRepository.delete(post);
    }

    private List<Tag> resolveTags(final List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        final List<Long> distinctIds = ids.stream().distinct().toList();
        final List<Tag> list = this.tagRepository.findAllById(distinctIds);
        if (list.size() != distinctIds.size()) {
            throw new ResourceNotFoundException("One or more tags were not found");
        }
        return list;
    }
}
