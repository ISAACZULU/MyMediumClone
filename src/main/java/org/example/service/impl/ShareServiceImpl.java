package org.example.service.impl;

import org.example.dto.ShareDto;
import org.example.entity.Article;
import org.example.entity.Share;
import org.example.entity.User;
import org.example.repository.ArticleRepository;
import org.example.repository.ShareRepository;
import org.example.repository.UserRepository;
import org.example.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {
    
    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @Override
    @Transactional
    public void shareArticle(Long articleId, ShareDto shareDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        Share share = new Share();
        share.setArticle(article);
        share.setUser(user);
        share.setShareType(shareDto.getShareType());
        share.setShareMessage(shareDto.getShareMessage());
        
        shareRepository.save(share);
    }

    @Override
    public List<String> getArticleShares(Long articleId) {
        List<Share> shares = shareRepository.findByArticleId(articleId);
        return shares.stream()
                .map(share -> share.getShareMessage())
                .collect(Collectors.toList());
    }

    @Override
    public int getArticleShareCount(Long articleId) {
        return shareRepository.countByArticleId(articleId).intValue();
    }
}
