package com.honeypot.domain.post.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.file.FileUploadService;
import com.honeypot.domain.file.PostFileUploadRequest;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import com.honeypot.domain.post.dto.UsedTradeModifyRequest;
import com.honeypot.domain.post.dto.UsedTradePostDto;
import com.honeypot.domain.post.dto.UsedTradePostUploadRequest;
import com.honeypot.domain.post.entity.UsedTradePost;
import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import com.honeypot.domain.post.mapper.UsedTradePostMapper;
import com.honeypot.domain.post.repository.UsedTradePostQuerydslRepository;
import com.honeypot.domain.post.repository.UsedTradePostRepository;
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
public class UsedTradePostService {

    private final UsedTradePostQuerydslRepository usedTradePostQuerydslRepository;

    private final UsedTradePostMapper usedTradePostMapper;

    private final UsedTradePostRepository usedTradePostRepository;

    private final MemberRepository memberRepository;

    private final FileUploadService fileUploadService;

    @Transactional(readOnly = true)
    public Page<UsedTradePostDto> pageList(Pageable pageable, Long memberId) {
        Page<UsedTradePostDto> result = usedTradePostQuerydslRepository
                .findAllPostWithCommentAndReactionCount(pageable, memberId);
        return new PageImpl<>(
                result.getContent(),
                pageable,
                result.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public UsedTradePostDto find(@NotNull Long postId, Long memberId) {
        UsedTradePost usedTradePost = usedTradePostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);
        return usedTradePostQuerydslRepository.findPostDetailById(postId, memberId);
    }

    @Transactional
    @Validated(InsertContext.class)
    public UsedTradePostDto upload(@Valid UsedTradePostUploadRequest request) {
        request.setTradeStatus(TradeStatus.ONGOING.toString());
        UsedTradePost created = usedTradePostRepository.save(usedTradePostMapper.toEntity(request));

        long writerId = created.getWriter().getId();
        Member writer = memberRepository
                .findById(writerId)
                .orElseThrow(EntityNotFoundException::new);

        UsedTradePostDto result = usedTradePostMapper.toDto(created);
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
    public UsedTradePostDto update(Long postId, @Valid UsedTradePostUploadRequest uploadRequest) {
        UsedTradePost usedTradePost = usedTradePostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if (!usedTradePost.getWriter().getId().equals(uploadRequest.getWriterId())) {
            throw new InvalidAuthorizationException();
        }

        usedTradePost.setTitle(uploadRequest.getTitle());
        usedTradePost.setContent(uploadRequest.getContent());
        usedTradePost.setGoodsPrice(uploadRequest.getGoodsPrice());
        usedTradePost.setTradeStatus(TradeStatus.valueOf(uploadRequest.getTradeStatus()));
        usedTradePost.setTradeType(TradeType.valueOf(uploadRequest.getTradeType()));
        usedTradePost.setChatRoomLink(uploadRequest.getChatRoomLink());

        return usedTradePostMapper.toDto(usedTradePostRepository.save(usedTradePost));
    }

    @Transactional
    @Validated(InsertContext.class)
    public UsedTradePostDto updateTradeStatus(@NotNull Long postId, @Valid UsedTradeModifyRequest request) {
        UsedTradePost usedTradePost = usedTradePostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if (!usedTradePost.getWriter().getId().equals(request.getWriterId())) {
            throw new InvalidAuthorizationException();
        }

        usedTradePost.setTradeStatus(TradeStatus.valueOf(request.getTradeStatus()));

        return usedTradePostMapper.toDto(usedTradePostRepository.save(usedTradePost));
    }

    @Transactional
    public void delete(Long postId, @NotNull Long memberId) {
        UsedTradePost usedTradePost = usedTradePostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if (!usedTradePost.getWriter().getId().equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        usedTradePostRepository.delete(usedTradePost);
    }

}
