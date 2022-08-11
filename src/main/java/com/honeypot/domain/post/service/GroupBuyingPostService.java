package com.honeypot.domain.post.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.file.FileUploadService;
import com.honeypot.domain.file.PostFileUploadRequest;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import com.honeypot.domain.post.dto.*;
import com.honeypot.domain.post.entity.GroupBuyingPost;
import com.honeypot.domain.post.entity.UsedTradePost;
import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import com.honeypot.domain.post.mapper.GroupBuyingPostMapper;
import com.honeypot.domain.post.repository.GroupBuyingPostQuerydslRepository;
import com.honeypot.domain.post.repository.GroupBuyingPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class GroupBuyingPostService implements PostCrudService<GroupBuyingPostDto, GroupBuyingPostUploadRequest> {

    private final GroupBuyingPostQuerydslRepository groupBuyingPostQuerydslRepository;

    private final GroupBuyingPostMapper groupBuyingPostMapper;

    private final GroupBuyingPostRepository groupBuyingPostRepository;

    private final MemberRepository memberRepository;

    private final FileUploadService fileUploadService;

    @Override
    public PostType getPostType() {
        return PostType.GROUP_BUYING;
    }

    @Transactional(readOnly = true)
    public Page<GroupBuyingPostDto> pageList(Pageable pageable, Long memberId) {
        Page<GroupBuyingPostDto> result = groupBuyingPostQuerydslRepository
                .findAllPostWithCommentAndReactionCount(pageable, memberId);
        return new PageImpl<>(
                result.getContent(),
                pageable,
                result.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public GroupBuyingPostDto find(@NotNull Long postId, Long memberId) {
        GroupBuyingPost post = groupBuyingPostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);
        return groupBuyingPostQuerydslRepository.findPostDetailById(postId, memberId);
    }

    @Transactional
    @Validated(InsertContext.class)
    public GroupBuyingPostDto upload(@Valid GroupBuyingPostUploadRequest request) {
        request.setGroupBuyingStatus(TradeStatus.ONGOING.toString());
        GroupBuyingPost created = groupBuyingPostRepository.save(groupBuyingPostMapper.toEntity(request));

        long writerId = created.getWriter().getId();
        Member writer = memberRepository
                .findById(writerId)
                .orElseThrow(EntityNotFoundException::new);

        GroupBuyingPostDto result = groupBuyingPostMapper.toDto(created);
        result.getWriter().setNickname(writer.getNickname());

        List<PostFileUploadRequest> attachedFiles = request.getAttachedFiles();
        if (attachedFiles != null) {
            List<AttachedFileResponse> files = attachedFiles.stream()
                    .filter(PostFileUploadRequest::isWillBeUploaded)
                    .peek(f -> f.setLinkPostId(result.getPostId()))
                    .map(fileUploadService::linkFileWithPost)
                    .toList();

            result.setAttachedFiles(files);
        }

        return result;
    }

    @Transactional
    @Validated(InsertContext.class)
    public GroupBuyingPostDto update(Long postId, @Valid GroupBuyingPostUploadRequest uploadRequest) {
        GroupBuyingPost post = groupBuyingPostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if (!post.getWriter().getId().equals(uploadRequest.getWriterId())) {
            throw new InvalidAuthorizationException();
        }

        post.setTitle(uploadRequest.getTitle());
        post.setContent(uploadRequest.getContent());
        post.setGoodsPrice(uploadRequest.getGoodsPrice());
        post.setCategory(uploadRequest.getCategory());
        post.setGroupBuyingStatus(GroupBuyingStatus.valueOf(uploadRequest.getGroupBuyingStatus()));
        post.setChatRoomLink(uploadRequest.getChatRoomLink());
        post.setDeadline(uploadRequest.getDeadline());

        return groupBuyingPostMapper.toDto(groupBuyingPostRepository.save(post));
    }

    @Transactional
    @Validated(InsertContext.class)
    public GroupBuyingPostDto updateTradeStatus(@NotNull Long postId, @Valid GroupBuyingModifyRequest request) {
        GroupBuyingPost post = groupBuyingPostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if (!post.getWriter().getId().equals(request.getWriterId())) {
            throw new InvalidAuthorizationException();
        }

        post.setGroupBuyingStatus(GroupBuyingStatus.valueOf(request.getGroupBuyingStatus()));

        return groupBuyingPostMapper.toDto(groupBuyingPostRepository.save(post));
    }

    @Transactional
    public void delete(Long postId, @NotNull Long memberId) {
        GroupBuyingPost post = groupBuyingPostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if (!post.getWriter().getId().equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        groupBuyingPostRepository.delete(post);
    }

}
