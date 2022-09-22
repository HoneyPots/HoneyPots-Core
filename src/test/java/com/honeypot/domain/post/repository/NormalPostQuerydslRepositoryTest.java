package com.honeypot.domain.post.repository;

import com.honeypot.config.TestConfig;
import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.comment.repository.CommentRepository;
import com.honeypot.domain.file.File;
import com.honeypot.domain.file.FileRepository;
import com.honeypot.domain.file.FileType;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.entity.NormalPost;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.reaction.entity.PostReaction;
import com.honeypot.domain.reaction.entity.Reaction;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import com.honeypot.domain.reaction.repository.PostReactionRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({"test", "aws"})
@DataJpaTest
@Import(TestConfig.class)
class NormalPostQuerydslRepositoryTest {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NormalPostRepository normalPostRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostReactionRepository postReactionRepository;

    @Autowired
    private FileRepository fileRepository;

    private NormalPostQuerydslRepository repository;

    @BeforeEach
    private void before() {
        repository = new NormalPostQuerydslRepository(jpaQueryFactory, "http://aws.com");
    }

    @Test
    void findAllPostWithCommentAndReactionCount() {
        // Arrange
        int page = 0;
        int size = 10;
        int postCount = 15;
        int pageCount = postCount / size + (postCount % size == 0 ? 0 : 1);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        Member writer = addMember();
        for (int i = 0; i < postCount; i++) {
            NormalPost post = addNormalPost(writer, null);
            addComment(post, writer);
            addLikeReactionToPost(post, writer);
        }

        // Act
        Page<NormalPostDto> result = repository.findAllPostWithCommentAndReactionCount(pageable, writer.getId());

        // Assert
        assertEquals(size, result.getNumberOfElements());
        assertEquals(postCount, result.getTotalElements());
        assertEquals(pageCount, result.getTotalPages());

        List<NormalPostDto> postList = result.getContent();
        for (NormalPostDto post : postList) {
            assertNotNull(post);
            assertEquals(writer.getId(), post.getWriter().getId());
            assertEquals(writer.getNickname(), post.getWriter().getNickname());
            assertNotNull(post.getUploadedAt());
            assertNotNull(post.getLastModifiedAt());
            assertEquals(1, post.getCommentCount());
            assertEquals(1, post.getLikeReactionCount());
            assertNotNull(post.getLikeReactionId());
            assertTrue(post.getIsLiked());
        }
    }

    @Test
    void findAllPostWithCommentAndReactionCount_InvalidSortProperty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("invalid")));
        Member writer = addMember();
        addNormalPost(writer, null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            repository.findAllPostWithCommentAndReactionCount(pageable, writer.getId());
        });
    }

    @Test
    void findAllPostWithCommentAndReactionCountByMemberId() {
        // Arrange
        int page = 0;
        int size = 10;
        int myPostCount = 2;
        int postCount = 5;
        int pageCount = myPostCount / size + (myPostCount % size == 0 ? 0 : 1);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        Member writer = addMember();
        Member anotherMember = addMember(1L);
        for (int i = 0; i < postCount; i++) {
            if (i < myPostCount) {
                addNormalPost(writer, null);
            } else {
                addNormalPost(anotherMember, null);
            }
        }

        // Act
        Page<NormalPostDto> result = repository.findAllPostWithCommentAndReactionCountByMemberId(pageable, writer.getId());

        // Assert
        assertEquals(myPostCount, result.getNumberOfElements());
        assertEquals(myPostCount, result.getTotalElements());
        assertEquals(pageCount, result.getTotalPages());

        List<NormalPostDto> myPostList = result.getContent();
        for (NormalPostDto post : myPostList) {
            assertNotNull(post);
            assertEquals(writer.getId(), post.getWriter().getId());
            assertEquals(writer.getNickname(), post.getWriter().getNickname());
            assertEquals(0, post.getCommentCount());
            assertEquals(0, post.getLikeReactionCount());
            assertNull(post.getLikeReactionId());
            assertFalse(post.getIsLiked());
            assertNotNull(post.getUploadedAt());
            assertNotNull(post.getLastModifiedAt());
        }
    }

    @Test
    void findPostDetailById() {
        // Arrange
        Member writer = addMember();
        int attachedFileCount = 5;
        List<File> attachedFiles = new ArrayList<>();
        for (int i = 0; i < attachedFileCount; i++) {
            attachedFiles.add(addAttachFile());
        }

        NormalPost post = addNormalPost(writer, attachedFiles);

        for (File file : attachedFiles) {
            file.setPost(post);
        }

        // Act
        NormalPostDto result = repository.findPostDetailById(post.getId(), writer.getId());

        // Assert
        assertNotNull(result);
        assertEquals(post.getId(), result.getPostId());
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getContent(), result.getContent());
        assertEquals(0, result.getCommentCount());
        assertEquals(0, result.getLikeReactionCount());
        assertNull(result.getLikeReactionId());
        assertFalse(result.getIsLiked());
        assertEquals(attachedFiles.get(0).getId(), result.getThumbnailImageFile().getFileId());
        for (int i = 0; i < attachedFiles.size(); i++) {
            assertEquals(attachedFiles.get(i).getId(), result.getAttachedFiles().get(i).getFileId());
        }
        assertNotNull(result.getUploadedAt());
        assertNotNull(result.getLastModifiedAt());
    }

    private Member addMember() {
        return addMember(0L);
    }

    private Member addMember(Long id) {
        Member member = Member.builder()
                .nickname("M" + Timestamp.valueOf(LocalDateTime.now()).getTime() + id)
                .build();
        return memberRepository.save(member);
    }

    private NormalPost addNormalPost(Member writer, List<File> attachedFiles) {
        NormalPost normalPost = NormalPost.builder()
                .title("제목")
                .content("내용")
                .attachedFiles(attachedFiles)
                .writer(writer)
                .build();

        return normalPostRepository.save(normalPost);
    }

    private Comment addComment(Post post, Member writer) {
        Comment comment = Comment.builder()
                .content("댓글 내용")
                .writer(writer)
                .post(post)
                .build();

        return commentRepository.save(comment);
    }

    private Reaction addLikeReactionToPost(Post post, Member writer) {
        PostReaction reaction = PostReaction.builder()
                .post(post)
                .reactor(writer)
                .reactionType(ReactionType.LIKE)
                .build();

        return postReactionRepository.save(reaction);
    }

    private File addAttachFile() {
        String originalFilename = "original.png";
        String filename = Timestamp.valueOf(LocalDateTime.now()).getTime() + "_" + originalFilename;
        File file = File.builder()
                .filePath("/img/normal" + filename)
                .originalFilename(originalFilename)
                .filename(filename)
                .fileType(FileType.NORMAL_POST_IMAGE)
                .build();

        return fileRepository.save(file);
    }

}