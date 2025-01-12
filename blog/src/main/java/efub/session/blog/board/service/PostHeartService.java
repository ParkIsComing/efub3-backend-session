package efub.session.blog.board.service;

import efub.session.blog.account.domain.Account;
import efub.session.blog.account.service.AccountService;
import efub.session.blog.board.domain.Post;
import efub.session.blog.board.domain.PostHeart;
import efub.session.blog.board.dto.request.AccountInfoRequestDto;
import efub.session.blog.board.repository.PostHeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostHeartService {
    private final PostHeartRepository postHeartRepository;
    private final PostService postService;
    private final AccountService accountService;


    public void create(Long postId, AccountInfoRequestDto requestDto) {
        Post post = postService.findPost(postId);
        Account account = accountService.findAccountById(requestDto.getAccountId());
        if (isExistsByWriterAndPost(account, post)) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }
        PostHeart postHeart = PostHeart.builder()
                .post(post)
                .account(account)
                .build();
        postHeartRepository.save(postHeart);
    }

    public void delete(Long postId, Long accountId) {
        Post post = postService.findPost(postId);
        Account account = accountService.findAccountById(accountId);
        PostHeart postHeart = postHeartRepository.findByWriterAndPost(account, post)
                .orElseThrow(() -> new IllegalArgumentException("좋아요가 존재하지 않습니다."));
        postHeartRepository.delete(postHeart);
    }


    public boolean isHeart(Long accountId, Post post){
        Account account = accountService.findAccountById(accountId);
        return isExistsByWriterAndPost(account, post);
    }

    @Transactional(readOnly = true)
    public boolean isExistsByWriterAndPost(Account account, Post post) {
        return postHeartRepository.existsByWriterAndPost(account, post);
    }



    @Transactional(readOnly = true)
    public Integer countPostHeart(Post post) {
        Integer count = postHeartRepository.countByPost(post);
        return count;
    }

    @Transactional(readOnly = true)
    public List<PostHeart> findByWriter(Account account) {
        return postHeartRepository.findByWriter(account);
    }

    @Transactional(readOnly = true)
    public PostHeart findById(Long postHeartId) {
        return postHeartRepository.findById(postHeartId)
                .orElseThrow(() -> new IllegalArgumentException("해당 좋아요가 없습니다. id=" + postHeartId));
    }
    @Transactional(readOnly = true)
    public List<Post> findLikePostList(List<PostHeart> postLikeList) {
        return postLikeList.stream()
                .map(PostHeart::getPost)
                .collect(Collectors.toList());
    }
}
