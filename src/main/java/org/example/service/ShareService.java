package org.example.service;

import org.example.dto.ShareDto;
import java.util.List;

public interface ShareService {
    void shareArticle(Long articleId, ShareDto shareDto, String username);
    List<String> getArticleShares(Long articleId);
    int getArticleShareCount(Long articleId);
}
