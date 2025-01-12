package efub.session.blog.board.service;


import efub.session.blog.account.domain.Account;
import efub.session.blog.account.service.AccountService;
import efub.session.blog.board.domain.Post;
import efub.session.blog.board.dto.request.PostModifyRequestDto;
import efub.session.blog.board.dto.request.PostRequestDto;
import efub.session.blog.board.exception.NotExistPostException;

import efub.session.blog.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final AccountService accountService;

    @Transactional
    public Post addPost(PostRequestDto postRequestDto) {
        Account writer = accountService.findAccountById(postRequestDto.getAccountId());
        return postRepository.save(
                Post.builder()
                        .title(postRequestDto.getTitle())
                        .content(postRequestDto.getContent())
                        .writer(writer)
                        .build()
        );
    }


    @Transactional(readOnly = true)
    public List<Post> findPostList() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Post> findByWriter(Long accountId) {
        Account account = accountService.findAccountById(accountId);
        return postRepository.findByWriter(account);
    }


    public Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(NotExistPostException::new);
    }

    @Transactional
    public void removePost(Long postId, Long accountId) {
        Post post = postRepository.findByPostIdAndWriter_AccountId(postId, accountId)
                .orElseThrow(NotExistPostException::new);
        postRepository.delete(post);

    }

    @Transactional
    public Post modifyPost(Long postId, PostModifyRequestDto requestDto) {
        Post post = postRepository.findByPostIdAndWriter_AccountId(postId, requestDto.getAccountId())
                .orElseThrow(NotExistPostException::new);
        post.updatePost(requestDto);

        return post;

    }
}
