package efub.session.blog.board.service;

import efub.session.blog.account.domain.Account;
import efub.session.blog.account.service.AccountService;
import efub.session.blog.board.domain.Comment;
import efub.session.blog.board.domain.CommentHeart;
import efub.session.blog.board.domain.Post;
import efub.session.blog.board.domain.PostHeart;
import efub.session.blog.board.dto.request.AccountInfoRequestDto;
import efub.session.blog.board.repository.CommentHeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentHeartService {
    private final CommentService commentService;
    private final CommentHeartRepository commentHeartRepository;


    private final AccountService accountService;

    public void create(Long commentId, AccountInfoRequestDto requestDto) {
        Account account = accountService.findAccountById(requestDto.getAccountId());
        Comment comment = commentService.findById(commentId);
        if (isExistsByWriterAndComment(account, comment)) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }
        CommentHeart commentHeart = CommentHeart.builder()
                .comment(comment)
                .account(account)
                .build();
        commentHeartRepository.save(commentHeart);
    }

    public void delete(Long commentId, Long accountId) {
        Account account = accountService.findAccountById(accountId);
        Comment comment = commentService.findById(commentId);
        CommentHeart commentHeart = commentHeartRepository.findByWriterAndComment(account, comment)
                .orElseThrow(() -> new IllegalArgumentException("해당 좋아요가 없습니다."));
        commentHeartRepository.delete(commentHeart);
    }

    public boolean isHeart(Long accountId, Comment comment){
        Account account = accountService.findAccountById(accountId);
        return isExistsByWriterAndComment(account, comment);
    }
    @Transactional(readOnly = true)
    public CommentHeart findById(Long commentHeartId) {
        return commentHeartRepository.findById(commentHeartId)
                .orElseThrow(() -> new IllegalArgumentException("해당 좋아요가 없습니다. id=" + commentHeartId));
    }

    @Transactional(readOnly = true)
    public boolean isExistsByWriterAndComment(Account account, Comment comment) {
        return commentHeartRepository.existsByWriterAndComment(account, comment);
    }



    @Transactional(readOnly = true)
    public Integer countCommentHeart(Comment comment) {
        Integer count = commentHeartRepository.countByComment(comment);
        return count;
    }

    @Transactional(readOnly = true)
    public List<CommentHeart> findByWriter(Account account) {
        return commentHeartRepository.findByWriter(account);
    }

    @Transactional(readOnly = true)
    public List<Post> findLikePostList(List<PostHeart> postLikeList) {
        return postLikeList.stream()
                .map(PostHeart::getPost)
                .collect(Collectors.toList());
    }
}
